package dmitriy.deomin.aimpradioplalist.`fun`.file

import java.io.File

fun deleteAllFilesFolder(path: String) {
    for (myFile in File(path).listFiles()) {
        if (
                myFile.isFile && myFile.name != "theme.txt" &&
                myFile.name != "history_url.txt" &&
                myFile.name != "my_plalist.m3u") myFile.delete()
    }

}