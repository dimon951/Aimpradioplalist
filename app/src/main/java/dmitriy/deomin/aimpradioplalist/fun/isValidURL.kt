package dmitriy.deomin.aimpradioplalist.`fun`

import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException
import java.net.URL

fun isValidURL(urlString: String): Boolean {
    try {
        val url = URL(urlString)
        return URLUtil.isValidUrl(url.toString()) && Patterns.WEB_URL.matcher(url.toString()).matches()
    } catch (e: MalformedURLException) {

    }
    return false
}
