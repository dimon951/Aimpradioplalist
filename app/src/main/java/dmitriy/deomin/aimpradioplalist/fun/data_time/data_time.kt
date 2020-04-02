package dmitriy.deomin.aimpradioplalist.`fun`.data_time

import java.util.*

fun data_time():String{
    // Вернёт   04/3/2020 03:57:58
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
    return calendar.time.toString()
}