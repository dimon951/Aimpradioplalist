package dmitriy.deomin.aimpradioplalist.`fun`.play

import android.content.Intent
import android.net.Uri
import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.toast
import java.lang.Exception

fun play_system_file(name: String) {
    try {
        val i = Intent(Intent.ACTION_VIEW)
        i.setDataAndType(Uri.parse("file://" + name), "audio/mpegurl")
        //проверим есть чем открыть или нет
        if (i.resolveActivity(Main.context.packageManager) != null) {
            Main.context.startActivity(i)
        } else {
            Main.context.toast("Системе не удалось ( ")
        }
    } catch (e: Exception) {
        Main.context.toast("Error" + e)
    }

}