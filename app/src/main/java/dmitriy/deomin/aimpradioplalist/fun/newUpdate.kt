package dmitriy.deomin.aimpradioplalist.`fun`

import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow

fun newUpdate() {
    val startWindow = DialogWindow(Main.context, R.layout.error_import)
    val t = startWindow.view().findViewById<TextView>(R.id.textView_error_import_podrobno)
    t.text = ""

}