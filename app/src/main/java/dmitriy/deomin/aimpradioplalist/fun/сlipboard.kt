package dmitriy.deomin.aimpradioplalist.`fun`

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import dmitriy.deomin.aimpradioplalist.Main
import org.jetbrains.anko.toast

//запись в буфер
fun putText_сlipboard(text: String, context: Context) {
    val sdk = android.os.Build.VERSION.SDK_INT
    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
        clipboard.text = text
    } else {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(text, text)
        clipboard.setPrimaryClip(clip)
    }
}

//чтение из буфера
fun getText_сlipboard(c: Context): String {
    val text: String
    val sdk = android.os.Build.VERSION.SDK_INT
    text = if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager?
        clipboard!!.text.toString()
    } else {
        val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard!!.text == null) {
            Main.context.toast("Буфер обмена пуст")
            ""
        } else {
            clipboard.text.toString()
        }
    }
    return text
}