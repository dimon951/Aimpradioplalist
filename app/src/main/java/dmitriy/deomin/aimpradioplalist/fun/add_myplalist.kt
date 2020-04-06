package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.add_may_plalist_stansiy
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.toast

//добавить в мой плейлист
fun add_myplalist(name: String, url: String) {
    //слот получит ответ после добавления станции
    Slot(Main.context, "File_created").onRun {
        //получим данные
        when (it.getStringExtra("update")) {
            "est" -> Main.context.toast(name + " " + url + " уже есть в плейлисте")
            "zaebis" -> {
                //пошлём сигнал пусть мой плейлист обновится
                signal("Data_add")
                        .putExtra("run", true)
                        .putExtra("update", "zaebis")
                        .putExtra("listfile", "old") //оставим что есть в списке
                        .send(Main.context)
            }
            "pizdec" -> {
                Main.context.toast(Main.context.getString(R.string.error))
                //запросим разрешения
                EbuchieRazreshenia()
            }
        }
    }



    add_may_plalist_stansiy(url, name)
}