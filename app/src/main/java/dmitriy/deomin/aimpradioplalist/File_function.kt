package dmitriy.deomin.aimpradioplalist

import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.ArrayList

class File_function {

    /* Проверяет, доступно ли external storage для чтения и записи */
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            Log.e("ggggggoooopaaa", (Environment.MEDIA_MOUNTED == state).toString())
            return Environment.MEDIA_MOUNTED == state

        }

    //прочитает файл из памяти устройства и вернёт массив радиопотоков распарсеный вида
    //Hip-Hop Barada\nhttp://listen1.myradio24.com:9000/4455
    //....
    //или текст пояснялку если нет нечего
    fun My_plalist(url: String): Array<String> {

        //прочитаем старыйе данные
        var text = ""
        try {
            text = read(url)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //удалим мусор
        val kontent = text
                .replace("\n", "")
                .replace(" ", "")
                .replace("#EXTM3U", "")

        //если есть чё разобьём на массив и вернём после удаления пустых строк
        if (kontent.length > 11) {

            //удалим вначале файла тег
            text = text.replace("#EXTM3U", "")

            //удалим все переносы чтоб мозги не парить дальше
            text = text.replace("\n".toRegex(), "")

            //добавим переносы в нужных местах
            //бывает http:// или https:// меняем все блять
            text = text.replace("https://".toRegex(), "\nhttps://")
            text = text.replace("http://".toRegex(), "\nhttp://")


            //скинем все сюда , а потом обратно
            val mas = ArrayList<String>()

            //разделим стороку на масссив через #EXTINF:-1,
            for (s in text.split("#EXTINF:-1,".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                //если строка не пустая и не содержит перенос
                if ((s != "") and (s != "\n")) {
                    mas.add(s)
                }
            }

            // конвертируем ArrayList в список
            val myArray: Array<String>
            myArray = mas.toTypedArray()


            return myArray
        } else {
            //врнём свой моссив с подсказкой
            return arrayOf(Main.PUSTO)
        }


    }


    //очистим наш плейлист
    fun Delet_my_plalist() {
        SaveFile_vizov("my_plalist.m3u", "")
    }

    //удаление одной станции из файла
    fun Delet_one_potok(potok: String) {
        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //в параметрах получаем строку вида
        //Авторадио\nhttp://ic7.101.ru:8000/v3_1

        //составим исходный вид строки потока(как в файле записано)
        //#EXTINF:-1,Авторадио\nhttp://ic7.101.ru:8000/v3_1
        val del_potok = "#EXTINF:-1,$potok"

        //теперь удаляем эту вещь из считаного файла и перезаписываем его
        old_text = old_text.replace(del_potok, "")

        //очищаем и записываем заново, там уже будут слаться сигналы получилось или нет
        SaveFile_vizov("my_plalist.m3u", old_text)
    }

    //переименование
    fun Rename_potok(potok_old: String, potok_new: String) {
        //прочитаем старыйе данные
        var old_text = ""
        try {
            old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //в параметрах получаем строку вида
        //Авторадио\nhttp://ic7.101.ru:8000/v3_1

        //теперь заменяем старый поток из считаного файла на новый и перезаписываем его
        old_text = old_text.replace(potok_old, potok_new)

        //очищаем и записываем заново, там уже будут слаться сигналы получилось или нет
        SaveFile_vizov("my_plalist.m3u", old_text)
    }

    //сохраняется одна станция в файле
    fun Save_temp_file(name: String, link: String) {

        //создадим папки если нет
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/")
        if (!sddir.exists()) {
            sddir.mkdirs()
        }
        //создадим файл, если есть перезапишется
        SaveFile_vizov(name, link)
    }

    //добавляется в текущему плейлисту ещё станцию
    fun Add_may_plalist_stansiy(link: String, name: String) {

        //создадим папки если нет
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/")
        if (!sddir.exists()) {
            sddir.mkdirs()
        }

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
                    val i = Intent("File_created")
                    i.putExtra("update", "zaebis")
                    Main.context.sendBroadcast(i)
                    Toast.makeText(Main.context, "Такая станция уже есть в плейлисте", Toast.LENGTH_LONG).show()
                    return
                }
            }
            //если цикл прошёл мимо то добавим станцию
            SaveFile_vizov("my_plalist.m3u", "$old_text\n#EXTINF:-1,$name\n$link")
        } else {
            //если наш плейлист пуст добавим в начале файла #EXTM3U
            SaveFile_vizov("my_plalist.m3u", "#EXTM3U\n#EXTINF:-1,$name\n$link")
        }

    }

    //чтение файла
    @Throws(FileNotFoundException::class)
    fun read(fileName: String): String {

        //создадим папки если нет
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/")
        if (!sddir.exists()) {
            sddir.mkdirs()
        }

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


    //Вызов функции SaveFile, который выполняет задачу сохранения файла в External-носителе:
    private fun SaveFile_vizov(filename: String, link_text: String) {
        val fullpath: String = Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + filename
        //Сохранение файла на External Storage:
        if (isExternalStorageWritable) {
            SaveFile(fullpath, link_text)
        } else {
            Toast.makeText(Main.context, "Нет доступа к памяти", Toast.LENGTH_LONG).show()
        }
    }

    //Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
    fun SaveFile(filePath: String, FileContent: String) {
        //Создание объекта файла.
        val fhandle = File(filePath)
        try {
            //Если файл существует, то он будет перезаписан:
            fhandle.createNewFile()
            val fOut = FileOutputStream(fhandle)
            val myOutWriter = OutputStreamWriter(fOut, Main.File_Text_Code)
            myOutWriter.write(FileContent)
            myOutWriter.close()
            fOut.close()

            //послать сигнал
            val i = Intent("File_created")
            i.putExtra("update", "zaebis")
            Main.context.sendBroadcast(i)
        } catch (e: IOException) {

            //послать сигнал
            val i = Intent("File_created")
            i.putExtra("update", "pizdec")
            Main.context.sendBroadcast(i)
        }

    }


}
