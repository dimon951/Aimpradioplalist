package dmitriy.deomin.aimpradioplalist.`fun`.play

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.is_install_app
import dmitriy.deomin.aimpradioplalist.`fun`.menu.menu_setup_aimp
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File

//открыть в аимпе ,если передаётся одна станция то создадим файл с ней и откроем его в аимпе
//если просто имя то значит это уже сохранёный файл ,откроем сразу по имени-адресу с припиской "file://"
//".views.Main.MainActivity"
@SuppressLint("WrongConstant")
fun play_aimp(name: String, url: String) {
    if (url != "") {
        Slot(Main.context, "File_created").onRun {
            //получим данные
            val s = it.getStringExtra("update")
            if (s == "zaebis") {
                //проверим есть ли аимп
                if (is_install_app("com.aimp.player")) {
                    //откроем файл с сылкой в плеере
                    play_aimp_file(it.getStringExtra("name"))
                } else {
                    //иначе предложим системе открыть или установить аимп
                    menu_setup_aimp(url, name)
                }
            } else {
                Main.context.toast(Main.context.getString(R.string.error))
                //запросим разрешения
                EbuchieRazreshenia()
            }
        }
        create_m3u_file(name, arrayListOf(Radio(name = name, url = url)))
    } else {
        //если юрл пуст значит передали список что открыть
        //проверим что файл есть
        val f_old = File(name)
        if (f_old.exists()) {
            //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется

            //покажем оконо в котором нужно будет ввести имя
            val nsf = DialogWindow(Main.context, R.layout.name_save_file)

            val vname = nsf.view().findViewById<EditText>(R.id.edit_new_name)
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

            val save_buttten = nsf.view().findViewById<Button>(R.id.button_save)
            save_buttten.onClick {
                //----
                if (vname.text.toString().isEmpty()) {
                    Main.context.toast("Введите имя")
                } else {
                    //если нечего не изменилось
                    if (f_old.name.replace(".m3u", "") == vname.text.toString()) {
                        //проверим есть ли аимп и откроем
                        if (is_install_app("com.aimp.player")) {
                            play_aimp_file(f_old.absolutePath)
                        } else {
                            play_system_file(f_old.absolutePath)
                        }
                    } else {
                        val f_new = File(f_old.parent + "/" + vname.text + ".m3u")
                        f_old.renameTo(f_new)
                        //проверим есть ли аимп и откроем
                        if (is_install_app("com.aimp.player")) {
                            play_aimp_file(f_new.absolutePath)
                        } else {
                            play_system_file(f_new.absolutePath)
                        }
                    }
                    //закроем окошко
                    nsf.close()
                }
            }
        } else {
            Main.context.toast("Куда-то пропал файл (")
        }
    }

}