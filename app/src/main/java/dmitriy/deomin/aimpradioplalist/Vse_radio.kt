package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.`fun`.save_read
import dmitriy.deomin.aimpradioplalist.`fun`.save_read_int
import dmitriy.deomin.aimpradioplalist.`fun`.save_value
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_vse_list
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.vse_radio.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.util.*


class Vse_radio : Fragment() {

    internal lateinit var context: Context
    lateinit var find: EditText

    companion object {
        var cho_nagimali_poslednee: Int = 0
        var Numeracia: Int = 1
        var find_text = ""
    }

    @SuppressLint("WrongConstant", "InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vse_radio, null)


        context = Main.context

        find = v.findViewById<EditText>(R.id.editText_find)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

        cho_nagimali_poslednee = save_read_int("cho_nagimali_poslednee")

        Numeracia = if (save_read_int("setting_numer") == 1) {
            1
        } else {
            0
        }

        val ganrlist = listOf("-Музыка-", "-Юмор-", "-Разговорное-", "-Детское-", " -Аудиокниги-", "-Саундтреки-", "-Дискография-")

        val recikl_vse_list = v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recicl_vse_radio)
        recikl_vse_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        //полоса быстрой прокрутки
        val fastScroller = v.findViewById<VerticalRecyclerViewFastScroller>(R.id.fast_scroller)
        //получим текущие пораметры
        val paramL = fastScroller.layoutParams
        //меняем ширину
        paramL.width = Main.SIZE_WIDCH_SCROLL
        //устанавливаем
        fastScroller.layoutParams = paramL
        fastScroller.setRecyclerView(recikl_vse_list)
        recikl_vse_list.setOnScrollListener(fastScroller.onScrollListener)

        //адаптеру будем слать список классов Radio
        val data = ArrayList<Radio>()

        GlobalScope.launch {

            //получаем список радио >1000 штук
            val mas_radio = resources.getStringArray(R.array.vse_radio)

            for (i in mas_radio.indices) {
                val m = mas_radio[i].split("\n")
                if (m.size > 1) {
                    var name = m[0]



                    //---kbps----------------------------------
                    var kbps = ""
                    if (name.contains("kbps")) {
                        //если имени нет(длинна меньше 7) поставим no name
                        if (name.length > 7) {
                            if(name.contains("mono")){
                                kbps = name.substring((name.length - 11), name.length)
                                name = name.substring(0, (name.length - 11))
                            }else{
                                kbps = name.substring((name.length - 7), name.length)
                                name = name.substring(0, (name.length - 7))
                            }
                        } else {
                            kbps = name
                            name = "no name"
                        }
                    }
                    //-------------------------------------------------------

                    //---mono----------------------------------------
                    if (name.contains("mono")) {
                        kbps = "mono " + kbps
                        name = name.replace("mono", "")
                    }
                    //------------------------------------------

                    //--ganr--------------------------------------------------
                    var ganr = ""
                    for (g in ganrlist) {
                        if (name.contains(g)) {
                            name = name.replace(g, "")
                            ganr = g.replace("-", "")
                        }
                    }
                    //-----------------------------------------------------

                    data.add(Radio(name, ganr, kbps, m[1]))
                }
            }

            //пошлём сигнал в маин чтобы отключил показ прогресс бара
            //он нам пошлёт в обратку сигнал "update_vse_radio"
            signal("Main_update")
                    .putExtra("signal", "load_stop_vse_radio")
                    .send(context)

        }


        //когда все распарсится и в маине отключится показ прогрессбара прилетит  сигнал
        // и запустит этот слот один раз походу так хз
        //------------------------------------------------------------------------------
        Slot(context, "update_vse_radio", false).onRun {

            val adapter_vse_list = Adapter_vse_list(data)
            recikl_vse_list.adapter = adapter_vse_list

            //пролистываем до нужного элемента
            if (cho_nagimali_poslednee > 0 && adapter_vse_list.raduoSearchList.size > cho_nagimali_poslednee) {
                try {
                    recikl_vse_list.scrollToPosition(cho_nagimali_poslednee)
                }catch (e:Exception){
                    context.toast("Не найдена позиция в списке")
                }

            }

            // текст только что изменили в строке поиска
            find.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                    find_text = text.toString()
                    adapter_vse_list.filter.filter(find_text)
                }
            })

            //этот слот будет висеть и если что будет сохранять текущий список в память
            Slot(context, "save_all_vse_lest").onRun {
                adapter_vse_list.notifyDataSetChanged()
                val data = adapter_vse_list.raduoSearchList
                if (data.isNotEmpty()) {
                    GlobalScope.launch {
                        Slot(context, "File_created", false).onRun {
                            //получим данные
                            when (it.getStringExtra("update")) {
                                //узнаем результат сохранения
                                "zaebis" -> Main.context.toast("Весь список сохранен в " + it.getStringExtra("name"))
                                "pizdec" -> Main.context.toast(Main.context.getString(R.string.error))
                            }
                            //послать сигнал отключить анимацию на кнопке
                            signal("File_created_save_vse").putExtra("anim", "anim_of").send(Main.context)
                        }
                        create_m3u_file(it.getStringExtra("name_list"),data)
                    }
                }
            }
        }
        //---------------------------------------------------------------
        //установим размер текста копкам поиска
        v.kod_diskografii.textSize=Main.SIZE_TEXT_VSE_BUTTON
        v.kod_32bit.textSize=Main.SIZE_TEXT_VSE_BUTTON
        v.kod_64bit.textSize=Main.SIZE_TEXT_VSE_BUTTON
        v.kod_96bit.textSize=Main.SIZE_TEXT_VSE_BUTTON
        v.kod_128bit.textSize=Main.SIZE_TEXT_VSE_BUTTON
        v.kod_256bit.textSize=Main.SIZE_TEXT_VSE_BUTTON


        //при первой загрузке будем ставить текст на кнопке , потом при смене будем менять тамже
        val t = if (save_read("button_text_filter1").isNotEmpty()) {
            save_read("button_text_filter1")
        } else {
            "Дискография"
        }
        v.kod_diskografii.text = t

        //при клике будем вставлять в строку поиска для отфильтровки
        v.kod_diskografii.onClick {
            if (find.text.toString() == v.kod_diskografii.text) {
                find.setText("")
            } else {
                find.setText(v.kod_diskografii.text)
            }
        }
        //при долгом нажатиии будем предлогать изменить текст
        v.kod_diskografii.onLongClick {

            val vntvrf = DialogWindow(context, R.layout.vvod_new_text_vse_radio_filtr)

            val e_t = vntvrf.view().findViewById<EditText>(R.id.new_text_filter_editText)
            e_t.typeface = Main.face
            e_t.setTextColor(Main.COLOR_TEXT)
            e_t.setText(if (save_read("button_text_filter1").isNotEmpty()) {
                save_read("button_text_filter1")
            } else {
                "Дискография"
            })

            (vntvrf.view().findViewById<Button>(R.id.new_text_filter_button)).onClick {

                if (e_t.text.toString().isNotEmpty()) {
                    save_value("button_text_filter1", e_t.text.toString())
                    v.kod_diskografii.text = save_read("button_text_filter1")
                } else {
                    toast("Значения нет, восстановим по умолчанию")
                    save_value("button_text_filter1", "Дискография")
                    v.kod_diskografii.text = save_read("button_text_filter1")
                }
                vntvrf.close()
            }
        }

        v.kod_32bit.onClick {

            if (find.text.toString() == (v.kod_32bit.text)) {
                find.setText("")
            } else {
                find.setText(v.kod_32bit.text)
            }
        }
        v.kod_64bit.onClick {

            if (find.text.toString() == (v.kod_64bit.text)) {
                find.setText("")
            } else {
                find.setText(v.kod_64bit.text)
            }
        }
        v.kod_96bit.onClick {

            if (find.text.toString() == (v.kod_96bit.text)) {
                find.setText("")
            } else {
                find.setText(v.kod_96bit.text)
            }
        }
        v.kod_128bit.onClick {

            if (find.text.toString() == (v.kod_128bit.text)) {
                find.setText("")
            } else {
                find.setText(v.kod_128bit.text)
            }
        }
        v.kod_256bit.onClick {

            if (find.text.toString() == (v.kod_256bit.text)) {
                find.setText("")
            } else {
                find.setText(v.kod_256bit.text)
            }
        }

        return v
    }

}