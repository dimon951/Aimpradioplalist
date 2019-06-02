package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
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
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class Adapter_my_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_my_list.ViewHolder>(), Filterable {

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.url_radio)
        val fon = itemView.findViewById<CardView>(R.id.fon_item_radio)
        val kbps = itemView.findViewById<TextView>(R.id.kbps_radio)
        val ganr = itemView.findViewById<TextView>(R.id.ganr_radio)
        val liner_kbps = itemView.findViewById<LinearLayout>(R.id.liner_kbps)
        val liner_ganr = itemView.findViewById<LinearLayout>(R.id.liner_ganr)
        val liner_url = itemView.findViewById<LinearLayout>(R.id.liner_url)
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


        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            //сохраняем позицию текушею списка
            Moy_plalist.position_list = p1

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

                //-----------скачиваем файл (читам его)--------
                GlobalScope.launch {
                    //запустим анимацию
                    signal("Main_update").putExtra("signal","start_anim_my_list").send(context)

                    radio.url.httpGet().responseString { request, response, result ->
                        when (result) {
                            is com.github.kittinunf.result.Result.Failure -> {
                                val ex = result.getException()
                                //если ошибка остановим анимацию и покажем ошибку
                                signal("Main_update").putExtra("signal","stop_anim_my_list").send(context)
                                context.toast(ex.toString())
                            }
                            is com.github.kittinunf.result.Result.Success -> {
                                val data = result.get()

                                if (data.isNotEmpty()) {

                                    val listfile = Main.ROOT+radio.name.replace("<List>","")+".m3u"

                                    //когда прийдёт сигнал что все хорошо обновим плейлист
                                    Slot(context, "File_created", false).onRun {
                                        //получим данные
                                        when (it.getStringExtra("update")) {
                                            "zaebis" -> {
                                                //пошлём сигнал пусть мой плейлист обновится
                                                signal("Data_add")
                                                        .putExtra("update", "zaebis")
                                                        .putExtra("listfile",listfile)
                                                        .send(context)
                                            }
                                            "pizdec" -> {
                                                context.toast(context.getString(R.string.error))
                                                //запросим разрешения
                                                Main.EbuchieRazreshenia()
                                            }
                                        }
                                    }

                                    val file_function=File_function()
                                    //поехали , сохраняем  и ждём сигналы
                                    file_function.SaveFile(listfile,data)
                                }else{
                                    //если нечего нет остановим анимацию и скажем что там пусто
                                    signal("Main_update").putExtra("signal","stop_anim_my_list").send(context)
                                    context.toast("ошибка,пусто")
                                }
                            }
                        }
                    }
                }
                //--------------------------------------------------------
            }
            //=============================================================================================
        }
    }
}
