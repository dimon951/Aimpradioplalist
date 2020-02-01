package dmitriy.deomin.aimpradioplalist.`fun`

import java.util.*

fun data_time():String{
    //  val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//  val currentDate = sdf.format(Date())
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
     return calendar.getTime().toString()
}