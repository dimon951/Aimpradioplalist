package dmitriy.deomin.aimpradioplalist

import android.os.Environment
import android.widget.Toast
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import kotlin.collections.ArrayList


class File_function {

    /* Проверяет, доступно ли external storage для чтения и записи */
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
    //удаление одной станции из файла
    //в параметрах получаем строку вида
    //Авторадио\nhttp://ic7.101.ru:8000/v3_1
    fun Delet_one_potok(potok: String, file_url: String) {
        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = readFile(file_url)
            // old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        //удалм переносы,потом при сохранении они подставятся
        old_text = old_text.replace("\n", "")
        //добавим переносы
        old_text = old_text.replace("http://", "\nhttp://")
        old_text = old_text.replace("https://", "\nhttps://")

        //исходный вид строки потока(как в файле записано)
        //#EXTINF:-1,Авторадио\nhttp://ic7.101.ru:8000/v3_1
        //теперь удаляем эту вещь из считаного файла и перезаписываем его
        old_text = old_text.replace("#EXTINF:-1,$potok", "")

        //очищаем и записываем заново, там уже будут слаться сигналы получилось или нет
        SaveFile("my_plalist", old_text)
    }

    //переименование
    fun Rename_potok(potok_old: String, potok_new: String, file_url: String) {
        //прочитаем старыйе данные
        var old_text = ""

        //прочитали файл как есть
        try {
            //  old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
            old_text =readFile(file_url)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //в параметрах получаем строку вида
        //Авторадио\nhttp://ic7.101.ru:8000/v3_1
        //теперь заменяем старый поток из считаного файла на новый и перезаписываем его
        old_text = old_text.replace(potok_old, potok_new)


        //очищаем и записываем заново, там уже будут слаться сигналы получилось или нет
        SaveFile( "my_plalist", old_text)
    }

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

    fun create_esli_net() {
        if (isExternalStorageWritable) {
            val sddir = File(Main.ROOT)
            if (!sddir.exists()) {
                sddir.mkdirs()
            }
        } else {
            Toast.makeText(Main.context, "Нет доступа к памяти", Toast.LENGTH_LONG).show()
            return
        }
    }

    //Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
    fun SaveFile(name: String, data: String) {
        GlobalScope.launch {
            //проверим наличие нашей папки  и доспуп к ней
            create_esli_net()

            val all_name = Main.ROOT +name+".m3u"

            val writer = FileWriter(all_name, false)
            try {
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

            val writer = FileWriter(Main.ROOT + name, false)
            try {
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
