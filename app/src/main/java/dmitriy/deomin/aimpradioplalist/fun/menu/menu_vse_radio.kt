package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import org.jetbrains.anko.email
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import org.jetbrains.anko.toast
fun menu_vse_radio(context:Context,radio:Radio){

    val name = radio.name

    val mvr = DialogWindow(context, R.layout.menu_vse_radio)

    val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
    val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
    val open_custom = mvr.view().findViewById<Button>(R.id.open_custom_plaer)
    val share = mvr.view().findViewById<Button>(R.id.button_cshre)

//Имя и урл выбраной станции , при клике будем копировать урл в буфер
    val text_name_i_url = mvr.view().findViewById<TextView>(R.id.textView_vse_radio)
    text_name_i_url.text = name + "\n" + radio.url
    text_name_i_url.onClick {
        text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
        putText_сlipboard(radio.url, context)
        context.toast("Url скопирован в буфер")
        mvr.close()
    }

    open_aimp.onLongClick {
        play_system(name, radio.url)
        mvr.close()
    }

    add_pls.onClick {
        add_myplalist(name, radio.url)
        mvr.close()
    }

    share.onClick {
        //сосавим строчку как в m3u вайле
        share_text(name + "\n" + radio.url)
    }
    share.onLongClick {
        send_email("deomindmitriy@gmail.com",name + "\n" + radio.url)
    }

    open_aimp.onClick {
        play_aimp(name, radio.url)
        mvr.close()
    }

    open_custom.onClick {
        if(isValidURL(radio.url)){
            Play_audio(radio.name, radio.url)
        }else{
            context.toast("Возможно ссылка битая, нельзя открыть")
        }
        mvr.close()
    }
}
