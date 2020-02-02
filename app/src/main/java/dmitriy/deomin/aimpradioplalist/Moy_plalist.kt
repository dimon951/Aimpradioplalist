package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.email
import org.jetbrains.anko.support.v4.share
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Moy_plalist : Fragment() {

    lateinit var ad: Adapter_my_list

    companion object {
        var position_list_my_plalist = 0
        //список файлов которые загружались онлайн
        var list_move_history: ArrayList<String> = ArrayList()
        //файл который загружен сейчас
        var open_file = ""
    }

    @SuppressLint("WrongConstant", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.my_plalist, null)
        val context: Context = Main.context

        val find = v.findViewById<EditText>(R.id.editText_find_my_list)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

        position_list_my_plalist = save_read_int("position_list_my_plalist")

        val recikl_list = v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recicl_my_list)
        recikl_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        //полоса быстрой прокрутки
        val fastScroller = v.findViewById<VerticalRecyclerViewFastScroller>(R.id.fast_scroller)
        //получим текущие пораметры
        val paramL = fastScroller.layoutParams
        //меняем ширину
        paramL.width = Main.SIZE_WIDCH_SCROLL
        //устанавливаем
        fastScroller.layoutParams = paramL
        fastScroller.setRecyclerView(recikl_list)
        recikl_list.setOnScrollListener(fastScroller.onScrollListener)

        val update_list = v.findViewById<Button>(R.id.button_close_list)

        //будем слушать эфир постоянно если че обновим список
        //----------------------------------------------------------------------------
        Slot(context, "Data_add").onRun { it ->
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    if (!it.getStringExtra("listfile").isNullOrEmpty()) {
                        if (it.getStringExtra("listfile") == "old") {
                            //оставим все как есть если не открыт мой список
                            if (open_file != Main.MY_PLALIST) {
                                context.toast("добавлено")
                            }
                        } else {
                            //иначе обновим мой список
                            update_list.visibility = View.VISIBLE
                            open_file = it.getStringExtra("listfile")
                            list_move_history.add(open_file)
                        }
                    } else {
                        update_list.visibility = View.GONE
                        open_file = Main.MY_PLALIST
                    }

                    //заново все сделаем
                    val d = read_and_pars_m3u_file(open_file)
                    ad = Adapter_my_list(d)
                    recikl_list.adapter = ad
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list_my_plalist < d.size && position_list_my_plalist >= 0) {
                        recikl_list.scrollToPosition(position_list_my_plalist)
                    }

                    //остановим анимацию
                    signal("Main_update").putExtra("signal", "stop_anim_my_list").send(context)


                    //скроем или покажем полосу прокрутки и поиск
                    if (d.size > Main.SIZE_LIST_LINE) {
                        fastScroller.visibility = View.VISIBLE

                        find.visibility = View.VISIBLE
                        // текст только что изменили в строке поиска
                        find.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable) {}
                            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                                ad.filter.filter(text)
                            }
                        })
                    } else {
                        fastScroller.visibility = View.GONE

                        find.setText("")
                        find.visibility = View.GONE
                    }
                    //==================================================================
                }

                "move_back" -> {
                    //если в истории чтото есть вообще
                    if (list_move_history.isNotEmpty()) {
                        //Если в истории что то есть
                        if (list_move_history.size > 1) {
                            Log.e("ttt", list_move_history.toString())
                            //удаляем текущию открытую страницу
                            list_move_history.removeAt(list_move_history.size - 1)
                            //передаём предыдующию
                            Log.e("ttt", list_move_history.toString())
                            open_file = list_move_history.last()
                        }else{
                            if (list_move_history.size == 1) {
                                open_file = Main.MY_PLALIST
                                update_list.visibility = View.GONE
                                list_move_history.clear()
                            }
                        }
                    } else {
                        update_list.visibility = View.GONE
                        list_move_history.clear()
                        open_file = ""
                        return@onRun
                    }

                    //заново все сделаем
                    val d = read_and_pars_m3u_file(open_file)
                    ad = Adapter_my_list(d)
                    recikl_list.adapter = ad
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list_my_plalist < d.size && position_list_my_plalist >= 0) {
                        recikl_list.scrollToPosition(position_list_my_plalist)
                    }

                    //остановим анимацию
                    signal("Main_update").putExtra("signal", "stop_anim_my_list").send(context)

                    //скроем или покажем полосу прокрутки и поиск
                    if (d.size > Main.SIZE_LIST_LINE) {
                        fastScroller.visibility = View.VISIBLE

                        find.visibility = View.VISIBLE
                        // текст только что изменили в строке поиска
                        find.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable) {}
                            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                                ad.filter.filter(text)
                            }
                        })
                    } else {
                        fastScroller.visibility = View.GONE

                        find.setText("")
                        find.visibility = View.GONE
                    }
                    //==================================================================
                }
            }
        }
        //-------------------------------------------------------------------------------------

        //Слушаем кнопки
        //===========================================================================================
        //------------удалить(очистить весь плейлист)---------------------------------------------
        (v.findViewById<Button>(R.id.button_delete)).onClick {

            //если список совсем не пуст (есть пояснялка но ссылок нет)
            if (ad.raduoSearchList.isNotEmpty()) {
                //если список не пуст
                if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {


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
                        File_function().SaveFile("my_plalist", data.joinToString(separator = "\n"))
                    }
                    (ddp.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
                        ddp.close()
                    }

                } else {
                    context.toast("Плейлист пуст")
                }
            } else {

                val ddp = DialogWindow(context, R.layout.dialog_delete_plalist)
                val text = ddp.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
                text.text = "Плейлист пуст(ошибка парсинга) очистить файл ?"

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

                    //перезапишем и ждём ответа
                    File_function().SaveFile("my_plalist", "")
                }
                (ddp.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
                    ddp.close()
                }
            }

        }
        //-----------------------------------------------------------------------------------------

        //-------------добавить свой поток(имя и адрес)-----------------------------------------
        (v.findViewById<Button>(R.id.button_add_url)).onClick {

            val auu = DialogWindow(context, R.layout.add_url_user)

            val edit = auu.view().findViewById<EditText>(R.id.editText_add_url)
            edit.typeface = Main.face
            edit.textColor = Main.COLOR_TEXT

            val edit_name = auu.view().findViewById<EditText>(R.id.editText_add_url_name)
            edit_name.typeface = Main.face
            edit_name.textColor = Main.COLOR_TEXT

            (auu.view().findViewById<Button>(R.id.button_paste_url_add)).onClick { edit.setText(getText_сlipboard(context)) }

            (auu.view().findViewById<Button>(R.id.button_add_url)).onClick {

                //проверим на пустоту
                if (edit.text.toString().length > 7) {

                    //проверим есть ли в начале ссылки http:// или "https://" - ато от неё много чего зависит
                    if (edit.text.toString().substring(0, 7) == "http://" || edit.text.toString().substring(0, 8) == "https://") {


                        Slot(context, "File_created", false).onRun {
                            //получим данные
                            val s = it.getStringExtra("update")
                            when (s) {
                                "est" -> context.toast("Такая станция уже есть в плейлисте")
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
                        //делаем
                        File_function().Add_may_plalist_stansiy(edit.text.toString(), edit_name.text.toString())
                        auu.close()
                    } else {
                        edit.setText("http://" + edit.text.toString())
                        context.toast("В начале ссылки потока должна быть http://, добавил , повторите :)")
                    }

                } else {
                    context.toast("Нечего добавлять")
                }
            }
        }
        //-----------------------------------------------------------------------------------------

        //-------------сохранить этот плейлист в отдельный файл------------------------------------
        (v.findViewById<Button>(R.id.save_v_file)).onClick {
            //если список не пуст
            if (ad.raduoSearchList[0].name != (Main.PUSTO)) {
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
                        //сохраним  временый файл ссылку и ждём сигналы
                        File_function().SaveFile(name.text.toString(), data.joinToString(separator = "\n"))
                    }
                }
            } else {
                context.toast("Нечего сохранять добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------------------

        //-------------открыть из памяти устройства или по ссылке плейлист--------------------------------------
        (v.findViewById<Button>(R.id.load_file)).onClick {
            //если в плейлисте есть чето предложим чегонибуть
            if (read_and_pars_m3u_file(Main.MY_PLALIST)[0].name != Main.PUSTO) {

                val vponf = DialogWindow(context, R.layout.vopros_pri_otkritii_new_file)

                //------------------открыть из памяти---------------------------------------------
                //затираем старое
                (vponf.view().findViewById<Button>(R.id.button_dell_old_plalist)).onClick {

                    //отправим с пустым старым текстом , старое затрётся
                    open_load_file(context, arrayListOf(Radio(name = "", url = "")))
                    //закрываем окошко
                    vponf.close()
                }

                //добавляем к старому если есть дубликаты пропустим их
                (vponf.view().findViewById<Button>(R.id.button_add_old_plalist)).onClick {
                    //после выбора файла он прочётся и добавится к старым данным
                    open_load_file(context, read_and_pars_m3u_file(Main.MY_PLALIST))
                    //закрываем окошко
                    vponf.close()
                }

            } else {
                open_load_file(context, arrayListOf(Radio(name = Main.PUSTO, url = "")))
            }
            //---------------------------------------------------------------------------------
        }
        //------------------------------------------------------------------------------------

        //-------------при долгом нажатии будем открывать папку программы-------------------
        (v.findViewById<Button>(R.id.load_file)).onLongClick {

            val fileDialog = OpenFileDialog(context)
                    .setFilter(".*\\.m3u")
                    .setStartDirectory(Main.ROOT)
                    .setEnablButton(false)
            fileDialog.show()
        }
        //-----------------------------------------------------------------------------------

        //------------открыть в плеере------------------------------------------------------
        (v.findViewById<Button>(R.id.open_aimp)).onClick {
            //если список не пуст
            if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                val name_file = "Плэйлист с " + ad.raduoSearchList.size.toString() + " станциями"
                //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        "zaebis" -> {
                            play_aimp(it.getStringExtra("name"), "")
                        }
                        "pizdec" -> {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            EbuchieRazreshenia()
                        }
                    }
                }
                create_m3u_file(name_file, ad.raduoSearchList)
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------

        //---------------открыть в системе----------------------------------------
        (v.findViewById<Button>(R.id.open_aimp)).onLongClick {
            //если список не пуст
            if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                val name_file = "Плэйлист с " + ad.raduoSearchList.size.toString() + " станциями"
                //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        "zaebis" -> {
                            play_system(it.getStringExtra("name"), "")
                        }
                        "pizdec" -> {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            EbuchieRazreshenia()
                        }
                    }
                }
                create_m3u_file(name_file, ad.raduoSearchList)
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //----------------------------------------------------------------------------------

        //------------поделится---------------------------------------------------------
        (v.findViewById<Button>(R.id.button_otpravit)).onClick {

            //если список не пуст
            if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                //приведем к норм виду
                val d = ad.raduoSearchList
                val data = ArrayList<String>()
                //запишем в строчном формате
                data.add("#EXTM3U")
                for (s in d.iterator()) {
                    if (s.url.isNotEmpty()) {
                        data.add("\n#EXTINF:-1," + s.name + " " + s.kbps + "\n" + s.url)
                    }
                }
                share(data.joinToString("\n"))
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //----------------------------------------------------------------------------------

        //-----при долгом нажатиии будем предлогать отправить мне письмом этот плейлист----
        (v.findViewById<Button>(R.id.button_otpravit)).onLongClick {
            //если список не пуст
            if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                //приведем к норм виду
                val data = ArrayList<String>()
                val d = ad.raduoSearchList
                //запишем в строчном формате
                data.add("#EXTM3U")
                for (s in d.iterator()) {
                    if (s.url.isNotEmpty()) {
                        data.add("\n#EXTINF:-1," + s.name + " " + s.kbps + "\n" + s.url)
                    }
                }
                email("deomindmitriy@gmail.com", "aimp_radio_plalist", data.joinToString("\n"))
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------

        //Кнопка обновления(появляется когда открывается список из ссылки)
        update_list.onClick {
            signal("Data_add").putExtra("update", "move_back").send(context)
        }
        //======================================================================================

        //пошлём сигнал для загрузки дааных п спискок
        signal("Data_add").putExtra("update", "zaebis").send(context)

        return v
    }

    private fun open_load_file(context: Context, old_data: ArrayList<Radio>) {
        //создадим папки если нет
        File_function().create_esli_net()

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
            val history_url_list = File_function().readArrayList(Main.HISTORY_LINK)

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
                    File_function().saveArrayList(Main.HISTORY_LINK, save_mass)
                    //----------------------------------------------------------

                    //-----------скачиваем файл (читам его)--------
                    download_i_open_m3u_file(url_link, "anim_my_list")
                }
            }
        }
        //--------------------------------------------------------------------------------------
    }
}