package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.EditText
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.getText_сlipboard
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.add_may_plalist_stansiy
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

fun add_url_user(context:Context){
    val auu = DialogWindow(context, R.layout.add_url_user)

    val edit = auu.view().findViewById<EditText>(R.id.editText_add_url)
    edit.typeface = Main.face
    edit.textColor = Main.COLOR_TEXT

    val edit_name = auu.view().findViewById<EditText>(R.id.editText_add_url_name)
    edit_name.typeface = Main.face
    edit_name.textColor = Main.COLOR_TEXT

    (auu.view().findViewById<Button>(R.id.button_paste_url_add)).onClick { edit.setText(getText_сlipboard(context)) }

    (auu.view().findViewById<Button>(R.id.button_add_url)).onClick {

        //проверим на пустоту
        if (edit.text.toString().length > 7) {

            //проверим есть ли в начале ссылки http:// или "https://" - ато от неё много чего зависит
            if (edit.text.toString().substring(0, 7) == "http://" || edit.text.toString().substring(0, 8) == "https://") {


                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        "est" -> context.toast("Такая станция уже есть в плейлисте")
                        "zaebis" -> {
                            //пошлём сигнал пусть мой плейлист обновится
                            signal("Data_add").putExtra("update", "zaebis").send(context)
                        }
                        "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
                    }
                }
                val name = if(edit_name.text.toString().isEmpty()){
                    "no_name"
                }else{
                    edit_name.text.toString()
                }
                //делаем
                add_may_plalist_stansiy(edit.text.toString(), name)
                auu.close()
            } else {
                edit.setText("http://" + edit.text.toString())
                context.toast("В начале ссылки потока должна быть http://, добавил , повторите :)")
            }

        } else {
            context.toast("Нечего добавлять")
        }
    }
}