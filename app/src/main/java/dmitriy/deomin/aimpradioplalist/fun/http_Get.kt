package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.toast
import com.github.kittinunf.fuel.httpGet
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import java.nio.charset.Charset

fun hhtp_get(url: String) {

    url.httpGet().responseString(Charset.forName(Main.File_Text_Code)) { result ->
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                signal("hhtp_get").putExtra("data", "error").send(Main.context)
                Main.context.toast(result.getException().toString())
            }
            is com.github.kittinunf.result.Result.Success -> {
                if (result.get().isNotEmpty()) {
                    signal("hhtp_get").putExtra("data", result.get()).send(Main.context)
                } else {
                    signal("hhtp_get").putExtra("data", "error").send(Main.context)
                    Main.context.toast("ошибка,пусто")
                }
            }
        }

    }
}
