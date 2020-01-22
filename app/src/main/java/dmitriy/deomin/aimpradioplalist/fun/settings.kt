package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main

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