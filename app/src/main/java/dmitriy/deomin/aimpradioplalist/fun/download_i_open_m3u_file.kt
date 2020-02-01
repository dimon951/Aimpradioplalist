package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

fun download_i_open_m3u_file(url: String, who_called: String) {

    //если адрес корректный
    if (!isValidURL(url)) {
        Main.context.toast("Неверный URL")
        return
    }

    //запустим анимацию
    signal("Main_update").putExtra("signal", "start_" + who_called).send(Main.context)

    GlobalScope.launch {

        //Вешаем слот который отработает один раз, и уничтожится
        Slot(Main.context, "hhtp_get",false).onRun {
            if (it.getStringExtra("data") != "error") {
                //парсим полученые данные
                val parsData = m3u_parser(it.getStringExtra("data"))
                if (who_called == "anim_my_list") {
                    //пошлём сигнал пусть мой плейлист обновится
                    signal("Data_add")
                            .putExtra("update", "zaebis")
                            .putExtra("url", url)
                            .putExtra("pars_data", parsData)
                            .send(Main.context)
                }
                if (who_called == "anim_online_plalist") {
                    //пошлём сигнал для загрузки дааных п спискок
                    signal("Online_plalist")
                            .putExtra("update", "zaebis")
                            .putExtra("url", url)
                            .putExtra("pars_data", parsData)
                            .send(Main.context)
                }
                //остановим анимацию
                signal("Main_update").putExtra("signal", "stop_" + who_called).send(Main.context)
            } else {
                //если ошибка остановим анимацию
                signal("Main_update").putExtra("signal", "stop_" + who_called).send(Main.context)
            }
        }


        //качаем файл , как скачается сработает слот hhtp_get
        hhtp_get(url)
    }
}