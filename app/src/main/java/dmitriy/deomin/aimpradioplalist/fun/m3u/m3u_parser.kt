package dmitriy.deomin.aimpradioplalist.`fun`.m3u

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.file.clear_name_ot_chlama
import dmitriy.deomin.aimpradioplalist.custom.Radio

//список говнища которое нужно удалить(начинается строка)
val m3uTeg_govna: List<String> = listOf("#EXTM3U", "#PLAYLIST", "#EXTGRP")

fun m3u_parser(data: String): ArrayList<Radio> {

    //если плейлист не пустой
    if (data.contains("http") or data.contains("/storage/") ) {
        val listRadio = ArrayList<Radio>()
        var name = ""
        var url = ""

        val listData = data.lines()

        for (l in listData) {
            //если это не строка с не с говном будем смотреть с чем она
            if(!if_govno_find(l)){
                if (l.contains("#EXTINF")) {
                    name = find_name_v_chlame(l)
                } else if(l.contains("http") or l.contains("/storage/")){
                     url = l
                }
                if (name.isNotEmpty() && url.isNotEmpty()) {
                    listRadio.add(Radio(name = name, url = url))
                    name = ""
                    url = ""
                }
            }
        }
        return listRadio
    } else {
        return arrayListOf(Radio(name = Main.PUSTO, url = ""))
    }
}

//если есть вернёт true
private fun if_govno_find(str: String): Boolean {
    for (l in m3uTeg_govna.iterator()) {
        if (str.contains(l))
            return true
    }
    return false
}

private fun find_name_v_chlame(s: String): String {

    // подстрока до первого указанного разделителя
    //val first = "developer.alexanderklimov.ru".substringBefore('.') // developer

    // подстрока после первого указанного разделителя
    //val last = "developer.alexanderklimov.ru".substringAfter('.') // alexanderklimov.ru

    // подстрока после последнего указанного разделителя
    //  val last = "developer.alexanderklimov.ru".substringAfterLast('.') // ru


    if (s.contains("=")) {
        //строка может быть:
        //#EXTINF:-1 group-title="Брест (Белорусь)",Брест - Камера 01
        //#EXTINF:-1,=== Kinodrom SN ===
        //#EXTINF:-1 tvg-logo="https://www.radio.pervii.com/logo/1426407635.jpg" group-title="RUSSIAN Radio - radio.pervii.com", Radio Rekord Russkiy Mix - 320 kbit/s

        // val new_s = s.substringAfterLast('=').substringAfter(',')
        var new_s = s.substringAfterLast('=')
        //если строка получилась пустой вернём не обработанный вариант
        if(new_s.isEmpty()){
            new_s = s
        }
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

