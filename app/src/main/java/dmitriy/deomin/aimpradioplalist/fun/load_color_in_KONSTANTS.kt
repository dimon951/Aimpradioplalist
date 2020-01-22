package dmitriy.deomin.aimpradioplalist.`fun`

import android.graphics.Color
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R

fun load_color_in_KONSTANTS(){
    //--------------------------------------------------------------------
    Main.COLOR_FON = if (save_read_int("color_fon") == 0) {
        Color.DKGRAY
    } else {
        save_read_int("color_fon")
    }
    //ставим цвет постов
    Main.COLOR_ITEM = if (save_read_int("color_post1") == 0) {
        Main.context.resources.getColor(R.color.green)
    } else {
        save_read_int("color_post1")
    }
    //ставим цвеи текста
    Main.COLOR_TEXT = if (save_read_int("color_text") == 0) {
        Color.BLACK
    } else {
        save_read_int("color_text")
    }
    //Цвет выделения
    Main.COLOR_SELEKT = if (save_read_int("color_selekt") == 0) {
        Color.GREEN
    } else {
        save_read_int("color_selekt")
    }
    //ставим цвеи текста для контекста
    Main.COLOR_TEXTcontext = if (save_read_int("color_textcontext") == 0) {
        Main.context.resources.getColor(R.color.textcontext)
    } else {
        save_read_int("color_textcontext")
    }
    //------------------------------------------------------------------------------
}