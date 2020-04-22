package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.file.is_existence_file
import dmitriy.deomin.aimpradioplalist.`fun`.isValidURL
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.download_i_open_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.`fun`.putText_сlipboard
import dmitriy.deomin.aimpradioplalist.`fun`.share_text
import dmitriy.deomin.aimpradioplalist.custom.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

fun menu_my_list(context: Context, radio: Radio, data:ArrayList<Radio>,p1:Int){
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
                    "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
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
                        "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
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
                        "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
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
    val play_custom = empid.view().findViewById<Button>(R.id.open_custom_plaer)
    val loadlist = empid.view().findViewById<Button>(R.id.loadlist)


    //если текуший элемент список ссылок
    if (radio.name.contains("<List>")) {
        //скроем кнопки открытия в плеере
        playAimp.visibility = View.GONE
        play_custom.visibility = View.GONE
        //покажем кнопку загрузки списка
        loadlist.visibility = View.VISIBLE

    } else {
        //иначе покажем
        playAimp.visibility = View.VISIBLE
        play_custom.visibility = View.VISIBLE
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
    playAimp.onLongClick {
        //закрываем основное окошко
        empid.close()
        play_system(radio.name, radio.url)
    }
    //открыть в своим ебучим плеером
    play_custom.onClick {
        if (is_existence_file(radio.url)) {
            menu_saved_file(context, radio.url)
        } else {
            if(isValidURL(radio.url)){
                Play_audio(radio.name, radio.url, context = context)
            }else{
                context.toast("Возможно ссылка битая, нельзя открыть")
            }
        }
        //закрываем основное окошко
        empid.close()
    }

    //поделится
    (empid.view().findViewById<Button>(R.id.shareaimp_my_list_one)).onClick {
        //закрываем основное окошко
        empid.close()
        share_text(radio.name + "\n" + radio.url)
    }

    //загрузить список
    loadlist.onClick {
        //закрываем основное окошко
        empid.close()
        download_i_open_m3u_file(radio.url, "anim_my_list")
    }
}