package dmitriy.deomin.aimpradioplalist.`fun`

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import dmitriy.deomin.aimpradioplalist.Main

//проверка есть ли приложение
fun is_install_app(app: String): Boolean {
    val pm = Main.context.packageManager
    var pi: PackageInfo? = null
    try {
        pi = pm.getPackageInfo(app, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return pi != null
}