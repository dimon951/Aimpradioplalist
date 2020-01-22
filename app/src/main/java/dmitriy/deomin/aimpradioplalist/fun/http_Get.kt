package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.toast
import com.github.kittinunf.fuel.httpGet
import java.nio.charset.Charset

fun hhtp_get(url: String): String {
    var readData = ""
    url.httpGet().responseString(Charset.forName(Main.File_Text_Code)) { req, res, result ->
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                readData = "error"
                Main.context.toast(result.getException().toString())
            }
            is com.github.kittinunf.result.Result.Success -> {
                if (result.get().isNotEmpty()) {
                    readData = result.get()
                } else {
                    readData = "error"
                    Main.context.toast("ошибка,пусто")
                }
            }
        }
    }
    return readData
}
