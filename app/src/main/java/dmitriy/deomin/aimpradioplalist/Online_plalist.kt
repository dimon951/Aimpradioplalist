package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Online_plalist : Fragment() {

    lateinit var ad_online_palist: Adapter_online_palist
    var open_file_online_palist = ""

    companion object{
        var position_list_online_palist = 0
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.online_plalist, null)
        val context: Context = Main.context
        //настройка вида----------------------------------------------------------------------------
        val find = v.findViewById<EditText>(R.id.editText_find_online_plalist)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

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
                    open_file_online_palist = if (!it.getStringExtra("listfile").isNullOrEmpty()) {
                        it.getStringExtra("listfile")
                    } else {
                        Main.HOME_ONLINE_PLALIST
                    }

                    //заново все сделаем
                    //====================================================================================
                    val file_function = File_function()
                    val mr = file_function.My_plalist(open_file_online_palist)
                    //адаптеру будем слать список классов Radio
                    val d = ArrayList<Radio>()

                    for (i in mr.indices) {
                        val m = mr[i].split("\n")
                        if (m.size > 1) {
                            d.add(Radio(m[0], "", "", m[1]))
                        }

                    }

                    ad_online_palist = Adapter_online_palist(d)
                    recikl_list_online.adapter = ad_online_palist
                    //---------------------------------------------------------

                    //перемотаем
                    if (position_list_online_palist < d.size && position_list_online_palist >= 0) {
                        recikl_list_online.scrollToPosition(position_list_online_palist)
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

        v.button_history_online_plalilst.onClick {
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

            val list_history  = read_page_list()
            val d = ArrayList<History>()
            val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss", Locale.getDefault())

            for (l in list_history.iterator()) {
                val f = File(l)
                if (f.isFile) {
                    d.add(History(f.name, f.absolutePath, sdf.format(f.lastModified())))
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
                    b_c.text = "Очистить Историю/Кэш(" + (s / 1024).toString() + " kb" + ")"
                } else {
                    b_c.text = "История/Кэш очищен"
                }
            } else
                b_c.text = "История/Кэш очищен"

            b_c.onClick {

                if (b_c.text == "История/Кэш очищен") {

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
                        }
                        if (file.exists()) {
                            val s = getDirSize(file)
                            if (s > Main.SIZEFILETHEME) {
                                b_c.text = "Очистить Историю/Кэш(" + (s / 1024).toString() + " kb" + ")"
                            } else {
                                b_c.text = "История/Кэш очищен"
                            }

                        } else
                            b_c.text = "История/Кэш очищен"

                    }
                    v_d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
                        v_d.close()
                    }

                }

            }
            //---------------------------------------
        }

        v.button_open_online_plalist_radio.onClick {
            Main.download_i_open_m3u_file("https://dl.dropbox.com/s/sl4x8z3yth5v1u0/Radio.m3u", "radio_plalisty", "anim_online_plalist")
        }

        v.button_open_online_plalist_audio_book.onClick {
            Main.download_i_open_m3u_file("https://dl.dropbox.com/s/cd479dcdguk6cg6/Audio_book.m3u", "audio_book", "anim_online_plalist")
        }

        v.button_open_online_plalist_tv.onClick {
            Main.download_i_open_m3u_file("https://dl.dropbox.com/s/4m3nvh3hlx60cy7/plialist_tv.m3u", "tv_plalist", "anim_online_plalist")
        }
        v.button_open_online_plalist_obmennik.onClick {
            startActivity<Obmenik>()
        }
        //---------------------------------------------------------


        //При старте будем открывать последнию страницу
        //Если было что-то
        if (Main.save_read_int(Main.ACTIV_item) >= 0) {
            //И если это есть в списке
            if (read_page_list().size > Main.save_read_int(Main.ACTIV_item)) {
                //если есть локально загрузим из памяти
                if (File(read_page_list()[Main.save_read_int(Main.ACTIV_item)]).isFile) {
                    //пошлём сигнал для загрузки дааных в спискок
                    signal("Online_plalist")
                            .putExtra("update", "zaebis")
                            .putExtra("listfile", read_page_list()[Main.save_read_int(Main.ACTIV_item)])
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
        } else {
            //иначе пустую страницу покажем
            signal("Online_plalist")
                    .putExtra("update", "zaebis")
                    .send(context)
        }

        return v
    }



    fun read_page_list(): ArrayList<String> {
        val dir = File(Main.ROOT) //path указывает на директорию
        val arrFiles = dir.listFiles()

        val d = ArrayList<String>()

        if(arrFiles!= null)
        for (s in arrFiles.iterator()) {
            if (s.isFile) {
                d.add(s.absolutePath)
            }
        }
        return  d
    }

    fun deleteAllFilesFolder(path: String) {
        for (myFile in File(path).listFiles())
            if (myFile.isFile && myFile.name != "theme.txt" && myFile.name != "history_url.txt") myFile.delete()
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

}