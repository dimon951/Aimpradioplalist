package dmitriy.deomin.aimpradioplalist.`fun`.file

fun clear_name_ot_chlama(s: String): String {
    // Удаляет все кроме букв и цифр в строке
    // ^ меняет режим фильтрации(на противоположный)
    //return s.replace(Regex("[^a-zA-Zа-яА-Я0-9 ]"), "")

    //пока просто уберу ненужные
    return s.replace("/", "")
}