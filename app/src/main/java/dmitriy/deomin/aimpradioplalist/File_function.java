package dmitriy.deomin.aimpradioplalist;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class File_function {


    //прочитает файл из памяти устройства и вернёт массив радиопотоков
    public  String[] My_plalist() {

        //прочитаем старыйе данные
        String text = "";
        try {
            text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //если есть чё разобьём на массив и вернём после удаления пустых строк
        if(text.length()>11){

            //скинем все сюда , а потом обратно
            ArrayList<String> mas = new ArrayList<>();

            //разделим стороку на масссив
            for(String s:text.split("#EXTINF:-1,")){

                //если строка не пустая
                if(!s.equals("")&!s.equals("\n")){
                        //удалим вначале файла тег
                        s =  s.replace("#EXTM3U","");
                        mas.add(s);
                }
            }

            // конвертируем ArrayList в список
            String[] myArray = {};
            myArray = mas.toArray(new String[0]);


            return myArray;
        }else {
            //врнём свой моссив с подсказкой
            String [] info = {"Плейлист пуст. Чтобы добавить станцию , выберите <добавить в плейлист> во <Всё радио>. Или нажмите + в <Популярное> долгий тап на станции предложит удалить её"};
            return  info;
        }



    }

    //очистим наш плейлист
    public void Delet_my_plalist() {
        SaveFile_vizov("my_plalist.m3u","");
    }

//сохраняется одна станция в файле
    public void Save_temp_file(String name,String link){

        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }
        //создадим файл, если есть перезапишется
        SaveFile_vizov(name,link);
    }

     //добавляется в текущему плейлисту ещё станцию
    public  void Add_may_plalist_stansiy(String link,String name) {

        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }

        //прочитаем старыйе данные
        String old_text = "";
        try {
            old_text = read(Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        if(old_text.length()>11){
           //разобьём всю кучу
            String mas[] = old_text.split("\n");
            for(String s:mas){
                //если такая уже есть выходим и шлём сигнал что закрылось окошко
                if(s.equals(link)){
                    //послать сигнал
                    Intent i  = new Intent("File_created");
                    i.putExtra("update","zaebis");
                    Main.Companion.getContext().sendBroadcast(i);
                    Toast.makeText(Main.Companion.getContext(),"Такая станция уже есть в плейлисте",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            //если цикл прошёл мимо то добавим станцию
            SaveFile_vizov("my_plalist.m3u",old_text+"\n"+"#EXTINF:-1,"+name+"\n"+link);
        }else {
            //если наш плейлист пуст добавим в начале файла #EXTM3U
            SaveFile_vizov("my_plalist.m3u","#EXTM3U"+"\n"+"#EXTINF:-1,"+name+"\n"+link);
        }

    }

//чтение файла
private String read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        //проверим есть он вообще
        File file = new File(fileName);
        if (!file.exists()){
            //если нет вернём пустоту
           return "";
        }

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();
    }


    //Вызов функции SaveFile, который выполняет задачу сохранения файла в External-носителе:
   private void SaveFile_vizov(String filename,String link_text)
    {
        String fullpath;
        //Сохранение файла на External Storage:
        fullpath = Environment.getExternalStorageDirectory().toString() +  "/aimp_radio/"+filename;
        Log.i("TTT",fullpath);
        if (isExternalStorageWritable())
        {
            SaveFile(fullpath, link_text);
        }else{
            Log.i("TTT","в память нельзя писать");
        }
    }

    //Функция, которая сохраняет файл, принимая полный путь до файла filePath и сохраняемый текст FileContent:
    public void SaveFile (String filePath, String FileContent)
    {
        //Создание объекта файла.
        File fhandle = new File(filePath);
        try
        {
            //Если файл существует, то он будет перезаписан:
            fhandle.createNewFile();
            FileOutputStream fOut = new FileOutputStream(fhandle);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(FileContent);
            myOutWriter.close();
            fOut.close();

            //послать сигнал
            Intent i  = new Intent("File_created");
            i.putExtra("update","zaebis");
            Main.Companion.getContext().sendBroadcast(i);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            Log.i("TTT","Path " + filePath + ", " + e.toString());

            //послать сигнал
            Intent i  = new Intent("File_created");
            i.putExtra("update","pizdec");
            Main.Companion.getContext().sendBroadcast(i);
        }
    }

    /* Проверяет, доступно ли external storage для чтения и записи */
    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }


}
