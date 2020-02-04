package dmitriy.deomin.aimpradioplalist.`fun`.file

import java.io.File

fun getDirSize(dir: File): Long {
    var size: Long = 0
    if (dir.isFile) {
        size = dir.length()
    } else {
        val subFiles = dir.listFiles()
        for (file in subFiles) {
            size += if (file.isFile) {
                file.length()
            } else {
                getDirSize(file)
            }
        }
    }
    return size
}