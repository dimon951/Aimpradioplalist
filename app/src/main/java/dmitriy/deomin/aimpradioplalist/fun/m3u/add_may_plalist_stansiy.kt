package dmitriy.deomin.aimpradioplalist.`fun`.m3u

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.file.create_esli_net
import dmitriy.deomin.aimpradioplalist.`fun`.file.readFile
import dmitriy.deomin.aimpradioplalist.`fun`.file.saveFile
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import java.io.FileNotFoundException

//добавляется в текущему плейлисту ещё станцию
fun add_may_plalist_stansiy(link: String, name: String) {

    //создадим папки если нет
    create_esli_net()

    //прочитаем старыйе данные
    var old_text = ""
    try {
        old_text = readFile(Main.MY_PLALIST)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    if (old_text.length > Main.PUSTO.length) {
        //разобьём всю кучу
        val mas = old_text.lines()
        for (s in mas) {
            //если такая уже есть выходим и шлём сигнал что закрылось окошко
            if (s == link) {
                //послать сигнал
                signal("File_created").putExtra("update", "est").send(Main.context)
                return
            }
        }
        //если цикл прошёл мимо то добавим станцию
        saveFile("my_plalist", "$old_text\n#EXTINF:-1,$name\n$link")
    } else {
        //если наш плейлист пуст добавим в начале файла #EXTM3U
        saveFile("my_plalist", "#EXTM3U\n#EXTINF:-1,$name\n$link")
    }
}