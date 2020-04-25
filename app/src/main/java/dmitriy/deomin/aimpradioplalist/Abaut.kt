package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.`fun`.save_read
import dmitriy.deomin.aimpradioplalist.`fun`.save_value
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

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
        (findViewById<Button>(R.id.donat)).onClick {
            //если даже просто нажмут отключим показ рекламы
            if (save_read("reklama_pokaz") == "of") {
                //если не первый  раз нажимают поблагодарим за возможный донат
                toast("Спасибо")
            } else {
                save_value("reklama_pokaz", "of")
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
