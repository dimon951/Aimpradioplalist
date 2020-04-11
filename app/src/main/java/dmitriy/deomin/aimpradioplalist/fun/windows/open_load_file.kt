package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.OpenFileDialog
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.file.create_esli_net
import dmitriy.deomin.aimpradioplalist.`fun`.file.readArrayList
import dmitriy.deomin.aimpradioplalist.`fun`.file.saveArrayList
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.download_i_open_m3u_file
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_history_list
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun open_load_file(context: Context, old_data: ArrayList<Radio>) {
    //создадим папки если нет
    create_esli_net()

    //если плейлист пуст откроем окно выбора загрузки файла(память или ссылка)
    val lf = DialogWindow(context, R.layout.load_file)

    var file_m3u_custom: String

    //---------------при выборе из памяти устройства----------------------------------------------------
    (lf.view().findViewById<Button>(R.id.load_fs)).onClick {

        //посмотрим есть старый путь
        val old_dir = save_read("startdir")
        val startdir: String
        startdir = if (old_dir.length > 2) {
            old_dir
        } else {
            Main.ROOT
        }

        //----
        val fileDialog = OpenFileDialog(context)
                .setFilter(".*\\.m3u")
                .setStartDirectory(startdir)
                .setOpenDialogListener {
                    if (it != null) {
                        //файл который выбрал пользователь
                        file_m3u_custom = it
                        //закрываем файлменеджер
                        lf.close()

                        //сохраним путь ,потом тамж и откроем
                        save_value("startdir", File(file_m3u_custom).parent)

                        GlobalScope.launch {
                            //запустим анимацию
                            signal("Main_update").putExtra("signal", "start_anim_my_list").send(context)

                            //читаем выбраный файл в str
                            val file_data = read_and_pars_m3u_file(file_m3u_custom)
                            //когда прийдёт сигнал что все хорошо обновим плейлист
                            Slot(context, "File_created", false).onRun { it ->
                                //получим данные
                                when (it.getStringExtra("update")) {
                                    "zaebis" -> signal("Data_add").putExtra("update", "zaebis").send(context)
                                    "pizdec" -> Main.context.longToast(it.getStringExtra("erorr"))
                                }
                            }
                            old_data.addAll(file_data)
                            create_m3u_file("my_plalist", old_data)
                        }
                    }
                }

        fileDialog.show()
        //----
        lf.close()
    }
    //--------------------------------------------------------------------------------------------------
    //-------------------открываем ссылку -----------------------------------------------
    (lf.view().findViewById<Button>(R.id.load_url)).onClick {

        lf.close()

        //покажем окно ввода ссылки и ранею историю ввода ссылок если есть
        //сюда будем записывать переботаный стринг в хистори массив
        val d = ArrayList<History>()
        //загружаем историю из файла
        val history_url_list = readArrayList(Main.HISTORY_LINK)

        //парсим в нужный вид  и переворачиваем
        for (s in history_url_list.listIterator()) {
            if (s.length > 1) {
                val s_list = s.split("$")
                if (s_list.size > 2) {
                    d.add(0, History(s_list[0], s_list[1], s_list[2]))
                }
            }
        }
        //------------------------------------------------------------------


        //содаём диалоговое окно
        val dvvul = DialogWindow(context, R.layout.dialog_vvoda_ull_lista)

        //поменяем цвет фона ато все сливается
        (dvvul.view().findViewById<LinearLayout>(R.id.fon_dialoga_add_online_plalist)).backgroundColor = Main.COLOR_FON

        //во веьсь экран
        (dvvul.view().findViewById<TextView>(R.id.textView_logo_add)).onClick {
            dvvul.full_screen()
        }

        //история
        val r = (dvvul.view().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list_history_link))

        val e = dvvul.view().findViewById<EditText>(R.id.editText_add_list_url)
        e.typeface = Main.face
        e.textColor = Main.COLOR_TEXT
        e.hintTextColor = Main.COLOR_TEXTcontext
        e.hint = "Введите Url плейлиста"

        val e_n = (dvvul.view().findViewById<EditText>(R.id.editText_add_list_url_name))
        e_n.typeface = Main.face
        e_n.textColor = Main.COLOR_TEXT
        e_n.hintTextColor = Main.COLOR_TEXTcontext
        e_n.hint = "Имя или поеснение какоенибуть(не важно)"


        val a = Adapter_history_list(d)
        r.adapter = a

        //вставка из буфера
        (dvvul.view().findViewById<Button>(R.id.button_paste_list_url_add)).onClick { e.setText(getText_сlipboard(context)) }

        //если по истории кто кликнет то установим тот текст в эдит
        Slot(Main.context, "clik_history_item").onRun {
            val t = it.getStringExtra("url")
            val n = it.getStringExtra("name")
            if (t.isNotEmpty()) {
                e.setText(t)
            }
            if (n.isNotEmpty()) {
                e_n.setText(n)
            }

        }

        //при клике ок будем проверять на пустоту и сохранять в историю
        (dvvul.view().findViewById<Button>(R.id.button_add_list_url)).onClick {
            if (e.text.toString().isEmpty()) {
                //если пустое поле
                context.toast("Введите url")
            } else {
                val url_link = e.text.toString()
                val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss", Locale.getDefault())
                val date_time = sdf.format(Date())
                //скроем окно
                dvvul.close()

                //сохраним в историю ссылку а если есть удалим ранее добавленые
                //-----------------------------------------------------------------
                val save_mass = ArrayList<String>()
                for (s in history_url_list) {
                    //если есть акойже адрес удалим из массива его
                    if (!s.contains(url_link)) {
                        save_mass.add(s)
                    }
                }

                //Имя файла, не особо важно не буду заморачиваться
                var n = e_n.text.toString()
                if (n.isEmpty()) {
                    n = "file" + rnd_int(1, 100).toString()
                }

                save_mass.add("$n$$url_link$$date_time")
                saveArrayList(Main.HISTORY_LINK, save_mass)
                //----------------------------------------------------------

                //-----------скачиваем файл (читам его)--------
                download_i_open_m3u_file(url_link, "anim_my_list")
            }
        }
    }
    //--------------------------------------------------------------------------------------
}