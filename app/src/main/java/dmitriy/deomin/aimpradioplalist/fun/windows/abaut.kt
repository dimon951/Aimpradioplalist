package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.startActivity
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.Politika
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick


fun abaut(){
    val abaut = DialogWindow(Main.context, R.layout.abaut)
    abaut.view().findViewById<RelativeLayout>(R.id.fon_abaut).setBackgroundColor(Main.COLOR_FON)

    var t: String = Main.context.getString(R.string.abaut_text)
    t= t.replace("+++++", "Aimp radio plalist " + Main.context.getString(R.string.versionName))
    abaut.view().findViewById<TextView>(R.id.textView_abaut).text =t

    abaut.view().findViewById<Button>(R.id.button_politika).onClick { Main.context.startActivity<Politika>() }
    (abaut.view().findViewById<Button>(R.id.vk)).onClick { Main.context.browse("https://vk.com/aimp_radio_plalist") }
    (abaut.view().findViewById<Button>(R.id.telegram)).onClick { Main.context.browse("https://t.me/joinchat/AAAAAFW3nSmcsxx9TJ9yUA") }
    (abaut.view().findViewById<Button>(R.id.donat)).onClick { Main.context.browse("https://money.yandex.ru/to/41001566605499") }
}