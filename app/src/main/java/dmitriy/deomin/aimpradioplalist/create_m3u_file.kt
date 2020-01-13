package dmitriy.deomin.aimpradioplalist

import dmitriy.deomin.aimpradioplalist.custom.Radio

fun create_m3u_file(name: String, data: ArrayList<Radio>) {


    var m3u_data = "\n#EXTM3U"
    for (l in data.iterator()) {
        m3u_data += "\n#EXTINF:-1," + l.name + "" + l.kbps + "\n" + l.url
    }

    //сохраним  временый файл ссылку
    // вернёт сигнал "File_created"
    File_function().SaveFile(name.replace("<List>", ""), m3u_data)
}