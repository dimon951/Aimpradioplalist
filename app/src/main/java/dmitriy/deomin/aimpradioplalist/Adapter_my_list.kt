package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.download_i_open_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.custom.*
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
            save_value_int("position_list_my_plalist", p1)
            Moy_plalist.position_list_my_plalist = p1

            //=============================================================================================
            //общее окошко с кнопками удалить,переименовать
            val empid = DialogWindow(context, R.layout.edit_my_plalist_item_dialog)

            //кнопка удалить
            //------------------------------------------------------------------------------
            (empid.view().findViewById<Button>(R.id.del)).onClick {
                //закрываем основное окошко
                empid.close()

                //покажем окошко с вопросом подтверждения удаления
                val dds = DialogWindow(context, R.layout.dialog_delete_stancii)

                (dds.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)).text = "Точно удалить? \n" + radio.name + "\n" + radio.url

                (dds.view().findViewById<Button>(R.id.button_dialog_delete)).onClick {
                    //закроем окошко
                    dds.close()

                    Slot(context, "File_created", false).onRun {
                        //получим данные
                        when (it.getStringExtra("update")) {
                            //пошлём сигнал пусть мой плейлист обновится
                            "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                            "pizdec" -> {
                                context.toast(context.getString(R.string.error))
                                //запросим разрешения
                                EbuchieRazreshenia()
                            }
                        }
                    }

                    //поехали ,удаляем и ждём сигналы
                    if (data.remove(radio)) {
                        create_m3u_file("my_plalist", data)
                    }
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
                            when (it.getStringExtra("update")) {
                                //пошлём сигнал пусть мой плейлист обновится
                                "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                "pizdec" -> {
                                    context.toast(context.getString(R.string.error))
                                    //запросим разрешения
                                    EbuchieRazreshenia()
                                }
                            }
                        }
                        //делаем
                        data[p1] = Radio(name = edit.text.toString(), url = radio.url)
                        create_m3u_file("my_plalist", data)

                    } else {
                        //закрываем окошко
                        nsf.close()
                        context.toast("Оставим как было")
                    }
                }
            }

            //при долгон нажатии будем копироваь имя в буфер
            btn_renem.onLongClick {
                putText_сlipboard(radio.name, context)
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
                            when (it.getStringExtra("update")) {
                                //пошлём сигнал пусть мой плейлист обновится
                                "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                "pizdec" -> {
                                    context.toast(context.getString(R.string.error))
                                    //запросим разрешения
                                    EbuchieRazreshenia()
                                }
                            }
                        }

                        //делаем
                        data[p1] = Radio(name = radio.name, url = edit.text.toString())
                        create_m3u_file("my_plalist", data)
                    } else {
                        //закрываем окошко
                        nsf.close()
                        context.toast("Оставим как было")
                    }
                }
            }
            btn_url.onLongClick {
                putText_сlipboard(radio.url, context)
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
                play_aimp(radio.name, radio.url)
            }
            //открыть в сстеме
            playSystem.onLongClick {
                //закрываем основное окошко
                empid.close()
                play_system(radio.name, radio.url)
            }

            //поделится
            (empid.view().findViewById<Button>(R.id.shareaimp_my_list_one)).onClick {
                //закрываем основное окошко
                empid.close()
                context.share(radio.name + "\n" + radio.url)
            }

            //загрузить список
            loadlist.onClick {
                //закрываем основное окошко
                empid.close()
                download_i_open_m3u_file(radio.url, "anim_my_list")
            }

        }
    }
}
