package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.service.autofill.FieldClassification
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import com.github.kittinunf.fuel.httpGet
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.email
import org.jetbrains.anko.support.v4.share
import org.jetbrains.anko.support.v4.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
            when(s){
                "zaebis" -> {
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

        //---------------при выборе из памяти устройства----------------------------------------------------
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
                                    //поехали , сохраняем  и ждём сигналы
                                    if(str_old.length>7){
                                        str = str.replace("#EXTM3U","")
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
            //загружаем историю
            val history_url_list = f.readArrayList(Main.HISTORY_LINK)

            //содаём диалоговое окно
            val dvvul = DialogWindow(context, R.layout.dialog_vvoda_ull_lista)

            //поменяем цвет фона ато все сливается
            (dvvul.view().findViewById<LinearLayout>(R.id.fon_dialoga_add_online_plalist)).backgroundColor =Main.COLOR_FON

            //история
            val r = (dvvul.view().findViewById<RecyclerView>(R.id.list_history_link))

            //сюда будем записывать переботаный стринг в хистори массив
            val d = ArrayList<History>()

            //если список пустой запишем парочку своих для примера
            //----------------------------------------------------------------
            if(history_url_list.size<3){
                for (sh in Main.HISTORY_LIST_PRIMER) {
                   history_url_list.add(sh.name+"$"+sh.url+"$"+sh.data_time)
                }
            }

            //парсим в нужный вид  и переворачиваем
            for (s in history_url_list.listIterator()) {
                if (s.length > 1) {
                    val s_list = s.split("$")
                    if (s_list.size > 2) {
                        d.add(0, History(s_list[0],s_list[1], s_list[2]))
                    }
                }
            }
            //------------------------------------------------------------------

            val e = dvvul.view().findViewById<EditText>(R.id.editText_add_list_url)
            e.typeface = Main.face
            e.textColor = Main.COLOR_TEXT
            e.hintTextColor = Main.COLOR_TEXTcontext
            e.hint = "Введите Url плейлиста"

            val e_n =(dvvul.view().findViewById<EditText>(R.id.editText_add_list_url_name))
            e_n.typeface = Main.face
            e_n.textColor = Main.COLOR_TEXT
            e_n.hintTextColor = Main.COLOR_TEXTcontext
            e_n.hint = "Имя или поеснение какоенибуть(не важно)"


            val a = Adapter_history_list(d)
            r.adapter = a

            //вставка из буфера
            (dvvul.view().findViewById<Button>(R.id.button_paste_list_url_add)).onClick { e.setText(getText(context)) }

            //если по истории кто кликнет то установим тот текст в эдит
            Slot(Main.context, "clik_history_item").onRun {
                val t = it.getStringExtra("url")
                val n = it.getStringExtra("name")
                if (t.isNotEmpty()) {
                    e.setText(t)
                }
                if(n.isNotEmpty()){
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
                    for(s in history_url_list){
                        //если есть акойже адрес удалим из массива его
                        if(!s.contains(url_link)){
                            save_mass.add(s)
                        }
                    }

                    //Имя файла, не особо важно не буду заморачиваться
                    var n =  e_n.text.toString()
                    if(n.isEmpty()){
                        n= ""
                    }

                    save_mass.add(n+"$"+url_link + "$" + date_time)
                    f.saveArrayList(Main.HISTORY_LINK, save_mass)
                    //----------------------------------------------------------

                    //-----------скачиваем файл (читам его)--------
                    GlobalScope.launch {
                        url_link.httpGet().responseString { request, response, result ->
                            when (result) {
                                is com.github.kittinunf.result.Result.Failure -> {
                                    val ex = result.getException()
                                }
                                is com.github.kittinunf.result.Result.Success -> {
                                    var data = result.get()

                                    if (data.isNotEmpty()) {
                                        //если там чтото есть сохраним все(вместе с мусором) в Main.MY_PLALIST
                                        //потом пошлётся сигнал чтобы мой плалист обновился , а там он при чтении уже удалит ненужное

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
                                        //если есть удалим ебучий тег в начале файла
                                        if(str_old.length>7){
                                            data = data.replace("#EXTM3U","")
                                        }
                                        //поехали , сохраняем  и ждём сигналы
                                        file_function.SaveFile(Main.MY_PLALIST, str_old + data)
                                    }
                                }
                            }
                        }
                    }
                    //--------------------------------------------------------
                }
            }

            //


        }
        //--------------------------------------------------------------------------------------


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
            if (clipboard!!.text == null) {
                toast("Буфер обмена пуст")
                ""
            } else {
                clipboard.text.toString()
            }
        }
        return text
    }
}


class Adapter_history_list(val data: ArrayList<History>) : RecyclerView.Adapter<Adapter_history_list.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val url = itemView.findViewById<TextView>(R.id.url_potok)
        val name =  itemView.findViewById<TextView>(R.id.name_potok)
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

        p0.url.text  = history.url
        p0.name.text = history.name
        p0.data_time.text = history.data_time


        p0.liner.onClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            signal("clik_history_item").putExtra("url", history.url).putExtra("name",history.name).send(Main.context)
        }

        p0.share.onClick {
            context.share(history.url)
        }
    }

}
