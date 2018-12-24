package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.browse

class Abaut : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abaut)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        (findViewById<View>(R.id.fon_abaut) as RelativeLayout).setBackgroundColor(Main.COLOR_FON)

        val textView = findViewById<View>(R.id.textView_abaut) as TextView
        val t:String =  getString(R.string.abaut_text)

        textView.text=(t.replace("+++++", "Aimp radio plalist " + getString(R.string.versionName)))

    }

    fun View.opengruppa() {
        browse("https://vk.com/aimp_radio_plalist")
    }
    fun View.opentelega() {
        browse("https://t.me/joinchat/AAAAAFW3nSmcsxx9TJ9yUA")
    }
    fun View.donat() {
        //если даже просто нажмут отключим показ рекламы
        Main.save_value("reklama_pokaz","of")

        browse("https://money.yandex.ru/to/41001566605499")
    }
}
