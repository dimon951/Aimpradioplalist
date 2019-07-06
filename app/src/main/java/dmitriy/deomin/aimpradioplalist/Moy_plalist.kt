package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import com.github.kittinunf.fuel.httpGet
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.email
import org.jetbrains.anko.support.v4.share
import org.jetbrains.anko.support.v4.startActivity
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Moy_plalist : Fragment() {

    lateinit var ad: Adapter_my_list

    companion object {
        var position_list = 0
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


        //скроем панель показа ошибки
        val leiner_error = v.findViewById<LinearLayout>(R.id.lener_error)
        val text_erro = v.findViewById<TextView>(R.id.textViewerror_loadd_file)
        leiner_error.visibility = View.GONE

        val update_list = v.findViewById<Button>(R.id.button_close_list)

        //будем слушать эфир постоянно если че обновим список
        //----------------------------------------------------------------------------
        Slot(context, "Data_add").onRun { it ->
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {

                    if (!it.getStringExtra("listfile").isNullOrEmpty()) {
                        update_list.visibility = View.VISIBLE
                        open_file = it.getStringExtra("listfile")
                        list_move_history.add(open_file)
                    } else {
                        update_list.visibility = View.GONE
                        open_file = Main.MY_PLALIST
                    }

                    //заново все сделаем
                    //====================================================================================
                    val file_function = File_function()
                    //прочитали файл Main.MY_PLALIST и получили список строк , каждая строка содержит имя и адрес станции
                    //или получили Main.PUSTO если ам нет нечего
                    val mr = file_function.My_plalist(open_file)
                    //адаптеру будем слать список классов Radio
                    val d = ArrayList<Radio>()
                    val d_error = ArrayList<String>()

                    for (i in mr.indices) {
                        val m = mr[i].split("\n")
                        if (m.size > 1) {
                            d.add(Radio(m[0], "", "", m[1]))
                        } else {
                            if (m.isEmpty()) {
                                if (m[0] != "#EXTM3U") {
                                    d_error.add(mr[i] + " Позиция:" + i.toString())
                                }
                            }
                        }

                    }

                    ad = Adapter_my_list(d)
                    recikl_list.adapter = ad
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list < d.size && position_list >= 0) {
                        recikl_list.scrollToPosition(position_list)
                    }

                    //остановим анимацию
                    signal("Main_update").putExtra("signal", "stop_anim_my_list").send(context)


                    if (d_error.size > 0) {
                        //покажем кнопочку для показа списка всех ошибок, чтобы могли вручную их добавить
                        leiner_error.visibility = View.VISIBLE
                        text_erro.text = "Не получилось импортировать " + d_error.size.toString() + " шт"
                        text_erro.onClick {
                            //покажем диалоговое окно с списком брака
                            val ei = DialogWindow(context, R.layout.error_import)
                            val podrobno = ei.view().findViewById<TextView>(R.id.textView_error_import_podrobno)
                            var tx = "Ошибки: "
                            for (t in d_error.iterator()) {
                                tx += t
                            }
                            podrobno.text = tx

                        }
                        (v.findViewById<Button>(R.id.buttonclose_err)).onClick {
                            leiner_error.visibility = View.GONE
                        }
                        context.toast("Готово, но обыли ошибки")
                    } else {
                        //   context.toast("ok")
                    }


                    //скроем или покажем полосу прокрутки и поиск
                    if (mr.size > Main.SIZE_LIST_LINE) {
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
                        //скажем загрузить последний открытый файл
                        if (list_move_history.size > 1) {
                            //и удалим последний элемент
                            list_move_history.removeAt(list_move_history.size - 1)
                            open_file = list_move_history[list_move_history.size - 1]
                        } else {
                            update_list.visibility = View.GONE
                            list_move_history.clear()
                            open_file = ""
                            return@onRun
                        }
                    } else {
                        (v.findViewById<Button>(R.id.button_open_online_plalist)).callOnClick()
                        update_list.visibility = View.GONE
                    }


                    //заново все сделаем
                    //====================================================================================
                    val file_function = File_function()
                    //прочитали файл Main.MY_PLALIST и получили список строк , каждая строка содержит имя и адрес станции
                    //или получили Main.PUSTO если ам нет нечего
                    val mr = file_function.My_plalist(open_file)
                    //адаптеру будем слать список классов Radio
                    val d = ArrayList<Radio>()
                    val d_error = ArrayList<String>()

                    for (i in mr.indices) {
                        val m = mr[i].split("\n")
                        if (m.size > 1) {
                            d.add(Radio(m[0], "", "", m[1]))
                        } else {
                            if (m.isEmpty()) {
                                if (m[0] != "#EXTM3U") {
                                    d_error.add(mr[i] + " Позиция:" + i.toString())
                                }
                            }
                        }

                    }

                    ad = Adapter_my_list(d)
                    recikl_list.adapter = ad
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list < d.size && position_list >= 0) {
                        recikl_list.scrollToPosition(position_list)
                    }

                    //остановим анимацию
                    signal("Main_update").putExtra("signal", "stop_anim_my_list").send(context)


                    if (d_error.size > 0) {
                        //покажем кнопочку для показа списка всех ошибок, чтобы могли вручную их добавить
                        leiner_error.visibility = View.VISIBLE
                        text_erro.text = "Не получилось импортировать " + d_error.size.toString() + " шт"
                        text_erro.onClick {
                            //покажем диалоговое окно с списком брака
                            val ei = DialogWindow(context, R.layout.error_import)
                            val podrobno = ei.view().findViewById<TextView>(R.id.textView_error_import_podrobno)
                            var tx = "Ошибки: "
                            for (t in d_error.iterator()) {
                                tx += t
                            }
                            podrobno.text = tx

                        }
                        (v.findViewById<Button>(R.id.buttonclose_err)).onClick {
                            leiner_error.visibility = View.GONE
                        }
                        context.toast("Готово, но обыли ошибки")
                    } else {
                        //  context.toast("ok")
                    }


                    //скроем или покажем полосу прокрутки и поиск
                    if (mr.size > Main.SIZE_LIST_LINE) {
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

        //----------Открыть окно со списком онлайн плейлистов-------------------------------------
        (v.findViewById<Button>(R.id.button_open_online_plalist)).onClick {
            //книги https://dl.dropbox.com/s/cd479dcdguk6cg6/Audio_book.m3u
            //радио https://dl.dropbox.com/s/sl4x8z3yth5v1u0/Radio.m3u
            //tv https://www.dropbox.com/s/4m3nvh3hlx60cy7/plialist_tv.m3u?dl=0
            val online_pls = DialogWindow(context, R.layout.open_online_plalist)

            (online_pls.view().findViewById<Button>(R.id.open_radio)).onClick {
                //закрываем окно
                online_pls.close()
                //очистим список и запишем первый элемент корневой плейлисто
                list_move_history.clear()
                list_move_history.add(Main.MY_PLALIST)
                //загрузим начальный список
                Main.download_i_open_m3u_file("https://dl.dropbox.com/s/sl4x8z3yth5v1u0/Radio.m3u", "radio_plalisty")
            }
            (online_pls.view().findViewById<Button>(R.id.open_book)).onClick {
                //закрываем окно
                online_pls.close()
                //очистим список и запишем первый элемент корневой плейлисто
                list_move_history.clear()
                list_move_history.add(Main.MY_PLALIST)
                //загрузим начальный список
                Main.download_i_open_m3u_file("https://dl.dropbox.com/s/cd479dcdguk6cg6/Audio_book.m3u", "audio_book")
            }
            (online_pls.view().findViewById<Button>(R.id.open_tv)).onClick {
                //закрываем окно
                online_pls.close()
                //очистим список и запишем первый элемент корневой плейлисто
                list_move_history.clear()
                list_move_history.add(Main.MY_PLALIST)
                //загрузим начальный список
                Main.download_i_open_m3u_file( "https://dl.dropbox.com/s/4m3nvh3hlx60cy7/plialist_tv.m3u", "tv_plalist")
            }
            (online_pls.view().findViewById<Button>(R.id.user_station)).onClick {
                startActivity<Obmenik>()
                //закрываем окно
                online_pls.close()
            }


        }
        //----------------------------------------------------------------------------------------

        //------------удалить(очистить весь плейлист)---------------------------------------------
        (v.findViewById<Button>(R.id.button_delete)).onClick {

            //если список совсем не пуст (есть пояснялка но ссылок нет)
            if (ad.raduoSearchList.isNotEmpty()) {
                //если список не пуст
                if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                    val file_function = File_function()

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
                                    Main.EbuchieRazreshenia()
                                }
                            }
                        }

                        //переведём наш список в норм вид
                        //перезапишем и ждём ответа
                        file_function.SaveFile(Main.ROOT + "my_plalist.m3u", data.joinToString("\n"))
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
                                Main.EbuchieRazreshenia()
                            }
                        }
                    }

                    //перезапишем и ждём ответа
                    val file_function = File_function()
                    file_function.SaveFile(Main.ROOT + "my_plalist.m3u", "")
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

            (auu.view().findViewById<Button>(R.id.button_paste_url_add)).onClick { edit.setText(Main.getText(context)) }

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
                                    Main.EbuchieRazreshenia()
                                }
                            }
                        }
                        //делаем
                        val file_function = File_function()
                        file_function.Add_may_plalist_stansiy(edit.text.toString(), edit_name.text.toString())

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
            if (ad.raduoSearchList[0].name != (Main.PUSTO.replace("\n", ""))) {
                val file_function = File_function()
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
                                    Main.EbuchieRazreshenia()
                                }
                            }
                        }
                        //сохраним  временый файл ссылку и ждём сигналы
                        file_function.SaveFile(Main.ROOT + name.text.toString() + ".m3u", data.joinToString("\n"))
                    }
                }
            } else {
                context.toast("Нечего сохранять добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------------------

        //-------------открыть из памяти устройства или по ссылке плейлист--------------------------------------
        (v.findViewById<Button>(R.id.load_file)).onClick {
            val file_function = File_function()
            //если в плейлисте есть чето предложим чегонибуть
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {

                val vponf = DialogWindow(context, R.layout.vopros_pri_otkritii_new_file)

                //------------------открыть из памяти---------------------------------------------
                //затираем старое
                (vponf.view().findViewById<Button>(R.id.button_dell_old_plalist)).onClick {

                    //отправим с пустым старым текстом , старое затрётся
                    open_load_file(context, "")
                    //закрываем окошко
                    vponf.close()
                }

                //добавляем к старому если есть дубликаты пропустим их
                (vponf.view().findViewById<Button>(R.id.button_add_old_plalist)).onClick {

                    //прочтём текущий со всеми закорючками и отправим для добавления
                    val old_text: String = file_function.read(Main.MY_PLALIST)

                    //после выбора файла он прочётся и добавится к старым данным
                    open_load_file(context, old_text)
                    //закрываем окошко
                    vponf.close()
                }

            } else {
                open_load_file(context, "")
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
                //передадим данные они там будут всеравно сохранятся(переименовыватся)
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

                val name_file = "Плэйлист с " + ad.raduoSearchList.size.toString() + " станциями.m3u"

                //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        "zaebis" -> {
                            Main.play_aimp(Main.ROOT + name_file, "")
                        }
                        "pizdec" -> {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            Main.EbuchieRazreshenia()
                        }
                    }
                }
                val file_function = File_function()
                //сохраним  временый файл ссылку и ждём сигналы
                file_function.SaveFile(Main.ROOT + name_file, data.joinToString("\n"))
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------

        //---------------открыть в системе----------------------------------------
        (v.findViewById<Button>(R.id.open_aimp)).onLongClick {
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

                val name_file = "Плэйлист с " + ad.raduoSearchList.size.toString() + " станциями.m3u"

                //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        "zaebis" -> {
                            Main.play_system(Main.ROOT + name_file, "")
                        }
                        "pizdec" -> {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            Main.EbuchieRazreshenia()
                        }
                    }
                }
                val file_function = File_function()
                //сохраним  временый файл ссылку и ждём сигналы
                file_function.SaveFile(Main.ROOT + name_file, data.joinToString("\n"))
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

    private fun open_load_file(context: Context, str_old: String) {
        val file_function = File_function()

        //создадим папки если нет
        file_function.create_esli_net()


        //если плейлист пуст откроем окно выбора загрузки файла(память или ссылка)
        val lf = DialogWindow(context, R.layout.load_file)


        var file_m3u_custom: String

        //---------------при выборе из памяти устройства----------------------------------------------------
        (lf.view().findViewById<Button>(R.id.load_fs)).onClick {

            //посмотрим есть старый путь
            val old_dir = Main.save_read("startdir")
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
                            Main.save_value("startdir", File(file_m3u_custom).parent)

                            GlobalScope.launch {
                                //запустим анимацию
                                signal("Main_update").putExtra("signal", "start_anim_my_list").send(context)

                                //читаем выбраный файл в str
                                var str = file_function.read(file_m3u_custom)
                                //если файл есть и он не пустой зальём его в список по умолчанию
                                if (str.length > 1) {

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
                                                Main.EbuchieRazreshenia()
                                            }
                                        }
                                    }
                                    //поехали , сохраняем  и ждём сигналы
                                    if (str_old.length > 7) {
                                        str = str.replace("#EXTM3U", "")
                                    }
                                    file_function.SaveFile(Main.MY_PLALIST, str_old + str)
                                } else {
                                    context.toast("Файл: $file_m3u_custom пуст")
                                }
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
            val f = File_function()
            //сюда будем записывать переботаный стринг в хистори массив
            val d = ArrayList<History>()
            //загружаем историю из файла
            val history_url_list = f.readArrayList(Main.HISTORY_LINK)

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
            (dvvul.view().findViewById<Button>(R.id.button_paste_list_url_add)).onClick { e.setText(Main.getText(context)) }

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
                        n = "file"+Main.rnd_int(1,100).toString()
                    }

                    save_mass.add("$n$$url_link$$date_time")
                    f.saveArrayList(Main.HISTORY_LINK, save_mass)
                    //----------------------------------------------------------

                    //-----------скачиваем файл (читам его)--------
                    Main.download_i_open_m3u_file(url_link,n)
                }
            }
        }
        //--------------------------------------------------------------------------------------
    }
}

//===========================Адаптер к спику истории ссылок=============================================================
class Adapter_history_list(val data: ArrayList<History>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_history_list.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val url = itemView.findViewById<TextView>(R.id.url_potok)
        val name = itemView.findViewById<TextView>(R.id.name_potok)
        val data_time = itemView.findViewById<TextView>(R.id.data_add_potok)
        val share = itemView.findViewById<Button>(R.id.button_share_url_plalist)
        val liner = itemView.findViewById<LinearLayout>(R.id.liner_online_plalist)
        val fon = itemView.findViewById<LinearLayout>(R.id.fon_item_vvoda_potoka)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_vvoda_potoka, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val history = data[p1]

        p0.url.text = history.url
        p0.name.text = history.name
        p0.data_time.text = history.data_time


        p0.liner.onClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            signal("clik_history_item").putExtra("url", history.url).putExtra("name", history.name).send(Main.context)
        }
        p0.liner.onLongClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //пересоберём список без текущей строки
            GlobalScope.launch {
                val save_data = ArrayList<String>()
                for (d in data) {
                    if (d.url != history.url) {
                        save_data.add(d.name + "$" + d.url + "$" + d.data_time)
                    }
                }
                File_function().saveArrayList(Main.HISTORY_LINK, save_data)
            }
            //не буду нечего слушать и проверять так пока сделаю
            data.removeAt(p1)
            notifyDataSetChanged()
        }


        p0.share.onClick {
            context.share(history.url)
        }
    }
}
//======================================================================================================================