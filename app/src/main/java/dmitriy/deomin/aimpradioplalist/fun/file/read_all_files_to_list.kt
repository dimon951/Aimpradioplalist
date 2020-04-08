package dmitriy.deomin.aimpradioplalist.`fun`.file

import dmitriy.deomin.aimpradioplalist.Main
import java.io.File

fun read_all_files_to_list(): ArrayList<String> {
    val dir = File(Main.ROOT) //path указывает на директорию
    val arrFiles = dir.listFiles()


    val d = ArrayList<String>()

    if (arrFiles != null) {
        for (s in arrFiles.iterator()) {
            if (s.isFile && s.name != "history_url.txt" && s.name != "theme.txt" && s.name != "my_plalist.m3u") {
                d.add(s.absolutePath)
            }
        }
        //отсортируем по дате создания файла
        d.sortWith(Comparator { o1, o2 ->
            val a = File(o1).lastModified()
            val b = File(o2).lastModified()
            a.compareTo(b)
        })
    }
    return d
}