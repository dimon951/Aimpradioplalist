package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.share
import org.jetbrains.anko.toast
import java.lang.Exception

fun share_text(text:String){
    try {
        Main.context.share(text)
    }catch (e: Exception){
        Main.context.toast("error"+e.message.toString())
    }
}