package dmitriy.deomin.aimpradioplalist.`fun`.play

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.toast
import java.io.File
import java.lang.Exception

fun play_aimp_file(name: String) {

    try {
        val cm = ComponentName(
                "com.aimp.player",
                "com.aimp.player.views.Main.MainActivity")

        val i = Intent()
        i.component = cm

        i.action = Intent.ACTION_VIEW
        i.setDataAndType(Uri.parse("file://" + File(name).absolutePath), "audio/mpegurl")
        i.flags = 0x3000000

        Main.context.startActivity(i)
    } catch (e: Exception) {
        Main.context.toast("Не удалось напрямую, выберите вручную")
        play_system_file(name)
    }

}