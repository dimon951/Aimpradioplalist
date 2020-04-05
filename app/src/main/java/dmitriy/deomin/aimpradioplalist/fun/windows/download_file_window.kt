package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.download_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

fun download_file_window(context:Context,name:String,url:String){
    val dw = DialogWindow(context, R.layout.dialog_delete_stancii, true)
    val dw_start = dw.view().findViewById<Button>(R.id.button_dialog_delete)
    val dw_no = dw.view().findViewById<Button>(R.id.button_dialog_no)
    val dw_logo = dw.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
    val dw_progres = dw.view().findViewById<ProgressBar>(R.id.progressBar)
    dw_progres.visibility = View.VISIBLE


    Slot(context, "dw_progres").onRun {
        val totalBytes = it.getStringExtra("totalBytes")
        val readBytes = it.getStringExtra("readBytes")
        dw_progres.max = totalBytes.toInt()
        dw_progres.progress = readBytes.toInt()

        if (totalBytes == readBytes) {
            if (totalBytes == "0") {
                dw_logo.text = "Отменено,попробовать еще раз?"
                dw_no.text = "Нет"
                dw_start.visibility = View.VISIBLE
            } else {
                dw_logo.text = "Готово,сохранено в папке программы"
                dw_start.visibility = View.VISIBLE
                dw_start.text = "Открыть файл"
                dw_no.text = "Нет"
            }
        }
    }

    dw_logo.text = "Попробовать скачать?\n(не работает для потока)"

    dw_start.onClick {
        if (dw_logo.text == "Готово,сохранено в папке программы") {
            //попробуем его открыть
            play_aimp_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
            dw.close()
        } else {
            dw_start.visibility = View.GONE
            dw_logo.text = "Идёт загрузка..."
            dw_no.text = "Отмена"
            download_file(url, name + "." + url.substringAfterLast('.'), "anim_online_plalist")
        }
    }
    dw_start.onLongClick {
        if (dw_logo.text == "Готово,сохранено в папке программы") {
            play_system_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
            dw.close()
        }
    }
    dw_no.onClick {
        if (dw_no.text == "Отмена") {
            signal("dw_cansel").send(context)
        } else {
            dw.close()
        }
    }
}