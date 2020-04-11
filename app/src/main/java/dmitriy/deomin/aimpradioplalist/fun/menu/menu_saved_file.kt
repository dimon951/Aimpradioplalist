package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.add_myplalist
import dmitriy.deomin.aimpradioplalist.`fun`.file.getDirSize
import dmitriy.deomin.aimpradioplalist.`fun`.file.long_size_to_good_vid
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.toast
import java.io.File

fun menu_saved_file(context: Context, file: String, info: Boolean = true) {
    val dw = DialogWindow(context, R.layout.menu_saved_file)

    //Если файл есть посмотрим его вес
    val size_file = long_size_to_good_vid(getDirSize(File(file)).toDouble())

    dw.view().findViewById<TextView>(R.id.logo_menu_saved_file).text = file
    dw.view().findViewById<TextView>(R.id.tetx_info_file).text = size_file

    if (info)
        dw.view().findViewById<TextView>(R.id.text_poesnalka).visibility = View.VISIBLE
    else
        dw.view().findViewById<TextView>(R.id.text_poesnalka).visibility = View.GONE

    dw.view().findViewById<Button>(R.id.button_del).onClick {

        val d = DialogWindow(context, R.layout.dialog_delete_stancii)

        d.view().findViewById<TextView>(R.id.text_voprosa_del_stncii).text = "Удалить $file ($size_file) ?"

        val da = d.view().findViewById<Button>(R.id.button_dialog_delete)
        da.onClick {
            d.close()
            if (File(file).delete()) {
                context.toast("Готово")
                dw.close()
            } else {
                context.toast("Неудачно")
            }
        }
        d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
            d.close()
        }

    }
    dw.view().findViewById<Button>(R.id.button_add_plalist).onClick {
        add_myplalist(File(file).name, file)
        dw.close()
    }

    dw.view().findViewById<Button>(R.id.open_custom_plaer).onClick {
        Play_audio(File(file).name, file)
        dw.close()
    }
    dw.view().findViewById<Button>(R.id.button_open_aimp).onClick {
        play_aimp_file(file)
        dw.close()
    }
    dw.view().findViewById<Button>(R.id.button_open_aimp).onLongClick {
        play_system_file(file)
        dw.close()
    }
    dw.view().findViewById<Button>(R.id.button_cshre).onClick {
        signal("Main_update")
                .putExtra("signal","send_mp3")
                .putExtra("pach_mp3_file",file).send(context)
        dw.close()
    }
}