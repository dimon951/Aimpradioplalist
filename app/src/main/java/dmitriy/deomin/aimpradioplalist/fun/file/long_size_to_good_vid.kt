package dmitriy.deomin.aimpradioplalist.`fun`.file

//переводит размер файла в нормальный вид
fun long_size_to_good_vid(size: Double): String {
    return if (size > 1024 * 1024) {
        round(size / (1024 * 1024), 1).toString() + " mb"
    } else if (size > 1024) {
        round(size / 1024, 1).toString() + " kb"
    } else {
        if (size > 0) {
            round(size, 1).toString() + " bytes"
        } else {
            ""
        }

    }
}

//уменьшает количество символов после запятой в double
private fun round(number: Double, scale: Int): Double {
    var pow = 10
    for (i in 1 until scale)
        pow *= 10
    val tmp = number * pow
    return (if (tmp - tmp.toInt() >= 0.5) tmp + 1 else tmp).toInt().toDouble() / pow
}