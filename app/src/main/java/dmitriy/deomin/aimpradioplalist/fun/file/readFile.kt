package dmitriy.deomin.aimpradioplalist.`fun`.file

import java.io.*

fun readFile(fileName: String): String {
    //создадим папки если нет
    create_esli_net()

    //проверим есть он вообще
    val file = File(fileName)
    if (!file.exists()) {
        //если нет вернём пустоту
        return  ""
    } else {
        val fis = FileInputStream(fileName)
        val bfr = BufferedReader(InputStreamReader(fis))
        return  bfr.readText()
    }
}
