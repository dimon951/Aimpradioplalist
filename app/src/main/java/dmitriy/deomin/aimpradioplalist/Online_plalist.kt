package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.online_plalist.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import android.util.Log
import android.view.animation.AnimationUtils
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.share
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Online_plalist : Fragment() {

    lateinit var ad_online_palist: Adapter_online_palist
    var open_file_online_palist = ""
    private var list_history = ArrayList<String>()


    companion object {
        var position_list_online_palist = 0
        var visible_selekt = false
        var list_selekt = ArrayList<Int>()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.online_plalist, null)
        val context: Context = Main.context
        //настройка вида----------------------------------------------------------------------------
        val find = v.findViewById<EditText>(R.id.editText_find_online_plalist)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

        //читам из памяти историю навигации
        //---------------------------------------------------------------------
//        val savhis = Main.save_read("history_navigacia")
//        if (savhis.length > 1) {
//            val collectionType = object : TypeToken<ArrayList<String>>() {}.type
//            history_navigacia = Gson().fromJson(savhis, collectionType)
//        }
        //-----------------------------------------------------------------------

        position_list_online_palist = save_read_int(open_file_online_palist)
//        CATEGORIA = Main.save_read("categoria")
//        visibleselekt_CATEGORIA(CATEGORIA, v)


        val recikl_list_online = v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recicl_online_plalist)
        recikl_list_online.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        //полоса быстрой прокрутки
        val fastScroller = v.findViewById<VerticalRecyclerViewFastScroller>(R.id.fast_scroller_online_plalist)
        //получим текущие пораметры
        val paramL = fastScroller.layoutParams
        //меняем ширину
        paramL.width = Main.SIZE_WIDCH_SCROLL
        //устанавливаем
        fastScroller.layoutParams = paramL
        fastScroller.setRecyclerView(recikl_list_online)
        recikl_list_online.setOnScrollListener(fastScroller.onScrollListener)
        //-------------------------------------------------------------------------------------------

        //будем слушать эфир постоянно если че обновим список
        //----------------------------------------------------------------------------
        Slot(context, "Online_plalist").onRun { it ->
            //получим данные
            when (it.getStringExtra("update")) {
                "zaebis" -> {
                    if (it.getStringExtra("url") != null) {
                        open_file_online_palist = it.getStringExtra("url")
                        //добавим в список навигации
                        list_history.add(open_file_online_palist)
                    }

                    position_list_online_palist = save_read_int(open_file_online_palist)

                    //получим переданные данные
                    //====================================================================================
                    val data = if (it.getParcelableArrayListExtra<Radio>("pars_data") != null) {
                        it.getParcelableArrayListExtra<Radio>("pars_data")
                    } else {
                        read_and_pars_m3u_file(open_file_online_palist)
                    }
                    ad_online_palist = Adapter_online_palist(data)
                    recikl_list_online.adapter = ad_online_palist
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list_online_palist < data.size && position_list_online_palist >= 0) {
                        recikl_list_online.scrollToPosition(position_list_online_palist)
                    }

                    //скроем или покажем полосу прокрутки и поиск
                    if (data.size > Main.SIZE_LIST_LINE) {
                        fastScroller.visibility = View.VISIBLE

                        find.visibility = View.VISIBLE
                        // текст только что изменили в строке поиска
                        find.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable) {}
                            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                                ad_online_palist.filter.filter(text)
                            }
                        })
                    } else {
                        fastScroller.visibility = View.GONE
                        find.setText("")
                        find.visibility = View.GONE
                    }
                    //остановим анимацию
                    signal("Main_update").putExtra("signal", "stop_anim_online_plalist").send(context)
                    //==================================================================
                }
            }
        }
        //-------------------------------------------------------------------------------------

        Slot(context, "save_pozitions").onRun {
            if (!it.getStringExtra("pos").isNullOrEmpty()) {
                position_list_online_palist = it.getStringExtra("pos").toInt()
                save_value_int(open_file_online_palist, position_list_online_palist)
            }
        }

        //выделить всЁ
        v.button_selekt_all_op.onClick {
            val d = ad_online_palist.raduoSearchList
            if (d.size == list_selekt.size) {
                list_selekt.clear()
                ad_online_palist.notifyDataSetChanged()
                v.button_selekt_all_op.backgroundDrawable = resources.getDrawable(R.drawable.selektall)
            } else {
                list_selekt.clear()
                for (l in d.withIndex()) {
                    list_selekt.add(l.index)
                    ad_online_palist.notifyDataSetChanged()
                }
                v.button_selekt_all_op.backgroundDrawable = resources.getDrawable(R.drawable.un_selektall)
            }
        }

        //будем слушать сигналы из адаптера
        Slot(context, "Online_plalist_Adapter").onRun {

            when (it.getStringExtra("signal")) {
                "visible" -> {
                    if (v.liner_long_menu.visibility == View.VISIBLE) {
                        v.liner_long_menu.visibility = View.GONE
                        visible_selekt = false
                        list_selekt.clear()
                        ad_online_palist.notifyDataSetChanged()
                    } else {
                        v.liner_long_menu.visibility = View.VISIBLE
                        visible_selekt = true
                    }
                }


            }
        }

        v.button_open_aimp_op.onClick {

            if (list_selekt.size > 0) {

                val d = ad_online_palist.raduoSearchList
                val data = ArrayList<String>()
                //запишем в строчном формате
                data.add("#EXTM3U")
                for (s in d.withIndex()) {
                    if (d[s.index].url.isNotEmpty() && list_selekt.contains(s.index)) {
                        data.add("\n#EXTINF:-1," + d[s.index].name + " " + d[s.index].kbps + "\n" + d[s.index].url)
                    }
                }

                val name_file = "Плэйлист:" + d[0].name + " (" + list_selekt.size.toString() + " частей)"

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
                File_function().SaveFile(name_file, data.toString())
            } else {
                context.toast("Выберите что воспроизводить")
            }


        }

        v.button_open_aimp_op.onLongClick {
            if (list_selekt.size > 0) {

                val d = ad_online_palist.raduoSearchList
                val data = ArrayList<String>()
                //запишем в строчном формате
                data.add("#EXTM3U")
                for (s in d.withIndex()) {
                    if (d[s.index].url.isNotEmpty() && list_selekt.contains(s.index)) {
                        data.add("\n#EXTINF:-1," + d[s.index].name + " " + d[s.index].kbps + "\n" + d[s.index].url)
                    }
                }

                val name_file = "Плэйлист:" + d[0].name + " (" + list_selekt.size.toString() + " частей)"

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
                File_function().SaveFile(name_file, data.toString())
            } else {
                context.toast("Выберите что воспроизводить")
            }
        }

        v.button_cshre_op.onClick {
            if (list_selekt.size > 0) {
                val d = ad_online_palist.raduoSearchList
                val data = ArrayList<String>()
                //запишем в строчном формате
                data.add("#EXTM3U")
                for (s in d.withIndex()) {
                    if (d[s.index].url.isNotEmpty() && list_selekt.contains(s.index)) {
                        data.add("\n#EXTINF:-1," + d[s.index].name + " " + d[s.index].kbps + "\n" + d[s.index].url)
                    }
                }
                share(data.joinToString("\n"))
            } else {
                context.toast("Выберите чем поделится")
            }
        }


        v.button_add_plalist_op.onClick {

            if (list_selekt.size > 0) {
                val d = ad_online_palist.raduoSearchList
                for (s in d.withIndex()) {
                    if (d[s.index].url.isNotEmpty() && list_selekt.contains(s.index)) {
                        add_myplalist(d[s.index].name + " " + d[s.index].kbps, d[s.index].url)
                    }
                }
            } else {
                context.toast("Выберите что добавить")
            }
        }


        v.help_open_tv.onClick {
            save_value("help_tv", "no")
            if (save_read("help_tv").length > 1) {
                v.help_open_tv.visibility = View.GONE
            } else {
                v.help_open_tv.visibility = View.VISIBLE
            }
        }


        v.button_history_online_plalilst.setOnLongClickListener {
            v.button_history_online_plalilst.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            val hop = DialogWindow(context, R.layout.history_online_plalist)

            //слот будет закрывать это окно
            Slot(context, "History_online_plalist").onRun { hop.close() }

            //настройка вида----------------------------------------------------------------------------
            val fon = hop.view().findViewById<LinearLayout>(R.id.fon)
            fon.setBackgroundColor(Main.COLOR_FON)

            val recikl = hop.view().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recikl)
            recikl.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            //полоса быстрой прокрутки
            val fS = hop.view().findViewById<VerticalRecyclerViewFastScroller>(R.id.fast_scroller)
            //получим текущие пораметры
            val paramL = fS.layoutParams
            //меняем ширину
            paramL.width = Main.SIZE_WIDCH_SCROLL
            //устанавливаем
            fS.layoutParams = paramL
            fS.setRecyclerView(recikl)
            recikl.setOnScrollListener(fS.onScrollListener)
            //-------------------------------------------------------------------------------------------

            val d = ArrayList<History>()
            val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss", Locale.getDefault())

            for (l in list_history.iterator()) {
                val f = File(l)
                if (f.isFile) {
                    d.add(History(f.name, f.absolutePath, sdf.format(f.lastModified()), long_size_to_good_vid(f.length().toDouble())))
                }
            }


            val a = Adapter_history_online_plalist(d)
            recikl.adapter = a


            //покажем полный размер кеша
            //кнопка очистить кеш
            //при открытии меню будем показыват размер этого кеша
            val b_c = hop.view().findViewById<Button>(R.id.button_clear_kesh)

            //получим размер
            val file = File(Main.ROOT)
            if (file.exists()) {
                val s = getDirSize(file)
                if (s > Main.SIZEFILETHEME) {
                    b_c.text = "Очистить Историю/Кэш (" + long_size_to_good_vid(s.toDouble()) + ")"
                } else {
                    b_c.text = "История/Кэш очищен"

                }
            } else
                b_c.text = "История/Кэш очищен"

            b_c.onClick {

                if (b_c.text == "История/Кэш очищен") {

                    signal("History_online_plalist").send(context)
                    signal("Online_plalist")
                            .putExtra("update", "zaebis")
                            .send(context)
                    Main.context.toast("Пусто")

                } else {

                    //покажем предупреждающее окошко
                    val v_d = DialogWindow(Main.context, R.layout.dialog_delete_plalist)

                    v_d.view().findViewById<TextView>(R.id.text_voprosa_del_stncii).text = "Удалить Историю/Кеш?"

                    v_d.view().findViewById<Button>(R.id.button_dialog_delete).onClick {
                        v_d.close()
                        //удаляем все
                        if (file.exists()) {
                            //файлы
                            deleteAllFilesFolder(Main.ROOT)
                            //список этих файлов
                            hop.close()
                            //очистим историю навигации
                            //------------------------------------------
                            list_history.clear()
                            signal("history_save").send(context)
                            //И принадлежность к категории сылок
                            save_value("url_tv", "")
                            save_value("url_ra", "")
                            save_value("url_au", "")
                            save_value("url_mu", "")
                            //сбросим на кнопках
                            visibleselekt_CATEGORIA("del", v)
                            //--------------------------------------------
                            //обновим список
                            //иначе пустую страницу покажем
                            signal("Online_plalist")
                                    .putExtra("update", "zaebis")
                                    .send(context)
                            signal("History_online_plalist").send(context)
                        }
                        if (file.exists()) {
                            val s = getDirSize(file)
                            if (s > Main.SIZEFILETHEME) {
                                b_c.text = "Очистить Историю/Кэш (" + long_size_to_good_vid(s.toDouble()) + ")"
                            } else {
                                b_c.text = "История/Кэш очищен"
                            }

                        } else {
                            b_c.text = "История/Кэш очищен"
                        }

                    }
                    v_d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
                        v_d.close()
                    }

                }

            }
            //---------------------------------------

            true
        }

        v.button_history_online_plalilst.onClick {
            if (list_history.size > 0) {
                download_i_open_m3u_file(list_history[list_history.size - 1], "anim_online_plalist")
                list_history.remove(list_history[list_history.size - 1])
            }
        }

        //--------категории--------------------------------------------------------------

        v.button_open_online_plalist_radio.onClick {
            download_i_open_m3u_file("https://dl.dropbox.com/s/sl4x8z3yth5v1u0/Radio.m3u", "anim_online_plalist")
        }

        v.button_open_online_plalist_audio_book.onClick {
            download_i_open_m3u_file("https://dl.dropbox.com/s/cd479dcdguk6cg6/Audio_book.m3u", "anim_online_plalist")
        }

        v.button_open_online_plalist_tv.onClick {
            download_i_open_m3u_file("https://dl.dropbox.com/s/4m3nvh3hlx60cy7/plialist_tv.m3u", "anim_online_plalist")
        }
        v.button_open_online_plalist_musik.onClick {
            download_i_open_m3u_file("https://dl.dropbox.com/s/oe9kdcksjru82by/Musik.m3u", "anim_online_plalist")
        }
        v.button_open_online_plalist_obmennik.onClick {
            startActivity<Obmenik>()
        }
        //---------------------------------------------------------

        //----------------------------------------------------------------------------------------------

        val h = save_read(Main.HISORYLAST)

        if (h.length > 1) {
            if (File(h).isFile) {
                //пошлём сигнал для загрузки дааных в спискок
                signal("Online_plalist")
                        .putExtra("update", "zaebis")
                        .putExtra("listfile", h)
                        .send(context)
            } else {
                //иначе пустую страницу покажем
                signal("Online_plalist")
                        .putExtra("update", "zaebis")
                        .send(context)
            }
        } else {
            //иначе пустую страницу покажем
            signal("Online_plalist")
                    .putExtra("update", "zaebis")
                    .send(context)
        }

        return v
    }


    fun visibleselekt_CATEGORIA(cat: String, v: View) {
        when (cat) {
            "button_open_online_plalist_radio" -> {
                v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_ITEM
                v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
            }
            "button_open_online_plalist_audio_book" -> {
                v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_ITEM
                v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
            }
            "button_open_online_plalist_tv" -> {
                v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_ITEM
                v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
            }
            "button_open_online_plalist_musik" -> {
                v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_ITEM
            }
            "del" -> {
                v.button_open_online_plalist_radio.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_audio_book.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_tv.backgroundColor = Main.COLOR_FON
                v.button_open_online_plalist_musik.backgroundColor = Main.COLOR_FON
            }
        }

    }


    fun read_page_list(): ArrayList<String> {
        val dir = File(Main.ROOT) //path указывает на директорию
        val arrFiles = dir.listFiles()


        val d = ArrayList<String>()

        if (arrFiles != null) {
            for (s in arrFiles.iterator()) {
                if (s.isFile && s.name != "history_url.txt" && s.name != "theme.txt" && s.name != "my_plalist.m3u") {
                    d.add(s.absolutePath)
                }
            }
            //отсортируем по дате создания файла
            d.sortWith(Comparator { o1, o2 ->
                val a = File(o1).lastModified()
                val b = File(o2).lastModified()
                a.compareTo(b)
            })
        }


        return d
    }


    fun deleteAllFilesFolder(path: String) {
        for (myFile in File(path).listFiles()) {
            Log.e("tttt", myFile.absolutePath)
            if (myFile.isFile && myFile.name != "theme.txt" && myFile.name != "history_url.txt" && myFile.name != "my_plalist.m3u") myFile.delete()
        }
    }

    fun getDirSize(dir: File): Long {
        var size: Long = 0
        if (dir.isFile) {
            size = dir.length()
        } else {
            val subFiles = dir.listFiles()
            for (file in subFiles) {
                size += if (file.isFile) {
                    file.length()
                } else {
                    getDirSize(file)
                }
            }
        }
        return size
    }

    //-----------------size---------------------------------------------------
    //
    fun long_size_to_good_vid(size: Double): String {
        return if (size > 1024 * 1024) {
            round(size / (1024 * 1024), 1).toString() + " mb"
        } else if (size > 1024) {
            round(size / 1024, 1).toString() + " kb"
        } else {
            if (size > 0) {
                round(size, 1).toString() + " bytes"
            } else {
                ""
            }

        }
    }

    //уменьшает количество символов после запятой в double
    fun round(number: Double, scale: Int): Double {
        var pow = 10
        for (i in 1 until scale)
            pow *= 10
        val tmp = number * pow
        return (if (tmp - tmp.toInt() >= 0.5) tmp + 1 else tmp).toInt().toDouble() / pow
    }
//--------------------------------------------------------------------------------

}
