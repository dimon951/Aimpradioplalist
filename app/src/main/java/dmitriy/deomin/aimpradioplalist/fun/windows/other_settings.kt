package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.Vse_radio
import dmitriy.deomin.aimpradioplalist.`fun`.save_read_int
import dmitriy.deomin.aimpradioplalist.`fun`.save_value_int
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick

fun other_settings(){
    val svr = DialogWindow(Main.context, R.layout.other_settings)

    //--------------------------------------------------------------------------------------
    val numeracia = svr.view().findViewById<Button>(R.id.button_seting_number)

    if (Vse_radio.Numeracia == 1) {
        numeracia.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        numeracia.setTypeface(Main.face, Typeface.BOLD)
        numeracia.text = "Нумерация списков включена"
    } else {
        numeracia.paintFlags = 0
        numeracia.typeface = Main.face
        numeracia.text = "Нумерация списков выключена"
    }

    numeracia.onClick {
        if (save_read_int("setting_numer") == 0) {
            save_value_int("setting_numer", 1)
            Vse_radio.Numeracia = 1

            numeracia.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            numeracia.setTypeface(Main.face, Typeface.BOLD)
            numeracia.text = "Нумерация списка включена"
        } else {
            save_value_int("setting_numer", 0)
            Vse_radio.Numeracia = 0

            numeracia.paintFlags = 0
            numeracia.typeface = Main.face
            numeracia.text = "Нумерация списка выключена"
        }
    }
    //---------------------------------------------------------------------------------------------

    val text_size = svr.view().findViewById<Button>(R.id.button_seting_size_text)








}