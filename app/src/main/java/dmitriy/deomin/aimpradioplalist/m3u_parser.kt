package dmitriy.deomin.aimpradioplalist

import dmitriy.deomin.aimpradioplalist.custom.Radio

fun m3u_parser(data: String): ArrayList<Radio> {

    //список говнища которое нужно удалить(начинается строка)
    val chlam: List<String> = listOf("#EXTINF", "#EXTM3U", "#PLAYLIST", "#EXTGRP")

    //если плейлист не пустой
    if (data.contains("http")) {
        val list_radio = ArrayList<Radio>()
        var name = ""
        var url = ""

        val list_data = data.lines()

        for (l in list_data) {

            if (esli_est(l, chlam)) {
                name = find_name_v_chlame(l)
            } else {
                if (l.contains("http")) url = l
            }

            if (name.isNotEmpty() && url.isNotEmpty()) {
                list_radio.add(Radio(name = name, url = url))
                name = ""
                url = ""
            }
        }
        return list_radio
    } else {
        return arrayListOf(Radio(name = Main.PUSTO, url = ""))
    }
}

private fun esli_est(str: String, list: List<String>): Boolean {
    for (l in list.iterator()) {
        if (str.contains(l))
            return true
    }
    return false
}

private fun find_name_v_chlame(s: String): String {

    if (s.contains("=")) {
        val new_s = s.substringAfterLast('=').substringAfter(',')
        return clear_name_ot_chlama(new_s)
    } else {
        var new_s = ""
        //если говна нету удалим #EXTINF:-1, может быть разная и отделятся может запятой или пробелом ебаные казлы придумли
        if (s.contains(",")) {
            //если есть запятая по ней будем делить иначе накройняк по пробелу
            new_s = s.substringAfter(',')
        } else {
            new_s = s.substringAfter(' ')
        }
        return clear_name_ot_chlama(new_s)
    }
}


private fun clear_name_ot_chlama(s: String): String {
    // Удаляет все кроме букв и цифр в строке
    // ^ меняет режим фильтрации(на противоположный)
    //return s.replace(Regex("[^a-zA-Zа-яА-Я0-9 ]"), "")


    //пока просто уберу ненужные
    return s.replace("/", "")
}
