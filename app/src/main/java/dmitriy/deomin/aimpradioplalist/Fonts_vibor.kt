package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import dmitriy.deomin.aimpradioplalist.`fun`.save_value
import kotlinx.android.synthetic.main.fonts_vibor.*
import org.jetbrains.anko.toast

class Fonts_vibor : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fonts_vibor)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

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
    }

    fun save_System(v: View) {
        save_value("fonts", "system")
        toast("Хорошо,теперь нужно перезапустить программу")
    }
    fun save_Tweed(v: View) {
        save_value("fonts", "fonts/Tweed.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Whitney(v: View) {
        save_value("fonts", "fonts/Whitney.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Bemount(v: View) {
        save_value("fonts", "fonts/Bemount.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Stolzl_Book(v: View) {
        save_value("fonts", "fonts/Stolzl_Book.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Snowstorm(v: View) {
        save_value("fonts", "fonts/Snowstorm.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Frezer(v: View) {
        save_value("fonts", "fonts/Frezer.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Futured(v: View) {
        save_value("fonts", "fonts/Futured.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Yarin(v: View) {
        save_value("fonts", "fonts/Yarin.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Digits(v: View) {
        save_value("fonts", "fonts/Digits.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

}
