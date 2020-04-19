package dmitriy.deomin.aimpradioplalist.`fun`.file

import android.content.res.AssetManager
import dmitriy.deomin.aimpradioplalist.Main
import java.io.IOException
import java.io.InputStream

//Проверяет наличие файла в ассетах
fun isAssetExists(pathInAssetsDir: String): Boolean {
    val assetManager: AssetManager = Main.context.assets
    var inputStream: InputStream? = null
    try {
        inputStream = assetManager.open(pathInAssetsDir)
        return true
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return false
}