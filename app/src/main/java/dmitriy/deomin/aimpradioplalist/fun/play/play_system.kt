package dmitriy.deomin.aimpradioplalist.`fun`.play

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.create_m3u_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File

//тоже если урл не пустой сохраним файл с сылкой и попытаемся открыть в системе
//иначе проверяем файл и пробуем его открыть
fun play_system(name: String, url: String) {

    if (url != "") {
        //приёмник  сигналов
        Slot(Main.context, "File_created").onRun {
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    play_system_file(it.getStringExtra("name"))
                }
                "pizdec" -> {
                    Main.context.toast(Main.context.getString(R.string.error))
                    //запросим разрешения
                    EbuchieRazreshenia()
                }

            }
        }
        //результат выполнения ждёт слот "File_created"
        create_m3u_file(name, arrayListOf(Radio(name = name, url = url)))
    } else {

        //проверим что файл есть
        val f_old = File(name)
        if (f_old.exists()) {
            //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется
            val msf = DialogWindow(Main.context, R.layout.name_save_file)

            //покажем оконо в котором нужно будет ввести имя
            val vname = msf.view().findViewById<EditText>(R.id.edit_new_name)
            vname.typeface = Main.face
            vname.textColor = Main.COLOR_TEXT
            vname.setText(f_old.name.replace(".m3u", ""))
            vname.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                    val n = File(f_old.parent + "/" + text + ".m3u")
                    if (n.exists()) {
                        //если такой файл есть будем подкрашивать красным
                        vname.textColor = Color.RED
                    } else {
                        vname.textColor = Main.COLOR_TEXT
                    }
                }
            })

            val save_buttten = msf.view().findViewById<Button>(R.id.button_save)
            save_buttten.onClick {
                //----
                if (vname.text.toString().isEmpty()) {
                    Main.context.toast("Введите имя")
                } else {

                    //переименовываем
                    val f_new = File(f_old.parent + "/" + vname.text + ".m3u")
                    val otvet: File
                    otvet = if (f_new.name == f_old.name) {
                        f_old
                    } else {
                        f_old.copyTo(f_new, true)
                    }
                    if (otvet.isFile) {
                        //если переименовалось откроем его
                        val i = Intent(Intent.ACTION_VIEW)
                        i.setDataAndType(Uri.parse("file://" + f_new.absolutePath), "audio/mpegurl")
                        //проверим есть чем открыть или нет
                        if (i.resolveActivity(Main.context.packageManager) != null) {
                            Main.context.startActivity(i)
                        } else {
                            Main.context.toast("Системе не удалось ( ")
                        }

                    } else {
                        Main.context.toast("Не получилось переименовать")
                    }
                    //закроем окошко
                    msf.close()
                }
            }

        } else {
            Main.context.toast("Куда-то пропал файл (")
        }

    }
}