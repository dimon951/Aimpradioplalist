package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.is_install_app
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

fun menu_setup_aimp(url: String, name: String) {

    val sa = DialogWindow(Main.context, R.layout.dialog_no_aimp)

    val dw_aimp_market = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_market)
    val dw_aimp_link = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_link)
    val open_sys = sa.view().findViewById<Button>(R.id.button_dialog_open_sistem)

    //если есть магазин покажем и установку через него
    if (is_install_app("com.google.android.gms")) {
        dw_aimp_market.visibility = View.VISIBLE
    } else {
        dw_aimp_market.visibility = View.GONE
    }

    dw_aimp_market.onClick {
        Main.context.browse("market://details?id=com.aimp.player")
    }

    dw_aimp_link.onClick {
        Main.context.browse(Main.LINK_DOWLOAD_AIMP)
    }

    open_sys.onClick {

        Slot(Main.context, "File_created").onRun {
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setDataAndType(Uri.parse(it.getStringExtra("name")), "audio/mpegurl")
                    //проверим есть чем открыть или нет
                    if (i.resolveActivity(Main.context.packageManager) != null) {
                        Main.context.startActivity(i)
                    } else {
                        Main.context.toast("Системе не удалось ( ")
                    }
                }
                "pizdec" -> {
                    Main.context.toast("Ошибка сохранения файла")
                }

            }
        }

        //передаётся один поток то создадим файл и откроем его иначе передаётся уже созданый файл
        if (url.isNotEmpty()) {
            create_m3u_file(name, arrayListOf(Radio(name = name, url = url)))
        } else {
            Main.context.toast("Ошибка сохранения файла(нечего сохранять)")
        }
    }
}