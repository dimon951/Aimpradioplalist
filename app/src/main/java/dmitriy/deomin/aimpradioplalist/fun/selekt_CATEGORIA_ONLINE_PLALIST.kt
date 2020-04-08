package dmitriy.deomin.aimpradioplalist.`fun`

import android.view.View
import dmitriy.deomin.aimpradioplalist.Main
import kotlinx.android.synthetic.main.online_plalist.view.*
import org.jetbrains.anko.backgroundColor

fun selekt_CATEGORIA_ONLINE_PLALIST(cat: String, v: View) {
    when (cat) {
        "1" -> {
            v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_ITEM
            v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
        }
        "2" -> {
            v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_ITEM
            v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
        }
        "3" -> {
            v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_ITEM
            v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
        }
        "4" -> {
            v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_ITEM
        }
        "del" -> {
            v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
            v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
        }
    }

}