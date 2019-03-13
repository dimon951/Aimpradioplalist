package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.email
import org.jetbrains.anko.support.v4.share
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.File


class Moy_plalist : Fragment() {

    companion object {
        var position_list = 0
    }


    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.my_plalist, null)
        val context: Context = Main.context

        val recikl_list = v.findViewById<RecyclerView>(R.id.recicl_my_list)
        recikl_list.layoutManager = LinearLayoutManager(context)

        //полоса быстрой прокрутки
        val fastScroller: VerticalRecyclerViewFastScroller = v.findViewById(R.id.fast_scroller)
        fastScroller.setRecyclerView(recikl_list)
        recikl_list.setOnScrollListener(fastScroller.onScrollListener)


        //скроем панель показа ошибки
        val leiner_error = v.findViewById<LinearLayout>(R.id.lener_error)
        val text_erro = v.findViewById<TextView>(R.id.textViewerror_loadd_file)
        leiner_error.visibility = View.GONE


        //будем слушать эфир постоянно если че обновим список
        //----------------------------------------------------------------------------
        Slot(context, "Data_add").onRun { it ->

            //получим данные
            val s = it.getStringExtra("update")
            if (s == "zaebis") {

                //заново все сделаем
                //------------------------------------------------------
                val file_function = File_function()
                //прочитали файл Main.MY_PLALIST и получили список строк , каждая строка содержит имя и адрес станции
                //или получили Main.PUSTO если ам нет нечего
                val mr = file_function.My_plalist(Main.MY_PLALIST)
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

                val ad = Adapter_my_list(d)
                recikl_list.adapter = ad
                //---------------------------------------------------------

                //перемотаем
                if (position_list < d.size && position_list >= 0) {
                    recikl_list.scrollToPosition(position_list)
                }


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
                    context.toast("ok")
                }


            }
        }
        //-------------------------------------------------------------------------------------

        //Слушаем кнопки

        //------------удалить(очистить весь плейлист)---------------------------------------------
        (v.findViewById<Button>(R.id.button_delete)).onClick {
            val file_function = File_function()

            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {

                val ddp = DialogWindow(context, R.layout.dialog_delete_plalist)

                (ddp.view().findViewById<Button>(R.id.button_dialog_delete)).onClick {

                    Slot(context, "File_created", false).onRun {
                        //получим данные
                        val s = it.getStringExtra("update")
                        when (s) {
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

                    //удаляем и ждём ответа
                    file_function.Delet_my_plalist()
                    ddp.close()
                }
                (ddp.view().findViewById<Button>(R.id.button_dialog_no)).onClick {
                    ddp.close()
                }
            } else {
                context.toast("Плейлист пуст")
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

            (auu.view().findViewById<Button>(R.id.button_paste_url_add)).onClick { edit.setText(getText(context)) }

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
            val file_function = File_function()
            //прочитаем плейлист весь с закорючками
            val data: String = file_function.read(Main.MY_PLALIST)
            if (data.length < 9) {
                context.toast("Нечего сохранять добавьте хотябы одну станцию")
            } else {

                //покажем оконо в котором нужно будет ввести имя
                val nsf = DialogWindow(context, R.layout.name_save_file)

                val name = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                name.typeface = Main.face
                name.textColor = Main.COLOR_TEXT

                (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                    if (name.text.toString().isEmpty()) {
                        //пока покажем это потом будум генерерить свои если не захотят вводить
                        context.toast("Введите имя")
                    } else {
                        //когда прийдёт сигнал что сохранилось все хорошо обновим плейлист
                        Slot(context, "File_created", false).onRun { it ->
                            //получим данные
                            val s = it.getStringExtra("update")
                            when (s) {
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
                        file_function.Save_temp_file(name.text.toString() + ".m3u", data)

                        //закроем окошко
                        nsf.close()
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------

        //-------------открыть из памяти устройства плейлист--------------------------------------
        (v.findViewById<Button>(R.id.load_file)).onClick {
            val file_function = File_function()
            //если в плейлисте есть чето предложим чегонибуть
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {

                val vponf = DialogWindow(context, R.layout.vopros_pri_otkritii_new_file)

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
        }
        //------------------------------------------------------------------------------------

        //------------открыть в плеере------------------------------------------------------
        (v.findViewById<View>(R.id.open_aimp)).onClick {
            val file_function = File_function()
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {
                Main.play_aimp(Main.MY_PLALIST, "")
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //--------------------------------------------------------------------------

        //---------------открыть в системе----------------------------------------
        (v.findViewById<View>(R.id.open_aimp)).onLongClick {
            val file_function = File_function()
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {
                Main.play_system(Main.MY_PLALIST, "")
            } else {
                context.toast("Плэйлист пуст, добавьте хотябы одну станцию")
            }
        }
        //----------------------------------------------------------------------------------

        //------------поделится---------------------------------------------------------
        (v.findViewById<Button>(R.id.button_otpravit)).onClick {
            val file_function = File_function()
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {
                var send = ""

                for (s in file_function.My_plalist(Main.MY_PLALIST)) {
                    send += s + "\n"
                }

                share(send)
            } else {
                context.toast("Нечего отпралять, плейлист пуст")
            }
        }
        //----------------------------------------------------------------------------------

        //-----при долгом нажатиии будем предлогать отправить мне письмом этот плейлист----
        (v.findViewById<Button>(R.id.button_otpravit)).onLongClick {
            val file_function = File_function()
            if (file_function.My_plalist(Main.MY_PLALIST)[0] != Main.PUSTO) {
                var send = ""

                for (s in file_function.My_plalist(Main.MY_PLALIST)) {
                    send += s + "\n"
                }
                email("deomindmitriy@gmail.com", "aimp_radio_plalist", send)
            } else {
                context.toast("Нечего отпралять, плейлист пуст")
            }
        }
        //--------------------------------------------------------------------------

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

        //при выборе из памяти устройства
        (lf.view().findViewById<Button>(R.id.load_fs)).onClick {

            //посмотрим есть старый пусть
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
                                //читаем выбраный файл в str
                                var str = file_function.read(file_m3u_custom)
                                //если файл есть и он не пустой зальём его в список по умолчанию
                                if (str.length > 1) {

                                    //когда прийдёт сигнал что все хорошо обновим плейлист
                                    Slot(context, "File_created", false).onRun { it ->
                                        //получим данные
                                        val s = it.getStringExtra("update")
                                        when (s) {
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

                                    //если в параметрах данные были поставим их вначале
                                    if (str_old.length > 7) {

                                        //заменим тег #EXTM3U в прочитаном файле если есть в начале файла на перенос строки
                                        //и удалим весь мусор
                                        str = str.replace("#EXTM3U", "\n")
                                                .replace("'", "")
                                                .replace("&", "")


                                        //если в новых данных есть старые удалим их
                                        val cikl_data = str_old.replace("#EXTM3U", "").split("#EXTINF:-1,")
                                        for (i in cikl_data.iterator()) {
                                            //читаем старые данные и если они есть в новых удаляем
                                            str = str.replace("#EXTINF:-1,$i", "")
                                        }

                                        //поехали , сохраняем  и ждём сигналы
                                        file_function.SaveFile(Main.MY_PLALIST, str_old + str)
                                    } else {
                                        //поехали , сохраняем  и ждём сигналы
                                        file_function.SaveFile(Main.MY_PLALIST, str)
                                    }

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
    }

    //чтение из буфера
    private fun getText(c: Context): String {
        val text: String
        val sdk = android.os.Build.VERSION.SDK_INT
        text = if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager?
            clipboard!!.text.toString()
        } else {
            val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager?
            if(clipboard!!.text==null){
                toast("Буфер обмена пуст")
                ""
            }else{
                clipboard.text.toString()
            }
        }
        return text
    }
}
