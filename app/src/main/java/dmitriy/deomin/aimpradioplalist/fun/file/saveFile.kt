package dmitriy.deomin.aimpradioplalist.`fun`.file

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.FileWriter

//Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
fun saveFile(name: String, data: String) {
    GlobalScope.launch {
        //проверим наличие нашей папки  и доспуп к ней
        create_esli_net()

        val all_name = Main.ROOT + clear_name_ot_chlama(name) + ".m3u"
        val fos: FileOutputStream

        try {
            fos = FileOutputStream(all_name, false)
            val fWriter: FileWriter
            try {
                fWriter = FileWriter(fos.fd)
                fWriter.write(data)
                fWriter.close()
            } catch (e: java.lang.Exception) {
                //послать сигнал
                signal("File_created")
                        .putExtra("run", false)
                        .putExtra("update", "pizdec")
                        .send(Main.context)
                e.printStackTrace()
            } finally {
                fos.fd.sync()
                fos.close()
                //послать сигнал
                signal("File_created")
                        .putExtra("run", false)
                        .putExtra("update", "zaebis")
                        .putExtra("name", all_name)
                        .send(Main.context)
            }
        } catch (e: java.lang.Exception) {
            //послать сигнал
            signal("File_created")
                    .putExtra("run", false)
                    .putExtra("update", "pizdec")
                    .send(Main.context)
            e.printStackTrace()
        }
    }
}