package dmitriy.deomin.aimpradioplalist.`fun`.file

import android.os.Environment
import android.widget.Toast
import dmitriy.deomin.aimpradioplalist.Main
import java.io.File

/* Проверяет, доступно ли external storage для чтения и записи */
private val isExternalStorageWritable: Boolean
    get() {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }


//проверим наличие нашей папки  и доспуп к ней
fun create_esli_net() {
    if (isExternalStorageWritable) {
        val sddir = File(Main.ROOT)
        if (!sddir.exists()) {
            sddir.mkdirs()
        }
    } else {
        Toast.makeText(Main.context, "Нет доступа к памяти", Toast.LENGTH_LONG).show()
        return
    }
}


