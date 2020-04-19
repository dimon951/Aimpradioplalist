package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import java.lang.Exception

fun send_email(email:String,data:String){
    try {
        Main.context.email(email, "aimp_radio_plalist", data)
    }catch (e: Exception){
        Main.context.toast("error"+e.message.toString())
    }
}