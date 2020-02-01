package dmitriy.deomin.aimpradioplalist.`fun`

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dmitriy.deomin.aimpradioplalist.Main


fun get_history_online_plalist(): ArrayList<String> {
    //---------------------------------------------------------------------
    val savhis = save_read("history_navigacia")
    return if (savhis.length > 1) {
        val collectionType = object : TypeToken<ArrayList<String>>() {}.type
        Gson().fromJson(savhis, collectionType)
    }else{
        arrayListOf("")
    }
//-----------------------------------------------------------------------
}


//сохранялки
//----------------------------
fun save_value(Key: String, Value: String) { //сохранение строки
    val editor = Main.mSettings.edit()
    editor.putString(Key, Value)
    editor.apply()
}

fun save_read(key_save: String): String {  // чтение настройки
    return if (Main.mSettings.contains(key_save)) {
        Main.mSettings.getString(key_save, "").toString()
    } else ""
}

fun save_value_int(Key: String, Value: Int) { //сохранение строки
    val editor = Main.mSettings.edit()
    editor.putInt(Key, Value)
    editor.apply()
}

fun save_read_int(key_save: String): Int {  // чтение настройки
    return if (Main.mSettings.contains(key_save)) {
        Main.mSettings.getInt(key_save, 0)
    } else 0
}
//-------------------