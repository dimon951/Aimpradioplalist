package dmitriy.deomin.aimpradioplalist.`fun`

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun formatTimeToEnd(second: Long): String? {
    val format: DateFormat = SimpleDateFormat("HHч mmмин ssсек")
    format.setTimeZone(TimeZone.getTimeZone("UTC"))
    return format.format(Date(second))
}