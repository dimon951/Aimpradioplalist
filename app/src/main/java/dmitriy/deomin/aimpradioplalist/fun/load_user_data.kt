package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.Main.Companion.NAME_USER


fun load_user_data(){
    //---------------------------------------------------------
//имя загрузим из сохранялки, а id часть имайла
    NAME_USER = save_read("name_user")
    if (save_read("id_user").isEmpty()) {
        //при первом запуске программы усановим рандомный ид
        Main.ID_USER = rnd_int(1000000, 10000000).toString()
        //и сохраним его
        save_value("id_user", Main.ID_USER)
    } else {
        Main.ID_USER = save_read("id_user")
    }
//-----------------------------------------------------
}

