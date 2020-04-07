package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.read_and_pars_m3u_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import org.jetbrains.anko.sdk27.coroutines.onClick

fun vopros_pri_otkritii_new_file(context:Context){
    val vponf = DialogWindow(context, R.layout.vopros_pri_otkritii_new_file)

    //------------------открыть из памяти---------------------------------------------
    //затираем старое
    (vponf.view().findViewById<Button>(R.id.button_dell_old_plalist)).onClick {

        //отправим с пустым старым текстом , старое затрётся
        open_load_file(context, arrayListOf(Radio(name = "", url = "")))
        //закрываем окошко
        vponf.close()
    }

    //добавляем к старому если есть дубликаты пропустим их
    (vponf.view().findViewById<Button>(R.id.button_add_old_plalist)).onClick {
        //после выбора файла он прочётся и добавится к старым данным
        open_load_file(context, read_and_pars_m3u_file(Main.MY_PLALIST))
        //закрываем окошко
        vponf.close()
    }
}