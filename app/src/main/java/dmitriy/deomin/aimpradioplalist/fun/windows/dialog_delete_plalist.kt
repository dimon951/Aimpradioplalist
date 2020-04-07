package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.file.saveFile
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_my_list
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

fun dialog_delete_plalist(context:Context,ad: Adapter_my_list){
    val ddp = DialogWindow(context, R.layout.dialog_delete_plalist)
    val text = ddp.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)

    val data = ArrayList<String>()

    //будем формировать вопрос удаления, если удаляется не весь список
    //и данные
    if (ad.raduoSearchList.size < ad.data.size) {
        text.text = "Удалить выбранные: " + ad.raduoSearchList.size.toString() + " станций?\nВсего(" + ad.data.size.toString() + "шт)"
        //удалим из общего списка выбранные элементы
        val d = ad.data
        d.removeAll(ad.raduoSearchList)
        //запишем в строчном формате
        data.add("#EXTM3U")
        for (s in d.iterator()) {
            if (s.url.isNotEmpty()) {
                data.add("\n#EXTINF:-1," + s.name + " " + s.kbps + "\n" + s.url)
            }

        }
    } else {
        text.text = "Удалить весь список?"
        data.add("")
    }

    (ddp.view().findViewById<Button>(R.id.button_dialog_delete)).onClick {
        ddp.close()

        Slot(context, "File_created", false).onRun {
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    //пошлём сигнал пусть мой плейлист обновится
                    signal("Data_add").putExtra("update", "zaebis").send(context)
                }
                "pizdec" -> {
                    context.toast(context.getString(R.string.error))
                    //запросим разрешения
                    EbuchieRazreshenia()
                }
            }
        }

        //переведём наш список в норм вид
        //перезапишем и ждём ответа
        saveFile("my_plalist", data.joinToString(separator = "\n"))
    }
    (ddp.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
        ddp.close()
    }
}