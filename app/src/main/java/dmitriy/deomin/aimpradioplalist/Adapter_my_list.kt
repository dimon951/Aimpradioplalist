package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.github.kittinunf.fuel.httpGet
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

class Adapter_my_list(val data: ArrayList<Radio>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_my_list.ViewHolder>(), Filterable {

    private lateinit var context: Context
    var raduoSearchList: ArrayList<Radio> = data

    override fun getFilter(): Filter {


        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    this@Adapter_my_list.raduoSearchList = data
                } else {
                    val filteredList = ArrayList<Radio>()
                    for (row in data) {
                        if (row.name.replace("<List>", "").toLowerCase().contains(charString.toLowerCase())
                                || row.url.toLowerCase().contains(charString.toLowerCase())
                                || row.kbps.toLowerCase().contains(charString.toLowerCase())
                                || row.kategory.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    this@Adapter_my_list.raduoSearchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = this@Adapter_my_list.raduoSearchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                if (filterResults.values != null) {
                    this@Adapter_my_list.raduoSearchList = filterResults.values as ArrayList<Radio>
                    notifyDataSetChanged()
                }
            }
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.user_name_info)
        val fon = itemView.findViewById<androidx.cardview.widget.CardView>(R.id.fon_item_radio)
        val kbps = itemView.findViewById<TextView>(R.id.kbps_radio)
        val ganr = itemView.findViewById<TextView>(R.id.ganr_radio)
        val liner_kbps = itemView.findViewById<LinearLayout>(R.id.liner_kbps)
        val liner_ganr = itemView.findViewById<LinearLayout>(R.id.liner_ganr)
        val liner_url = itemView.findViewById<LinearLayout>(R.id.liner_url)


        //------------------------------------------------------------------------------------
        // коментарии ,лайки, инфо
        val liner_user = itemView.findViewById<LinearLayout>(R.id.liner_user_add_info)
        val user_name = itemView.findViewById<TextView>(R.id.user_name)
        //
        val liner_reiting = itemView.findViewById<LinearLayout>(R.id.liner_reiting)
        val btn_koment = itemView.findViewById<Button>(R.id.button_komenty)
        val btn_dislike = itemView.findViewById<Button>(R.id.button_dislake)
        val btn_like = itemView.findViewById<Button>(R.id.button_like)
        //
        val liner_text_komentov = itemView.findViewById<LinearLayout>(R.id.liner_text_komentov)
        val btn_add_koment = itemView.findViewById<Button>(R.id.btn_add_new_koment)
        val btn_update_koment = itemView.findViewById<Button>(R.id.button_updete_obmenik)
        val text_komentov = itemView.findViewById<TextView>(R.id.text_komentov)
        //-----------------------------------------------------------------------------------
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return this.raduoSearchList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


        //заполним данными(тут в логах бывает падает - обращение к несуществующему элементу)
        //поэтому будем проверять чтобы общее количество было больше текушего номера
        val radio: Radio = if (this.raduoSearchList.size > p1) {
            this.raduoSearchList[p1]
        } else {
            //иначе вернём пустой элемент(дальше будут проверки и он не отобразится)
            Radio("", "", "", "")
        }

        //из названия будем удалять тип ссылки
        p0.name_radio.text = radio.name.replace("<List>", "")


        if (radio.url.isNotEmpty()) {
            p0.liner_url.visibility = View.VISIBLE
            p0.url_radio.text = radio.url
        } else {
            p0.liner_url.visibility = View.GONE
        }

        //нумерация списка
        if (Vse_radio.Numeracia == 1) {
            p0.nomer_radio.text = (p1 + 1).toString() + ". "
        } else {
            p0.nomer_radio.text = ""
        }
        //kbps
        if (radio.kbps.isNotEmpty()) {
            p0.liner_kbps.visibility = View.VISIBLE
            p0.kbps.text = radio.kbps
        } else {
            p0.liner_kbps.visibility = View.GONE
        }
        //ganr
        if (radio.kategory.isNotEmpty()) {
            p0.liner_ganr.visibility = View.VISIBLE
            p0.ganr.text = radio.kategory
        } else {
            p0.liner_ganr.visibility = View.GONE
        }

        //взависимости что сейчас открыто будем показывать или скрывать коменты и прочию инфу
        //если загружен список редактирования
        if(Moy_plalist.open_file==""||Moy_plalist.open_file==Main.MY_PLALIST) {
            //покажем понель пока коментарии откроем
            p0.liner_reiting.visibility = View.GONE
            p0.btn_dislike.visibility = View.GONE
            p0.btn_like.visibility = View.GONE
        }else{
            //покажем понель пока коментарии откроем
            p0.liner_reiting.visibility = View.VISIBLE
            p0.btn_dislike.visibility = View.GONE
            p0.btn_like.visibility = View.GONE
        }

        //-----------коментарии и лайки-----------------------------------------
        //так как вся эта хуйня лежит не в базе у неё нет ид , будем брать урл
        val id = radio.url.replace("\\","").replace("/","")

        p0.text_komentov.setTextIsSelectable(true)

        Slot(context, "load_koment").onRun {
            if (it.getStringExtra("id") == id) {
                val data = it.getParcelableArrayListExtra<Koment>("data")
                p0.btn_koment.text = "Коментарии: " + (if (data.size > 0) {
                    data.size
                } else {
                    0
                })
                //обнулим количество коментов и заново запишем
                p0.text_komentov.text = ""
                var t =""
                for(kom in data.iterator()){
                    t= t+ "\n"+ (if(kom.user_name.isEmpty()){"no_name"}else{kom.user_name})+ ": "+kom.text
                }
                p0.text_komentov.text = t.drop(1)

            }
        }

        p0.btn_koment.onClick {
            if(p0.liner_text_komentov.visibility==View.GONE){
                p0.liner_text_komentov.visibility = View.VISIBLE
            }else{
                p0.liner_text_komentov.visibility =View.GONE
            }
        }
        p0.btn_add_koment.onClick {
            //добавление коментариев
            //-------------------------------------------------------------------------------
            val add_kom = DialogWindow(context, R.layout.add_koment)
            val ed = add_kom.view().findViewById<EditText>(R.id.ed_add_kom)
            ed.typeface = Main.face
            ed.textColor = Main.COLOR_TEXT
            ed.hintTextColor = Main.COLOR_TEXTcontext
            add_kom.view().findViewById<Button>(R.id.btn_ad_kom).onClick {

                if(ed.text.toString().isEmpty()){
                    context.toast("введите текст")
                }else {
                    Slot(context,"add_koment").onRun {
                        if(it.getStringExtra("update")=="zaebis"){
                            add_kom.close()
                            Main.load_koment(id)
                        }else{
                            context.toast("ошибка")
                        }
                    }
                    Main.add_koment(id,ed.text.toString())
                }
            }
            //-------------------------------------------------------------------------------------

        }
        p0.btn_update_koment.onClick {
            //обновить текуший список коментов
            Main.load_koment(id)
        }

        //Загрузим в начале просто количество коментов
        Main.load_koment(id)
        //------------------------------------------------------------------------


        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            //сохраняем позицию текушею списка
            Moy_plalist.position_list = p1

            //если загружен список редактирования
            if(Moy_plalist.open_file==""||Moy_plalist.open_file==Main.MY_PLALIST) {
                //=============================================================================================
                //общее окошко с кнопками удалить,переименовать
                val empid = DialogWindow(context, R.layout.edit_my_plalist_item_dialog)

                //кнопка удалить
                //------------------------------------------------------------------------------
                (empid.view().findViewById<Button>(R.id.del)).onClick {

                    //закрываем основное окошко
                    empid.close()

                    //получаем выбранную строку
                    val selectedItem = radio.name + "\n" + radio.url

                    //покажем окошко с вопросом подтверждения удаления
                    val dds = DialogWindow(context, R.layout.dialog_delete_stancii)

                    (dds.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)).text = "Точно удалить? \n$selectedItem"

                    (dds.view().findViewById<Button>(R.id.button_dialog_delete)).onClick {

                        Slot(context, "File_created", false).onRun {
                            //получим данные
                            when (it.getStringExtra("update")) {
                                //пошлём сигнал пусть мой плейлист обновится
                                "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                "pizdec" -> {
                                    context.toast(context.getString(R.string.error))
                                    //запросим разрешения
                                    Main.EbuchieRazreshenia()
                                }
                            }
                        }

                        //поехали ,удаляем и ждём сигналы
                        val file_function = File_function()
                        file_function.Delet_one_potok(selectedItem,Moy_plalist.open_file)
                        //закроем окошко
                        dds.close()
                    }

                    //кнопка отмены удаления
                    (dds.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
                        //закроем окошко
                        dds.close()
                    }
                }
                //--------------------------------------------------------------------------------------------------

                //кнопка переименовать
                val btn_renem = empid.view().findViewById<Button>(R.id.reneme)
                btn_renem.onClick {

                    //закрываем основное окошко
                    empid.close()

                    //показываем окошко ввода нового имени
                    val nsf = DialogWindow(context, R.layout.name_save_file)

                    val edit = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                    edit.typeface = Main.face
                    edit.textColor = Main.COLOR_TEXT
                    edit.hintTextColor = Main.COLOR_TEXTcontext
                    edit.hint = radio.name
                    edit.setText(radio.name)

                    //переименовываем
                    (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                        //проверим на пустоту
                        if (edit.text.toString().isNotEmpty()) {

                            //если все хорошо закрываем окошко ввода имени
                            nsf.close()

                            Slot(context, "File_created", false).onRun {
                                //получим данные
                                val s = it.getStringExtra("update")
                                when (s) {
                                    //пошлём сигнал пусть мой плейлист обновится
                                    "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                    "pizdec" -> {
                                        context.toast(context.getString(R.string.error))
                                        //запросим разрешения
                                        Main.EbuchieRazreshenia()
                                    }
                                }
                            }

                            //делаем
                            val file_function = File_function()
                            file_function.Rename_potok(radio.name + "\n" + radio.url, edit.text.toString() + "\n" + radio.url,Moy_plalist.open_file)
                        } else {
                            //закрываем окошко
                            nsf.close()
                            context.toast("Оставим как было")
                        }
                    }
                }

                //при долгон нажатии будем копироваь имя в буфер
                btn_renem.onLongClick {
                    Main.putText(radio.name, context)
                    context.toast("Имя скопировано в буфер")
                }

                //покажем кнопку изменить url  и при клике будем предлогать изменить адрес
                //при долгом нажатии будем копировать в буфер
                //--------------------------------------------------------------------------------
                val btn_url = empid.view().findViewById<Button>(R.id.reneme_url)
                btn_url.visibility = View.VISIBLE
                btn_url.onClick {
                    //закрываем основное окошко
                    empid.close()

                    //показываем окошко ввода нового имени
                    val nsf = DialogWindow(context, R.layout.name_save_file)

                    //меняем заголовок окна
                    ((nsf.view().findViewById<TextView>(R.id.textView_vvedite_name))).text = "Изменить URL"

                    val edit = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                    edit.typeface = Main.face
                    edit.textColor = Main.COLOR_TEXT
                    edit.hintTextColor = Main.COLOR_TEXTcontext
                    edit.hint = radio.url
                    edit.setText(radio.url)

                    //переименовываем
                    (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                        //проверим на пустоту
                        if (edit.text.toString().isNotEmpty()) {

                            //если все хорошо закрываем окошко ввода имени
                            nsf.close()

                            Slot(context, "File_created", false).onRun {
                                //получим данные
                                val s = it.getStringExtra("update")
                                when (s) {
                                    //пошлём сигнал пусть мой плейлист обновится
                                    "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                    "pizdec" -> {
                                        context.toast(context.getString(R.string.error))
                                        //запросим разрешения
                                        Main.EbuchieRazreshenia()
                                    }
                                }
                            }

                            //делаем
                            val file_function = File_function()
                            file_function.Rename_potok(radio.name + "\n" + radio.url, radio.name + "\n" + edit.text.toString(),Moy_plalist.open_file)
                        } else {
                            //закрываем окошко
                            nsf.close()
                            context.toast("Оставим как было")
                        }
                    }
                }
                btn_url.onLongClick {
                    Main.putText(radio.url, context)
                    context.toast("Url скопирован в буфер")
                }
                //-------------------------------------------------------------------------


                val playAimp = empid.view().findViewById<Button>(R.id.open_aimp_my_list_one)
                val playSystem = empid.view().findViewById<Button>(R.id.open_aimp_my_list_one)
                val loadlist = empid.view().findViewById<Button>(R.id.loadlist)



                //если текуший элемент список ссылок
                if (radio.name.contains("<List>")) {
                    //скроем кнопки открытия в плеере
                    playAimp.visibility = View.GONE
                    playSystem.visibility = View.GONE
                    //покажем кнопку загрузки списка
                    loadlist.visibility = View.VISIBLE

                } else {
                    //иначе покажем
                    playAimp.visibility = View.VISIBLE
                    playSystem.visibility = View.VISIBLE
                    //скроем кнопку загрузки списка
                    loadlist.visibility = View.GONE
                }

                //открыть в аимп
                playAimp.onClick {
                    //закрываем основное окошко
                    empid.close()
                    Main.play_aimp(radio.name, radio.url)
                }
                //открыть в сстеме
                playSystem.onLongClick {
                    //закрываем основное окошко
                    empid.close()
                    Main.play_system(radio.name, radio.url)
                }

                //поделится
                (empid.view().findViewById<Button>(R.id.shareaimp_my_list_one)).onClick {
                    //закрываем основное окошко
                    empid.close()
                    context.share(radio.name, radio.url)
                }

                //загрузить список
                loadlist.onClick {
                    //закрываем основное окошко
                    empid.close()
                    Main.download_i_open_m3u_file(radio.url,radio.name)
                }
            }
            //если все остальное
            else{


                val mvr = DialogWindow(context, R.layout.menu_vse_radio)

                val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
                val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
                val loadlist = mvr.view().findViewById<Button>(R.id.button_load_list)
                val share = mvr.view().findViewById<Button>(R.id.button_cshre)
                val instal_aimp = mvr.view().findViewById<Button>(R.id.button_instal_aimp)
                val instal_aimp2 = mvr.view().findViewById<Button>(R.id.button_download_yandex_aimp)

                val name = radio.name.replace("<List>","")

                //если aimp установлен скроем кнопку установить аимп
                if (Main.install_app("com.aimp.player")) {
                    instal_aimp.visibility = View.GONE
                    instal_aimp2.visibility = View.GONE
                    open_aimp.visibility = View.VISIBLE
                } else {
                    //если есть магазин покажем и установку через него
                    if (Main.install_app("com.google.android.gms")) {
                        instal_aimp.visibility = View.VISIBLE
                    } else {
                        instal_aimp.visibility = View.GONE
                    }
                    //скачать по ссылке будем показывать всегда
                    instal_aimp2.visibility = View.VISIBLE
                    open_aimp.visibility = View.GONE
                }

                //Имя и урл выбраной станции , при клике будем копировать урл в буфер
                val text_name_i_url = mvr.view().findViewById<TextView>(R.id.textView_vse_radio)
                text_name_i_url.text = name + "\n" + radio.url
                text_name_i_url.onClick {
                    text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                    Main.putText(radio.url, context)
                    context.toast("url скопирован в буфер")
                }


                open_aimp.onLongClick {
                    Main.play_system(name, radio.url)
                }

                open_aimp.onClick {
                    Main.play_aimp(name, radio.url)
                    mvr.close()
                }

                instal_aimp.onClick {
                    context.browse("market://details?id=com.aimp.player")
                }

                instal_aimp2.onClick {
                    context.browse(Main.LINK_DOWLOAD_AIMP)
                }

                add_pls.onClick {
                    Main.add_myplalist(radio.name, radio.url)
                    mvr.close()
                }

                share.onClick {
                    //сосавим строчку как в m3u вайле
                    context.share(radio.name + "\n" + radio.url)
                }
                share.onLongClick {
                    context.email("deomindmitriy@gmail.com", "aimp_radio_plalist",radio.name + "\n" + radio.url)
                }



                //если текуший элемент список ссылок
                if (radio.name.contains("<List>")) {
                    //скроем кнопки открытия в плеере
                    open_aimp.visibility = View.GONE
                    //покажем кнопку загрузки списка
                    loadlist.visibility = View.VISIBLE
                } else {
                    //иначе покажем
                    open_aimp.visibility = View.VISIBLE
                    //скроем кнопку загрузки списка
                    loadlist.visibility = View.GONE
                }

                //загрузить список
                loadlist.onClick {
                    //закрываем основное окошко
                    mvr.close()
                    Main.download_i_open_m3u_file(radio.url,name)
                }
            }
        }
    }
}
