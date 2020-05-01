package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class Abaut : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.abaut)
        if(Main.FULLSCRIN >0){
            //во весь экран
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        findViewById<RelativeLayout>(R.id.fon_abaut).setBackgroundColor(Main.COLOR_FON)

        var t: String = getString(R.string.abaut_text)
        t= t.replace("+++++", "Aimp radio plalist " + getString(R.string.versionName))
        findViewById<TextView>(R.id.textView_abaut).text =t

        findViewById<Button>(R.id.button_politika).onClick { startActivity<Politika>() }
        (findViewById<Button>(R.id.vk)).onClick { browse("https://vk.com/aimp_radio_plalist") }
        (findViewById<Button>(R.id.telegram)).onClick { browse("https://t.me/joinchat/AAAAAFW3nSmcsxx9TJ9yUA") }
        (findViewById<Button>(R.id.donat)).onClick { browse("https://money.yandex.ru/to/41001566605499") }
    }
}
