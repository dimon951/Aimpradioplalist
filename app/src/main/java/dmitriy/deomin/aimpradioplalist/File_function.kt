package dmitriy.deomin.aimpradioplalist

import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.ArrayList
import java.util.regex.Pattern

class File_function {

    /* Проверяет, доступно ли external storage для чтения и записи */
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    //прочитает файл из памяти устройства и вернёт массив радиопотоков распарсеный вида
    //Hip-Hop Barada\nhttp://listen1.myradio24.com:9000/4455
    //....
    //или текст пояснялку если нет нечего
    fun My_plalist(url: String): ArrayList<String> {

        if (File(url).exists()) {
            //прочитаем старыйе данные
            var kontent = ""
            try {
                kontent = read(url)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }


            //если есть чё разобьём на массив и вернём после удаления пустых строк
            if (kontent.length > 11) {

                //скинем все сюда , а потом обратно
                val mas = ArrayList<String>()

                //посмотрим че там есть #EXTINF:-1, или #EXTINF:0,
                var textSplit = ""

                if (kontent.contains("#EXTINF:-1,")) {
                    textSplit = "#EXTINF:-1,"
                }
                if (kontent.contains("#EXTINF:0,")) {
                    textSplit = "#EXTINF:0,"
                }


                //и разделим стороку на масссив через
                for (s in kontent.split(textSplit.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    //если строка не пустая и не содержит перенос
                    if ((s != "") && (s != "\n")) {
                        mas.add(s)
                    }
                }
                return mas
            } else {
                //врнём свой моссив с подсказкой
                val pusto = ArrayList<String>()
                pusto.add(Main.PUSTO)
                return pusto
            }
        } else {
            //врнём свой моссив с подсказкой
            val pusto = ArrayList<String>()
            pusto.add(Main.PUSTO)
            return pusto
        }
    }

    //удаление одной станции из файла
    //в параметрах получаем строку вида
    //Авторадио\nhttp://ic7.101.ru:8000/v3_1
    fun Delet_one_potok(potok: String, file_url: String) {
        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = read(file_url)
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
        SaveFile(Main.ROOT + "my_plalist.m3u", old_text)
    }

    //переименование
    fun Rename_potok(potok_old: String, potok_new: String, file_url: String) {
        //прочитаем старыйе данные
        var old_text = ""

        //прочитали файл как есть
        try {
            //  old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
            old_text = read(file_url)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //в параметрах получаем строку вида
        //Авторадио\nhttp://ic7.101.ru:8000/v3_1
        //теперь заменяем старый поток из считаного файла на новый и перезаписываем его
        old_text = old_text.replace(potok_old, potok_new)


        //очищаем и записываем заново, там уже будут слаться сигналы получилось или нет
        SaveFile(Main.ROOT + "my_plalist.m3u", old_text)
    }

    //добавляется в текущему плейлисту ещё станцию
    fun Add_may_plalist_stansiy(link: String, name: String) {

        //создадим папки если нет
        create_esli_net()

        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }


        //нужно записывать файл плейлиста в нор виде со всеми закорючками
        //#EXTM3U
        //#EXTINF:-1,bbc_radio_one
        //http://as-hls-ww-live.akamaized.net/pool_7/live/bbc_radio_one/bbc_radio_one.isml/bbc_radio_one-audio%3d320000.norewind.m3u8
        //#EXTINF:-1,bbc_1xtra
        //http://as-hls-ww-live.akamaized.net/pool_7/live/bbc_1xtra/bbc_1xtra.isml/bbc_1xtra-audio%3d320000.norewind.m3u8


        //link - ссыка на поток
        //name - название станции


        //если эта станция уже есть забьём
        if (old_text.length > 11) {
            //разобьём всю кучу
            val mas = old_text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in mas) {
                //если такая уже есть выходим и шлём сигнал что закрылось окошко
                if (s == link) {
                    //послать сигнал
                    signal("File_created").putExtra("update", "est").send(Main.context)
                    return
                }
            }
            //если цикл прошёл мимо то добавим станцию
            SaveFile(Main.ROOT + "my_plalist.m3u", "$old_text\n#EXTINF:-1,$name\n$link")
        } else {
            //если наш плейлист пуст добавим в начале файла #EXTM3U
            SaveFile(Main.ROOT + "my_plalist.m3u", "#EXTM3U\n#EXTINF:-1,$name\n$link")
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

    //чтение файла
    @Throws(FileNotFoundException::class)
    fun read(fileName: String): String {

        //создадим папки если нет
        create_esli_net()

        //Этот спец. объект для построения строки
        val sb = StringBuilder()

        //проверим есть он вообще
        val file = File(fileName)
        if (!file.exists()) {
            //если нет вернём пустоту
            return ""
        } else {
            try {
                //Объект для чтения файла в буфер(хер просыш че тут творится)
                val reader = BufferedReader(FileReader(file.absoluteFile))
                reader.use { reader ->
                    //В цикле построчно считываем файл
                    var s: String? = null
                    while ({ s = reader.readLine(); s }() != null) {
                        sb.append(s)
                        sb.append("\n")
                    }
                }
            } catch (e: IOException) {
                //если нет доступа или еще чего там вернём пусоту
                return ""
            }

            //Возвращаем полученный текст с файла
            return sb.toString()
        }
    }


    //Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
    fun SaveFile(filePath: String, k: String) {

        //проверим наличие нашей папки  и доспуп к ней
        create_esli_net()

        //посмотрим что это за файл может хрень какая , то ошибу покакжем
        //если пустота значит надо файл оччистить
        if (k.contains("http") || k == "") {

            Log.e("ttt","1")

            //разобьём на список через перенос и удалим мусорные строчки
            //тут нужно делать поиск в каждой строчке так как там разные получаются запросы
            var newkontent = ""
            if(k.contains("#EXTGRP")){
                val lkontent = k.split("\n")
                for (l in lkontent.listIterator()) {
                    newkontent += if (l.contains("#EXTGRP")) {
                        val d = l.substringAfter("#EXTGRP").substringBefore("http://")
                        l.replace("#EXTGRP$d", "")
                    } else {
                        l
                    }
                }
            }else{
              newkontent = k
            }


            Log.e("ttt","2")

            //удалим мусор и приведём все к одному виду
            var kontent = newkontent
                    .replace("\n", "")
                    .replace("'", "")
                    .replace("&", "")
                    .replace("\"", "")
                    .replace("#EXTM3U", "")
                    .replace("#EXTINF:0,", "#EXTINF:0")
                    .replace("#EXTINF:0", "#EXTINF:0,")
                    .replace("#EXTINF:-1,", "#EXTINF:-1")
                    .replace("#EXTINF:-1", "#EXTINF:-1,")
                    .replace("group-title=", "")

            if (kontent.contains("#PLAYLIST")) {
                //получим мусорную строку Типа #PLAYLIST:101.RU-Профессиональное радио
                val d = kontent.substringAfter("#PLAYLIST").substringBefore("#EXTINF:")
                kontent = kontent.replace("#PLAYLIST$d", "")
            }
            if (kontent.contains("#EXTVLCOPT")) {
                //получим мусорную строку Типа #EXTVLCOPT:http-user-agent=PotokovoeRu
                val d = kontent.substringAfter("#EXTVLCOPT").substringBefore("http://")
                kontent = kontent.replace("#EXTVLCOPT$d", "")
            }


            //добавим переносы в станции
            kontent = kontent
                    .replace("https://".toRegex(), "\nhttps://")
                    .replace("http://".toRegex(), "\nhttp://")

            //добавим переносы между станций
            kontent = kontent.replace("#EXTINF:-1,", "\n#EXTINF:-1,")
            kontent = kontent.replace("#EXTINF:0,", "\n#EXTINF:0,")

            Log.e("ttt","3")



            //захуярим пока костылём проверочку еще одну
            //заменим все #EXTINF с непонятным хламом на норм вид #EXTINF:-1,
            //1-найдём весь храм а потом заменим его
//            val list = kontent.split("#")
//            var new_kontent = ""
//            for (l in list.iterator()) {
//                //составим новый список приведёный к одному виду
//                if (l.contains(",")) {
//                    new_kontent = new_kontent + "#EXTINF:-1," + l.substring(l.indexOf(",") + 1, l.length)
//                }
//            }


            Log.e("ttt","zapis v file")
            val writer = FileWriter(filePath, false)
            try {
                writer.write(kontent)
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
                        .send(Main.context)
            }
        }
    }

    //запись в файл
    fun writeToFile(name: String, str: String) {
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

    fun readFile() {
        try {
            val read = FileReader("text.txt")
            println(read.readText())
        } catch (e: Exception) {
            println(e.message)
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
        val fsave = read(Main.ROOT + f_name)
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
