package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.EditText
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.add_koment
import dmitriy.deomin.aimpradioplalist.`fun`.load_koment
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

fun add_koment_window(context:Context,id:String){
    //добавление коментариев
    //-------------------------------------------------------------------------------
    val add_kom = DialogWindow(context, R.layout.add_koment)
    val ed = add_kom.view().findViewById<EditText>(R.id.ed_add_kom)
    add_kom.view().findViewById<Button>(R.id.btn_ad_kom).onClick {

        if (ed.text.toString().isEmpty()) {
            context.toast("Введите текст")
        } else {
            Slot(context, "add_koment", false).onRun {
                if (it.getStringExtra("update") == "zaebis") {
                    add_kom.close()
                    load_koment(id)
                } else {
                    context.toast("ошибка")
                }
            }
           add_koment(id, ed.text.toString())
        }
    }
    //-------------------------------------------------------------------------------------
}