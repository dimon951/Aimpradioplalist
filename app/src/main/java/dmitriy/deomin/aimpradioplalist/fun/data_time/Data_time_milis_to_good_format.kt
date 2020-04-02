package dmitriy.deomin.aimpradioplalist.`fun`.data_time

import java.text.SimpleDateFormat
import java.util.*

fun Data_time_milis_to_good_format(data:Long): String {
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    return sdf.format(Date())
}