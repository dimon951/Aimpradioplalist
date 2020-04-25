package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.download_file
import dmitriy.deomin.aimpradioplalist.`fun`.is_install_app
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import org.jetbrains.anko.browse
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast


fun menu_setup_aimp(url: String, name: String) {

    val sa = DialogWindow(Main.context, R.layout.dialog_no_aimp)

    val dw_aimp_market = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_market)
    val dw_aimp_link = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_link)
    val open_sys = sa.view().findViewById<Button>(R.id.button_dialog_open_sistem)
    val dw_progres = sa.view().findViewById<ProgressBar>(R.id.progressBar2)
    val text_logo = sa.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)

    text_logo.text ="AIMP не найден, что cделать?"

    //если есть магазин покажем и установку через него
    if (is_install_app("com.google.android.gms")) {
        dw_aimp_market.visibility = View.VISIBLE
    } else {
        dw_aimp_market.visibility = View.GONE
    }

    dw_aimp_market.onClick {
        Main.context.browse("market://details?id=com.aimp.player")
        sa.close()
    }

    dw_aimp_link.text = "Скачать Aimp (Долгим нажатием файл загрузится через программу)"

    dw_aimp_link.setOnLongClickListener {

        val name_app = "aimp_setup.apk"

        Slot(Main.context, "dw_progres").onRun {
            val totalBytes = it.getStringExtra("totalBytes")
            val readBytes = it.getStringExtra("readBytes")
            if (totalBytes != null) {
                dw_progres.max = totalBytes.toInt()
            }
            if (readBytes != null) {
                dw_progres.progress = readBytes.toInt()
            }

            if (totalBytes == readBytes) {
                if (totalBytes == "0") {
                    text_logo.text = "Ошибка"
                    dw_aimp_link.visibility = View.VISIBLE
                } else {
                    text_logo.text ="Загруженно в:"+Main.ROOT + "запустите установку вручную через любой файловый менеджер"
                    dw_aimp_link.visibility = View.VISIBLE
                }
            }
        }
        dw_aimp_link.visibility = View.GONE
        text_logo.text = "Загрузка установочного файла в папку программы"
        dw_progres.visibility = View.VISIBLE
        download_file(Main.LINK_DOWLOAD_AIMP,name_app,"anim_download_app")
        true
    }

    dw_aimp_link.setOnClickListener {
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
                "pizdec" -> Main.context.longToast(it.getStringExtra("erorr")!!)

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