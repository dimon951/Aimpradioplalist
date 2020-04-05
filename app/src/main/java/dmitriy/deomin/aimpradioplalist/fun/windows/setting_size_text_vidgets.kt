package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick

fun setting_size_text_vidgets(){

    val mes = DialogWindow(Main.context, R.layout.menu_edit_size)

    mes.view().findViewById<Button>(R.id.button_setting_menu_button).onClick {
        window_edit_size("SIZE_TEXT_MAIN_BUTTON")
        mes.close()
    }
    mes.view().findViewById<Button>(R.id.button_setting_vse_button).onClick {
        window_edit_size("SIZE_TEXT_VSE_BUTTON")
        mes.close()
    }
    mes.view().findViewById<Button>(R.id.button_setting_online_button).onClick {
        window_edit_size("SIZE_TEXT_ONLINE_BUTTON")
        mes.close()
    }
    mes.view().findViewById<Button>(R.id.button_setting_item_name).onClick {
        window_edit_size("SIZE_TEXT_NAME")
        mes.close()
    }
    mes.view().findViewById<Button>(R.id.button_setting_item_context).onClick {
        window_edit_size("SIZE_TEXT_CONTEXT")
        mes.close()
    }
    mes.view().findViewById<Button>(R.id.button_setting_item_context2).onClick {
        window_edit_size("SIZE_TEXT_CONTEXT_text")
        mes.close()
    }

}