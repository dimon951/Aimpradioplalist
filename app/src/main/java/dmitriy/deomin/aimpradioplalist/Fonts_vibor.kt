package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import dmitriy.deomin.aimpradioplalist.`fun`.file.isAssetExists
import dmitriy.deomin.aimpradioplalist.`fun`.save_read
import dmitriy.deomin.aimpradioplalist.`fun`.save_value
import kotlinx.android.synthetic.main.fonts_vibor.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class Fonts_vibor : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fonts_vibor)

        if (Main.FULLSCRIN > 0) {
            //во весь экран
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if(Main.NAVBUTTON >0){
            //скрывем кнопки навигации
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
            //будем слушать  если покажется опять - закроем
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) decorView.systemUiVisibility =
                        uiOptions
            }
            //-----------------------------------------------------------------------------
        }


        fonts_vibor.setBackgroundColor(Main.COLOR_FON)

        button_font_system.typeface = Typeface.DEFAULT

        button_font_tweed.typeface = Typeface.createFromAsset(assets, "fonts/Tweed.ttf")
        button_font_Whitney.typeface = Typeface.createFromAsset(assets, "fonts/Whitney.ttf")
        button_font_bemount.typeface = Typeface.createFromAsset(assets, "fonts/Bemount.ttf")
        button_Stolzl_Book.typeface = Typeface.createFromAsset(assets, "fonts/Stolzl_Book.ttf")
        button_font_Snowstorm.typeface = Typeface.createFromAsset(assets, "fonts/Snowstorm.ttf")
        button_font_Digits.typeface = Typeface.createFromAsset(assets, "fonts/Digits.ttf")
        button_font_Yarin.typeface = Typeface.createFromAsset(assets, "fonts/Yarin.ttf")
        button_font_Frezer.typeface = Typeface.createFromAsset(assets, "fonts/Frezer.ttf")
        button_font_Futured.typeface = Typeface.createFromAsset(assets, "fonts/Futured.ttf")

        button_close_setting_font.onClick { finish() }

        //устанавливаем выбраный цвет
        selekt_fon_button()
    }

    fun selekt_fon_button(){
        clear_text_color_button()
        when (val s = save_read("fonts")) {
            "system" -> button_font_system.textColor = Main.COLOR_SELEKT
            "" -> button_font_tweed.textColor= Main.COLOR_SELEKT
            "fonts/Tweed.ttf"->button_font_tweed.textColor= Main.COLOR_SELEKT
            "fonts/Whitney.ttf"->button_font_Whitney.textColor= Main.COLOR_SELEKT
            "fonts/Bemount.ttf"->button_font_bemount.textColor= Main.COLOR_SELEKT
            "fonts/Stolzl_Book.ttf"->button_Stolzl_Book.textColor= Main.COLOR_SELEKT
            "fonts/Snowstorm.ttf"->button_font_Snowstorm.textColor= Main.COLOR_SELEKT
            "fonts/Digits.ttf"->button_font_Digits.textColor= Main.COLOR_SELEKT
            "fonts/Yarin.ttf"->button_font_Yarin.textColor= Main.COLOR_SELEKT
            "fonts/Frezer.ttf"->button_font_Frezer.textColor= Main.COLOR_SELEKT
            "fonts/Futured.ttf"->button_font_Futured.textColor= Main.COLOR_SELEKT
        }
    }

    fun clear_text_color_button(){
        button_font_system.textColor= Main.COLOR_TEXT
        button_font_tweed.textColor= Main.COLOR_TEXT
        button_font_Whitney.textColor= Main.COLOR_TEXT
        button_font_bemount.textColor= Main.COLOR_TEXT
        button_Stolzl_Book.textColor= Main.COLOR_TEXT
        button_font_Snowstorm.textColor= Main.COLOR_TEXT
        button_font_Digits.textColor= Main.COLOR_TEXT
        button_font_Yarin.textColor= Main.COLOR_TEXT
        button_font_Frezer.textColor= Main.COLOR_TEXT
        button_font_Futured.textColor= Main.COLOR_TEXT
    }

    fun save_System(v: View) {
        save_value("fonts", "system")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Tweed(v: View) {
        save_value("fonts", "fonts/Tweed.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Whitney(v: View) {
        save_value("fonts", "fonts/Whitney.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Bemount(v: View) {
        save_value("fonts", "fonts/Bemount.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Stolzl_Book(v: View) {
        save_value("fonts", "fonts/Stolzl_Book.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Snowstorm(v: View) {
        save_value("fonts", "fonts/Snowstorm.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Frezer(v: View) {
        save_value("fonts", "fonts/Frezer.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Futured(v: View) {
        save_value("fonts", "fonts/Futured.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Yarin(v: View) {
        save_value("fonts", "fonts/Yarin.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Digits(v: View) {
        save_value("fonts", "fonts/Digits.ttf")
        selekt_fon_button()
        toast("Хорошо,теперь нужно перезапустить программу")
    }

}
