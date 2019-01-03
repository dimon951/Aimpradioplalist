package dmitriy.deomin.aimpradioplalist

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class Adapter_my_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_my_list.ViewHolder>() {

    private lateinit var context: Context


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
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
        //-------------------------------------------------------

        //заполним данными
        val radio: Radio = data[p1]
        p0.name_radio.text = radio.name
        p0.url_radio.text = radio.url


        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.edit_my_plalist_item_dialog, null)
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()

            //кнопка удалить
            val del = content.findViewById<Button>(R.id.del)
            del.typeface = Main.face
            del.textColor = Main.COLOR_TEXT
            del.onClick {
                del.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                //закроем окошко
                alertDialog.cancel()
                //получаем выбранную строку
                val selectedItem = radio.name + "\n" + radio.url


                // val mas = file_function.My_plalist(Main.MY_PLALIST)


                //покажем окошко с вопросом подтверждения удаления
                val b = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
                val c = LayoutInflater.from(context).inflate(R.layout.dialog_delete_stancii, null)
                b.setView(c)

                val alertDialog_vopros_delete = b.create()
                alertDialog_vopros_delete.show()


                (c.findViewById<View>(R.id.text_voprosa_del_stncii) as TextView).typeface = Main.face
                (c.findViewById<View>(R.id.text_voprosa_del_stncii) as TextView).text = "Точно удалить: \n$selectedItem ?"

                val del_ok = c.findViewById<Button>(R.id.button_dialog_delete)
                del_ok.textColor = Main.COLOR_TEXT
                del_ok.typeface = Main.face
                del_ok.onClick {
                    del_ok.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                    //приёмник  сигналов
                    // фильтр для приёмника
                    val intentFilter = IntentFilter()
                    intentFilter.addAction("File_created")
                    val broadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(c: Context, intent: Intent) {
                            if (intent.action == "File_created") {
                                //получим данные
                                val s = intent.getStringExtra("update")
                                if (s == "zaebis") {
                                    //удаляем в рецикле
                                    //если быстро удвлять по одной то все падает надо гдето чето блокировать
                                    data.removeAt(p1)
                                    notifyItemRemoved(p1)
                                } else {
                                    context.toast(Main.context.getString(R.string.error))
                                }
                                //попробуем уничтожить слушителя
                                context.unregisterReceiver(this)
                            }
                        }
                    }
                    //регистрируем приёмник
                    context.registerReceiver(broadcastReceiver, intentFilter)
                    //поехали ,удаляем и ждём сигналы
                    val file_function = File_function()
                    file_function.Delet_one_potok(selectedItem)
                    //закроем окошко
                    alertDialog_vopros_delete.cancel()
                }

                //кнопка отмены удаления
                val dno = c.findViewById<Button>(R.id.button_dialog_no)
                dno.textColor = Main.COLOR_TEXT
                dno.typeface = Main.face
                dno.onClick {
                    dno.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                    //закроем окошко
                    alertDialog_vopros_delete.cancel()
                }
            }

            //кнопка переименовать
            val renem = content.findViewById<Button>(R.id.reneme)
            renem.typeface = Main.face
            renem.textColor = Main.COLOR_TEXT
            renem.onClick {
                renem.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                //закроем окошко
                alertDialog.cancel()
                //показываем окошко ввода нового имени
                val b = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
                val c = LayoutInflater.from(context).inflate(R.layout.name_save_file, null)
                b.setView(c)
                val alertDialog_reneme = b.create()
                alertDialog_reneme.show()

                val edit = c.findViewById<EditText>(R.id.edit_new_name)
                edit.typeface = Main.face
                edit.textColor = Main.COLOR_TEXT
                edit.hint = radio.name
                edit.setText(radio.name)

                val logo = c.findViewById<TextView>(R.id.textView_vvedite_name)
                logo.typeface = Main.face
                logo.textColor = Main.COLOR_TEXT

                val b_ok = c.findViewById<Button>(R.id.button_save)
                b_ok.typeface = Main.face
                b_ok.textColor = Main.COLOR_TEXT
                b_ok.onClick {
                    b_ok.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                    //закрываем окошко
                    alertDialog_reneme.cancel()
                    //переименовываем

                    //проверим на пустоту
                    if (edit.text.toString().isNotEmpty()) {

                        //фильтр для нашего сигнала
                        val intentFilter = IntentFilter()
                        intentFilter.addAction("File_created")

                        //приёмник  сигналов
                        val broadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(c: Context, intent: Intent) {
                                if (intent.action == "File_created") {
                                    //получим данные
                                    val s = intent.getStringExtra("update")
                                    if (s == "zaebis") {
                                        alertDialog.cancel()
                                        //обновим старницу
                                        data.removeAt(p1)
                                        data.add(p1, Radio(edit.text.toString(), radio.url))
                                        notifyItemChanged(p1)
                                    } else {
                                        context.toast(Main.context.getString(R.string.error))
                                    }
                                    //попробуем уничтожить слушителя
                                    context.unregisterReceiver(this)
                                }
                            }
                        }

                        //регистрируем приёмник
                        context.registerReceiver(broadcastReceiver, intentFilter)


                        //делаем
                        val file_function = File_function()
                        file_function.Rename_potok(radio.name + "\n" + radio.url, edit.text.toString() + "\n" + radio.url)

                        alertDialog.cancel()
                    } else {
                        context.toast("Оставим как было")
                        alertDialog.cancel()
                    }

                }

            }

        }

    }

}
