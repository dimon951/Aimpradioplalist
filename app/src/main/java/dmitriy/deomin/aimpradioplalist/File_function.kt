package dmitriy.deomin.aimpradioplalist

import dmitriy.deomin.aimpradioplalist.`fun`.file.clear_name_ot_chlama
import dmitriy.deomin.aimpradioplalist.`fun`.file.create_esli_net
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import kotlin.collections.ArrayList


class File_function {

    //добавляется в текущему плейлисту ещё станцию
    fun Add_may_plalist_stansiy(link: String, name: String) {

        //создадим папки если нет
        create_esli_net()

        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = readFile(Main.MY_PLALIST)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        if (old_text.length > Main.PUSTO.length) {
            //разобьём всю кучу
            val mas = old_text.lines()
            var data:ArrayList<Radio>
            for (s in mas) {
                //если такая уже есть выходим и шлём сигнал что закрылось окошко
                if (s == link) {
                    //послать сигнал
                    signal("File_created").putExtra("update", "est").send(Main.context)
                    return
                }
            }
            //если цикл прошёл мимо то добавим станцию
            SaveFile("my_plalist","$old_text\n#EXTINF:-1,$name\n$link")
        } else {
            //если наш плейлист пуст добавим в начале файла #EXTM3U
            SaveFile( "my_plalist", "#EXTM3U\n#EXTINF:-1,$name\n$link")
        }

    }


    //Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
    fun SaveFile(name: String, data: String) {
        GlobalScope.launch {
            //проверим наличие нашей папки  и доспуп к ней
            create_esli_net()

            val all_name = Main.ROOT + clear_name_ot_chlama(name)+".m3u"

            lateinit var writer:FileWriter

            try {
                writer = FileWriter(all_name, false)
                writer.write(data)
            } catch (ex: Exception) {
                //послать сигнал
                signal("File_created")
                        .putExtra("run", false)
                        .putExtra("update", "pizdec")
                        .send(Main.context)
            } finally {
                writer.close()
                //послать сигнал
                signal("File_created")
                        .putExtra("run", false)
                        .putExtra("update", "zaebis")
                        .putExtra("name",all_name)
                        .send(Main.context)
            }
        }
    }


    //запись в файл
    fun writeToFile(name: String, str: String) {
        GlobalScope.launch {
            create_esli_net()
            lateinit var writer:FileWriter
            try {
                writer = FileWriter(Main.ROOT + name, false)
                writer.write(str + "\n")
            } catch (ex: Exception) {
                println(ex.message)
                //послать сигнал
                signal("File_created_save_vse")
                        .putExtra("run", false)
                        .putExtra("update", "pizdec")
                        .putExtra("anim", "anim_of")
                        .send(Main.context)
            } finally {
                writer.close()
                //послать сигнал
                signal("File_created_save_vse")
                        .putExtra("run", false)
                        .putExtra("update", "zaebis")
                        .putExtra("anim", "anim_of")
                        .send(Main.context)
            }
        }
    }

    fun readFile(fileName: String): String {
        //создадим папки если нет
        create_esli_net()
        //проверим есть он вообще
        val file = File(fileName)
        return if (!file.exists()) {
            //если нет вернём пустоту
            ""
        } else {
            try {
              FileReader(fileName).readText()
            } catch (e: Exception) {
               ""
            }
        }
    }


    //сохранение и чение списка из памяти
    fun saveArrayList(f_name: String, data: ArrayList<String>) {
        GlobalScope.launch {
            //перегоним data в строку с переносами
            var save_text = ""
            for (t in data.listIterator()) {
                save_text = save_text + "\n" + t
            }
            //сохраняем
            writeToFile(f_name, save_text)
        }
    }

    fun readArrayList(f_name: String): ArrayList<String> {
        //смотрим не пустой ли файл , читаем и отправляем
        val fsave = readFile(Main.ROOT + f_name)
        if (fsave == "") {
            val nechego = ArrayList<String>()
            nechego.add("")
            return nechego
        }
        //если там чтото есть разбиваем по пробелу и отправляем
        val list = fsave.split("\n")
        val chtoto = ArrayList<String>()
        for (s in list.listIterator()) {
            if (s.length > 7) {
                chtoto.add(s)
            }

        }
        return chtoto
    }


}
