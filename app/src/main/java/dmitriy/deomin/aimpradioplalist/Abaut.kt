package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.longToast
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

        (findViewById<Button>(R.id.vk)).onClick {
            browse("https://vk.com/aimp_radio_plalist")
        }

        (findViewById<Button>(R.id.telegram)).onClick {
            browse("https://t.me/joinchat/AAAAAFW3nSmcsxx9TJ9yUA")
        }

        (findViewById<Button>(R.id.donat)).onClick {
            //если даже просто нажмут отключим показ рекламы
            if (Main.save_read("reklama_pokaz") == "of") {
                //если не первый  раз нажимают поблагодарим за возможный донат
                toast("Спасибо")
            } else {
                Main.save_value("reklama_pokaz", "of")
                longToast("Если решили кинуть копеечку спасибо! , реклама будет скрыта после перезагрузки")
            }

            //подаждём секунду и откроем браужзер
            GlobalScope.launch {
                delay(2000)
                browse("https://money.yandex.ru/to/41001566605499")
            }

        }
    }
}
