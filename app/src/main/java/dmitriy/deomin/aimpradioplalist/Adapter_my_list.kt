package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.custom.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class Adapter_my_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_my_list.ViewHolder>() {

    private lateinit var context: Context


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.url_radio)
        val fon = itemView.findViewById<CardView>(R.id.fon_item_radio)

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //настроим вид тутже
        //-----------------------------------------------
        p0.name_radio.typeface = Main.face
        p0.name_radio.textColor = Main.COLOR_TEXT

        p0.url_radio.typeface = Main.face
        p0.url_radio.textColor = Main.COLOR_TEXT

        p0.nomer_radio.typeface = Main.face
        p0.nomer_radio.textColor = Main.COLOR_TEXT
        //-------------------------------------------------------

        //заполним данными
        val radio: Radio = data[p1]
        p0.name_radio.text = radio.name
        p0.url_radio.text = radio.url
        p0.nomer_radio.text = ""


        //обработка нажатий
        p0.itemView.onClick {

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

                    Slot(context, "File_created", false).onRun { it ->
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

                    //поехали ,удаляем и ждём сигналы
                    val file_function = File_function()
                    file_function.Delet_one_potok(selectedItem)
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
            (empid.view().findViewById<Button>(R.id.reneme)).onClick {

                //закрываем основное окошко
                empid.close()

                //показываем окошко ввода нового имени
                val nsf = DialogWindow(context, R.layout.name_save_file)

                val edit = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                edit.typeface = Main.face
                edit.textColor = Main.COLOR_TEXT
                edit.hint = radio.name
                edit.setText(radio.name)

                //переименовываем
                (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                    //проверим на пустоту
                    if (edit.text.toString().isNotEmpty()) {

                        //если все хорошо закрываем окошко ввода имени
                        nsf.close()

                        Slot(context, "File_created", false).onRun { it ->
                            //получим данные
                            val s = it.getStringExtra("update")
                            if (s == "zaebis") {
                                //обновим старницу
                                data.removeAt(p1)
                                data.add(p1, Radio(edit.text.toString(), radio.url))
                                notifyItemChanged(p1)
                            } else {
                                context.toast(context.getString(R.string.error))
                            }
                        }

                        //делаем
                        val file_function = File_function()
                        file_function.Rename_potok(radio.name + "\n" + radio.url, edit.text.toString() + "\n" + radio.url)
                    } else {
                        //закрываем окошко
                        nsf.close()
                        context.toast("Оставим как было")
                    }
                }
            }
        }
    }
}
