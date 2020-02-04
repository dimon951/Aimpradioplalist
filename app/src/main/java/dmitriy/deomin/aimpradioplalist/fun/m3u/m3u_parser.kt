package dmitriy.deomin.aimpradioplalist.`fun`.m3u

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.file.clear_name_ot_chlama
import dmitriy.deomin.aimpradioplalist.custom.Radio

fun m3u_parser(data: String): ArrayList<Radio> {

    //список говнища которое нужно удалить(начинается строка)
    val m3uTeg: List<String> = listOf("#EXTINF", "#EXTM3U", "#PLAYLIST", "#EXTGRP")

    //если плейлист не пустой
    if (data.contains("http")) {
        val listRadio = ArrayList<Radio>()
        var name = ""
        var url = ""

        val listData = data.lines()

        for (l in listData) {

            if (esli_est(l, m3uTeg)) {
                name = find_name_v_chlame(l)
            } else {
                if (l.contains("http")) url = l
            }

            if (name.isNotEmpty() && url.isNotEmpty()) {
                listRadio.add(Radio(name = name, url = url))
                name = ""
                url = ""
            }
        }
        return listRadio
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

