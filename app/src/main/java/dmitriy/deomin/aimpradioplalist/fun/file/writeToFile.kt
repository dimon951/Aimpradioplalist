package dmitriy.deomin.aimpradioplalist.`fun`.file

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.FileWriter

//запись в файл
fun writeToFile(name: String, str: String) {
    GlobalScope.launch {
        //создадим папки если нет
        create_esli_net()

        val fos: FileOutputStream

        try {
            fos = FileOutputStream(Main.ROOT + name, false)
            val fWriter: FileWriter
            try {
                fWriter = FileWriter(fos.fd)
                fWriter.write(str + "\n")
                fWriter.close()
            } catch (e: java.lang.Exception) {
                //послать сигнал
                signal("File_created_save_vse")
                        .putExtra("run", false)
                        .putExtra("update", "pizdec")
                        .putExtra("anim", "anim_of")
                        .send(Main.context)
                e.printStackTrace()
            } finally {
                fos.fd.sync()
                fos.close()
                //послать сигнал
                signal("File_created_save_vse")
                        .putExtra("run", false)
                        .putExtra("update", "zaebis")
                        .putExtra("anim", "anim_of")
                        .send(Main.context)
            }
        } catch (e: java.lang.Exception) {
            //послать сигнал
            signal("File_created_save_vse")
                    .putExtra("run", false)
                    .putExtra("update", "pizdec")
                    .putExtra("anim", "anim_of")
                    .send(Main.context)
            e.printStackTrace()
        }
    }
}