package dmitriy.deomin.aimpradioplalist.`fun`

fun konvert_read_save_pos(page: Int): Int {
    var r = 0
    when (page) {
        0 -> r = 1
        10 -> r = 0
        100 -> r = 1
        200 -> r = 2
        300 -> r = 3
    }
    return r
}