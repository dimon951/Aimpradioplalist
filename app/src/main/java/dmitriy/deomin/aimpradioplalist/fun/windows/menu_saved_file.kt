package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.file.getDirSize
import dmitriy.deomin.aimpradioplalist.`fun`.file.long_size_to_good_vid
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import java.io.File

fun menu_saved_file(context: Context, file: String, info: Boolean = true) {
    val dw = DialogWindow(context, R.layout.menu_saved_file)

    //Если файл есть посмотрим его вес
    val s = getDirSize(File(file))
    val size_file = long_size_to_good_vid(s.toDouble())

    dw.view().findViewById<TextView>(R.id.logo_menu_saved_file).text = file
    dw.view().findViewById<TextView>(R.id.tetx_info_file).text = size_file

    if (info)
        dw.view().findViewById<TextView>(R.id.text_poesnalka).visibility = View.VISIBLE
    else
        dw.view().findViewById<TextView>(R.id.text_poesnalka).visibility = View.GONE

    dw.view().findViewById<Button>(R.id.button_del)
    dw.view().findViewById<Button>(R.id.button_add_plalist)

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
        dw.close()
    }
}