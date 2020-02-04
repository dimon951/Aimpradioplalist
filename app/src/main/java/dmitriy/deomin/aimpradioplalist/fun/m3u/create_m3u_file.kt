package dmitriy.deomin.aimpradioplalist.`fun`.m3u
import dmitriy.deomin.aimpradioplalist.File_function
import dmitriy.deomin.aimpradioplalist.custom.Radio

fun create_m3u_file(name: String, data: ArrayList<Radio>) {

    var m3uData = "\n#EXTM3U"
    for (l in data) {
        m3uData += "\n#EXTINF:-1," + l.name + "" + l.kbps + "\n" + l.url
    }

    //сохраним  временый файл ссылку
    // вернёт сигнал "File_created"
    File_function().SaveFile(name.replace("<List>", ""), m3uData)
}