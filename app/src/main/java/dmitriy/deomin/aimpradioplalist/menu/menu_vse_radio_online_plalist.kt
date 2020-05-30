package dmitriy.deomin.aimpradioplalist.menu

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.file.is_existence_file
import dmitriy.deomin.aimpradioplalist.`fun`.file.long_name_resize
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.download_i_open_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.toast

fun menu_vse_radio_online_plalist(context: Context, radio: Radio) {

    //проврим существование файла
    val file_pach = Main.ROOT + radio.name + "." + radio.url.substringAfterLast('.')
    if (is_existence_file(file_pach)) {
        menu_saved_file(context, file_pach)
    } else {
        val mvr = DialogWindow(context, R.layout.menu_vse_radio)

        val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
        val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
        val open_custom = mvr.view().findViewById<Button>(R.id.open_custom_plaer)
        val loadlist = mvr.view().findViewById<Button>(R.id.button_load_list)
        val share = mvr.view().findViewById<Button>(R.id.button_cshre)

        val name = radio.name.replace("<List>", "")
        //Имя и урл выбраной станции , при клике будем копировать урл в буфер
        val text_name_i_url = mvr.view().findViewById<TextView>(R.id.textView_vse_radio)
        text_name_i_url.text = name + "\n" + radio.url

        text_name_i_url.onClick {
            text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            putText_сlipboard(radio.url, context)
            context.toast("url скопирован в буфер")
        }

        open_aimp.onLongClick {
            play_system(long_name_resize(name), radio.url)
        }

        open_aimp.onClick {
            if (save_read("categoria") == "3") {
                play_system(long_name_resize(name), radio.url)
            } else {
                play_aimp(long_name_resize(name), radio.url)
            }
            mvr.close()
        }

        open_custom.onClick {
            if (isValidURL(radio.url)) {
                Play_audio(radio.name, radio.url)
            } else {
                context.toast("Возможно ссылка битая, нельзя открыть")
            }
            mvr.close()
        }

        add_pls.onClick {

            //если текуший элемент список ссылок
            if (radio.name.contains("<List>")) {
                mvr.close()
                val dw = DialogWindow(context, R.layout.dialog_delete_stancii)
                val dw_start = dw.view().findViewById<Button>(R.id.button_dialog_delete)
                val dw_no = dw.view().findViewById<Button>(R.id.button_dialog_no)
                val dw_logo = dw.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)

                dw_logo.text = "Текущая ссылка содержит список\n Все равно добавить ?"
                dw_start.text = "Да"
                dw_no.text = "Нет"

                dw_start.onClick {
                    add_myplalist(radio.name, radio.url)
                    dw.close()
                }
                dw_no.onClick {
                    dw.close()
                }

            } else {
                add_myplalist(radio.name, radio.url)
                mvr.close()
            }

        }

        share.onClick {
            //сосавим строчку как в m3u вайле
            share_text(radio.name + "\n" + radio.url)
        }
        share.onLongClick {
            send_email("deomindmitriy@gmail.com",  radio.name + "\n" + radio.url)
        }

        //если текуший элемент список ссылок
        if (radio.name.contains("<List>")) {
            //скроем кнопки открытия в плеере
            open_aimp.visibility = View.GONE
            open_custom.visibility = View.GONE
            //покажем кнопку загрузки списка
            loadlist.visibility = View.VISIBLE
        } else {
            //иначе покажем
            open_aimp.visibility = View.VISIBLE
            open_custom.visibility = View.VISIBLE
            //скроем кнопку загрузки списка
            loadlist.visibility = View.GONE
        }

        //загрузить список
        loadlist.onClick {
            //закрываем основное окошко
            mvr.close()
            download_i_open_m3u_file(radio.url, "anim_online_plalist")
        }
    }
}