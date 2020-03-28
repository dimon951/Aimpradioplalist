package dmitriy.deomin.aimpradioplalist.`fun`.file

import java.io.File

fun is_existence_file(name:String):Boolean{
    val file = File(name)
    if (file.exists() && file.isFile) {
        return true
    }
    return false
}
