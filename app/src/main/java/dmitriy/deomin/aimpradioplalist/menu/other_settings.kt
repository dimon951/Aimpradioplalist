package dmitriy.deomin.aimpradioplalist.menu

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Fonts_vibor
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.Vse_radio
import dmitriy.deomin.aimpradioplalist.`fun`.save_read_int
import dmitriy.deomin.aimpradioplalist.`fun`.save_value_int
import dmitriy.deomin.aimpradioplalist.`fun`.windows.setting_size_text_vidgets
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

@SuppressLint("SetTextI18n")
fun other_settings(){
    val svr = DialogWindow(Main.context, R.layout.other_settings)

    svr.view().findViewById<Button>(R.id.button_edit_fonts).onClick {
        Main.context.startActivity<Fonts_vibor>()
        svr.close()
    }

    //--------------------------------------------------------------------------------------
    val numeracia = svr.view().findViewById<Button>(R.id.button_seting_number)

    if (Vse_radio.Numeracia == 1) {
        numeracia.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        numeracia.setTypeface(Main.face, Typeface.BOLD)
        numeracia.text = "Нумерация списков(Включена)"
    } else {
        numeracia.paintFlags = 0
        numeracia.typeface = Main.face
        numeracia.text = "Нумерация списков(Выключена)"
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
    text_size.onClick {
        svr.close()
        setting_size_text_vidgets()
    }

    //----------------------------
    val fullsckrin = svr.view().findViewById<Button>(R.id.button_seting_full_screen)
    if (Main.FULLSCRIN==1) {
        fullsckrin.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        fullsckrin.setTypeface(Main.face, Typeface.BOLD)
        fullsckrin.text = "Во весь экран(Включенно)\nприменятся после перезапуска"
    } else {
        fullsckrin.paintFlags = 0
        fullsckrin.typeface = Main.face
        fullsckrin.text = "Во весь экран(Выключенно)\nприменятся после перезапуска"
    }

    fullsckrin.onClick {
        if (save_read_int("fullsckrin")==-1) {

            save_value_int("fullsckrin", 1)
            Main.FULLSCRIN = 1

            fullsckrin.paintFlags = 0
            fullsckrin.setTypeface(Main.face, Typeface.BOLD)
            fullsckrin.text = "Во весь экран(Включенно)\nприменятся после перезапуска"

        } else {
            save_value_int("fullsckrin", -1)
            Main.FULLSCRIN = -1

            fullsckrin.typeface = Main.face
            fullsckrin.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            fullsckrin.text = "Во весь экран(Выключенно)\nприменятся после перезапуска"
        }
    }

    val navbutton = svr.view().findViewById<Button>(R.id.button_seting_navbutton)
    if (Main.NAVBUTTON==1) {
        navbutton.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        navbutton.setTypeface(Main.face, Typeface.BOLD)
        navbutton.text = "Скрыть кнопки навигации(Включенно)\nприменятся после перезапуска"
    } else {
        navbutton.paintFlags = 0
        navbutton.typeface = Main.face
        navbutton.text = "Скрыть кнопки навигации(Выключенно)\nприменятся после перезапуска"
    }

    navbutton.onClick {
        if (save_read_int("navbutton")==-1) {

            save_value_int("navbutton", 1)
            Main.NAVBUTTON = 1

            navbutton.paintFlags = 0
            navbutton.setTypeface(Main.face, Typeface.BOLD)
            navbutton.text = "Скрыть кнопки навигации(Включенно)\nприменятся после перезапуска"

        } else {
            save_value_int("navbutton", -1)
            Main.NAVBUTTON = -1

            navbutton.typeface = Main.face
            navbutton.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            navbutton.text = "Скрыть кнопки навигации(Выключенно)\nприменятся после перезапуска"
        }
    }

}