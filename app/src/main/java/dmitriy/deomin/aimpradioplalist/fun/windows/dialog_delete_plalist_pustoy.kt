package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.file.saveFile
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

fun dialog_delete_plalist_pustoy(context:Context){
    val ddp = DialogWindow(context, R.layout.dialog_delete_plalist)
    val text = ddp.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
    text.text = "Плейлист пуст(ошибка парсинга) очистить файл ?"

    (ddp.view().findViewById<Button>(R.id.button_dialog_delete)).onClick {
        ddp.close()

        Slot(context, "File_created", false).onRun {
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    //пошлём сигнал пусть мой плейлист обновится
                    signal("Data_add").putExtra("update", "zaebis").send(context)
                }
                "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
            }
        }

        //перезапишем и ждём ответа
        saveFile("my_plalist", "")
    }
    (ddp.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
        ddp.close()
    }
}