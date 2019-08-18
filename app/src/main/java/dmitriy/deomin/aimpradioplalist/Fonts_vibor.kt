package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.fonts_vibor.*
import org.jetbrains.anko.toast

class Fonts_vibor : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fonts_vibor)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        fonts_vibor.setBackgroundColor(Main.COLOR_FON)

        button_font_tweed.typeface = Typeface.createFromAsset(assets, "fonts/Tweed.ttf")
        button_font_SonyEricssonLogo.typeface = Typeface.createFromAsset(assets, "fonts/SonyEricssonLogo.ttf")
        button_font_Whitney.typeface = Typeface.createFromAsset(assets, "fonts/Whitney.ttf")
        button_font_bemount.typeface = Typeface.createFromAsset(assets, "fonts/Bemount.ttf")
        button_Stolzl_Book.typeface = Typeface.createFromAsset(assets, "fonts/Stolzl_Book.ttf")
        button_font_Snowstorm.typeface = Typeface.createFromAsset(assets, "fonts/Snowstorm.ttf")
        button_font_Izhitsa.typeface = Typeface.createFromAsset(assets, "fonts/Izhitsa.ttf")
        button_font_Sensei.typeface = Typeface.createFromAsset(assets, "fonts/Sensei.ttf")
        button_font_Yarin.typeface = Typeface.createFromAsset(assets, "fonts/Yarin.ttf")
        button_font_Frezer.typeface = Typeface.createFromAsset(assets, "fonts/Frezer.ttf")
        button_font_Futured.typeface = Typeface.createFromAsset(assets, "fonts/Futured.ttf")
        button_font_Rotonda.typeface = Typeface.createFromAsset(assets, "fonts/Rotonda.ttf")
    }

    fun save_System(v: View) {
        Main.save_value("fonts", "system")
        toast("Хорошо,теперь нужно перезапустить программу")
    }
    fun save_Tweed(v: View) {
        Main.save_value("fonts", "fonts/Tweed.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }
    fun save_SonyEricssonLogo(v: View) {
        Main.save_value("fonts", "fonts/SonyEricssonLogo.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Whitney(v: View) {
        Main.save_value("fonts", "fonts/Whitney.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Bemount(v: View) {
        Main.save_value("fonts", "fonts/Bemount.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Stolzl_Book(v: View) {
        Main.save_value("fonts", "fonts/Stolzl_Book.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Snowstorm(v: View) {
        Main.save_value("fonts", "fonts/Snowstorm.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Izhitsa(v: View) {
        Main.save_value("fonts", "fonts/Izhitsa.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Rotonda(v: View) {
        Main.save_value("fonts", "fonts/Rotonda.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Frezer(v: View) {
        Main.save_value("fonts", "fonts/Frezer.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Futured(v: View) {
        Main.save_value("fonts", "fonts/Futured.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Yarin(v: View) {
        Main.save_value("fonts", "fonts/Yarin.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

    fun save_Sensei(v: View) {
        Main.save_value("fonts", "fonts/Sensei.ttf")
        toast("Хорошо,теперь нужно перезапустить программу")
    }

}
