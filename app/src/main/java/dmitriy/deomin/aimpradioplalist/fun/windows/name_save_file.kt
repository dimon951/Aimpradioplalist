package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.EbuchieRazreshenia
import dmitriy.deomin.aimpradioplalist.`fun`.file.saveFile
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_my_list
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

fun name_save_file(context: Context, ad: Adapter_my_list){
    //покажем оконо в котором нужно будет ввести имя
    val nsf = DialogWindow(context, R.layout.name_save_file)

    val text = nsf.view().findViewById<TextView>(R.id.textView_vvedite_name)

    val data = ArrayList<String>()

    //будем формировать вопрос удаления, если удаляется не весь список
    //и данные
    if (ad.raduoSearchList.size < ad.data.size) {
        text.text = "Сохранить выбранные: " + ad.raduoSearchList.size.toString() + " станций\nВсего(" + ad.data.size.toString() + "шт)"
    } else {
        text.text = "Сохранить весь список"
    }

    //приведем к норм виду
    val d = ad.raduoSearchList
    //запишем в строчном формате
    data.add("#EXTM3U")
    for (s in d.iterator()) {
        if (s.url.isNotEmpty()) {
            data.add("\n#EXTINF:-1," + s.name + " " + s.kbps + "\n" + s.url)
        }

    }


    val name = nsf.view().findViewById<EditText>(R.id.edit_new_name)
    name.typeface = Main.face
    name.textColor = Main.COLOR_TEXT
    // name.setText(help_name_for_save_plalist_v_file)

    (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

        if (name.text.toString().isEmpty()) {
            //пока покажем это потом будум генерерить свои если не захотят вводить
            context.toast("Введите имя")
        } else {
            //закроем окошко
            nsf.close()

            //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
            Slot(context, "File_created", false).onRun {
                //получим данные
                when (it.getStringExtra("update")) {
                    "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                    "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
                }
            }
            //сохраним  временый файл ссылку и ждём сигналы
            saveFile(name.text.toString(), data.joinToString(separator = "\n"))
        }
    }
}