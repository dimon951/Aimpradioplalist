package dmitriy.deomin.aimpradioplalist.`fun`


import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.m3u_parser
import dmitriy.deomin.aimpradioplalist.custom.Radio
import java.io.File
import java.io.FileReader


//Принимает полный путь к файлу с его расширением
//или только его имя(без расширения) по умолчанию будет искаться в папке программы
fun read_and_pars_m3u_file(full_name: String): ArrayList<Radio> {

    val data = read_file_to_str(full_name)

    return if (data.isNotEmpty()) m3u_parser(data) else {
        arrayListOf(Radio(name = Main.PUSTO, url = ""))
    }
}

fun read_file_to_str(name: String): String {
    //проверим есть он вообще
    //если нет вернём пустоту
    val file = File(name)
    return if (!file.exists()) {
        ""
    } else {
        try {
            FileReader(name).readText()
        } catch (e: Exception) {
            ""
        }
    }
}