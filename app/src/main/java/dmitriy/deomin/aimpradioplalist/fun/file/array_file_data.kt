package dmitriy.deomin.aimpradioplalist.`fun`.file

import dmitriy.deomin.aimpradioplalist.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//сохранение и чение списка из памяти
fun saveArrayList(f_name: String, data: ArrayList<String>) {
    GlobalScope.launch {
        //перегоним data в строку с переносами
        var save_text = ""
        for (t in data.listIterator()) {
            save_text = save_text + "\n" + t
        }
        //сохраняем
        writeToFile(f_name, save_text)
    }
}

fun readArrayList(f_name: String): ArrayList<String> {
    //смотрим не пустой ли файл , читаем и отправляем
    val fsave = readFile(Main.ROOT + f_name)
    if (fsave == "") {
        val nechego = ArrayList<String>()
        nechego.add("")
        return nechego
    }
    //если там чтото есть разбиваем по пробелу и отправляем
    val list = fsave.split("\n")
    val chtoto = ArrayList<String>()
    for (s in list.listIterator()) {
        if (s.length > 7) {
            chtoto.add(s)
        }

    }
    return chtoto
}