package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

class Abaut : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.abaut)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        (findViewById<View>(R.id.fon_abaut) as RelativeLayout).setBackgroundColor(Main.COLOR_FON)

        val textView = findViewById<View>(R.id.textView_abaut) as TextView
        val t: String = getString(R.string.abaut_text)

        textView.text = (t.replace("+++++", "Aimp radio plalist " + getString(R.string.versionName)))

        val vk = findViewById<Button>(R.id.vk)
        vk.onClick {
            val anim = AnimationUtils.loadAnimation(Main.context, R.anim.myalpha)
            vk.startAnimation(anim)
            browse("https://vk.com/aimp_radio_plalist")
        }

        val tl = findViewById<Button>(R.id.telegram)
        tl.onClick {
            val anim = AnimationUtils.loadAnimation(Main.context, R.anim.myalpha)
            tl.startAnimation(anim)
            browse("https://t.me/joinchat/AAAAAFW3nSmcsxx9TJ9yUA")
        }


        val don = findViewById<Button>(R.id.donat)
        don.onClick {
            val anim = AnimationUtils.loadAnimation(Main.context, R.anim.myalpha)
            don.startAnimation(anim)

            //если даже просто нажмут отключим показ рекламы
            if (Main.save_read("reklama_pokaz") == "of") {
                //если не первый  раз нажимают поблагодарим за возможный донат
                toast("Спасибо")
            } else {
                Main.save_value("reklama_pokaz", "of")
                Main.mAdView.visibility = View.GONE
                toast("Реклама отключена")
            }

            //подаждём секунду и откроем браужзер
            GlobalScope.launch {
                delay(2000)
                browse("https://money.yandex.ru/to/41001566605499")
            }

        }
    }
}
