package dmitriy.deomin.aimpradioplalist.`fun`

import com.github.kittinunf.fuel.Fuel
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.file.create_esli_net
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import java.io.File

fun download_file(url: String, name: String, sourse: String) {
    if (isValidURL(url)) {
        //проверим есть ли наши папки
        create_esli_net()
        //-----------скачиваем файл (читам его)--------
        GlobalScope.launch {
            //запустим анимацию
            signal("Main_update").putExtra("signal", "start_" + sourse).send(Main.context)

            val d = Fuel.download(url)
                    .fileDestination { response, url -> File(Main.ROOT + name) }
                    .progress { readBytes, totalBytes ->
                        val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                        signal("dw_progres")
                                .putExtra("readBytes", readBytes.toString())
                                .putExtra("totalBytes", totalBytes.toString())
                                .send(Main.context)

                        if (progress.toInt() == 100) {
                            signal("Main_update").putExtra("signal", "stop_" + sourse).send(Main.context)
                        }
                    }
                    .response { result -> }

            //если пошлют сигнал отмены отменим и удалим что скачалось
            Slot(Main.context, "dw_cansel").onRun {
                signal("dw_progres")
                        .putExtra("readBytes", "0")
                        .putExtra("totalBytes", "0")
                        .send(Main.context)
                signal("Main_update").putExtra("signal", "stop_" + sourse).send(Main.context)
                d.cancel()
                File(Main.ROOT + name).delete()
            }

        }
        //--------------------------------------------------------
    } else {
        Main.context.toast("Неверный URL")
    }
}