package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import dmitriy.deomin.aimpradioplalist.custom.Radio
import kotlinx.android.synthetic.main.vse_radio.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller

import java.util.ArrayList


class Vse_radio : Fragment() {

    internal lateinit var context: Context
    lateinit var find: EditText
    val PREFIX = "-"


    companion object {
        var Numeracia: Int = 1
        var Poisk_ima_url: Int = 1
    }


    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vse_radio, null)
        context = container!!.context

        find = v.findViewById(R.id.editText_find)
        find.typeface = Main.face

        Numeracia = if (Main.save_read_int("setting_numer") == 1) {
            1
        } else {
            0
        }
        Poisk_ima_url = if (Main.save_read_int("setting_poisk") == 1) {
            1
        } else {
            0
        }



        val recikl_vse_list = v.findViewById<RecyclerView>(R.id.recicl_vse_radio)
        recikl_vse_list.layoutManager =  LinearLayoutManager(context)
        recikl_vse_list.setHasFixedSize(true)

        //полоса быстрой прокрутки
        val fastScroller: VerticalRecyclerViewFastScroller = v.findViewById(R.id.fast_scroller)
        fastScroller.setRecyclerView(recikl_vse_list)
        recikl_vse_list.setOnScrollListener(fastScroller.onScrollListener)


        //попробуем в корутинах
        //адаптеру будем слать список классов Radio
        val data = ArrayList<Radio>()

        GlobalScope.launch {
            //получаем список радио >1000 штук
            val mas_radio = resources.getStringArray(R.array.vse_radio)

            for (i in mas_radio.indices) {
                val m = mas_radio[i].split("\n")
                data.add(Radio(m[0], m[1]))
            }

        }

        val adapter_vse_list = Adapter_vse_list(data)
        recikl_vse_list.adapter = adapter_vse_list






        //пролистываем до нужного элемента
        if (Main.save_read("nomer_stroki") != "") {
            if (Integer.valueOf(Main.save_read("nomer_stroki")) > 0) {
                (recikl_vse_list.layoutManager as LinearLayoutManager).scrollToPosition(Integer.valueOf(Main.save_read("nomer_stroki")))

            }
        }


        // текст только что изменили в строке поиска
        find.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                adapter_vse_list.filter.filter(text)
            }
        })


        //при первой загрузке будем ставить текст на кнопке , потом при смене будем менять тамже
        val t = if (Main.save_read("button_text_filter1").isNotEmpty()) {
            Main.save_read("button_text_filter1")
        } else {
            "Дискография"
        }
        v.kod_diskografii.text = t

        //при клике будем вставлять в строку поиска для отфильтровки
        v.kod_diskografii.onClick {
            v.kod_diskografii.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))

            if (find.text.toString() == v.kod_diskografii.text) {
                find.setText("")
            } else {
                find.setText(v.kod_diskografii.text)
            }
        }
        //при долгом нажатиии будем предлогать изменить текст
        v.kod_diskografii.onLongClick {
            v.kod_diskografii.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.vvod_new_text_vse_radio_filtr, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()

            val e_t = content.findViewById<EditText>(R.id.new_text_filter_editText)
            e_t.typeface = Main.face
            e_t.setTextColor(Main.COLOR_TEXT)
            e_t.setText(if (Main.save_read("button_text_filter1").isNotEmpty()) {
                Main.save_read("button_text_filter1")
            } else {
                "Дискография"
            })

            val bt_ok = content.findViewById<Button>(R.id.new_text_filter_button)
            bt_ok.onClick {
                bt_ok.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))


                if (e_t.text.toString().isNotEmpty()) {
                    Main.save_value("button_text_filter1", e_t.text.toString())
                    v.kod_diskografii.text = Main.save_read("button_text_filter1")
                } else {
                    toast("Значения нет, восстановим по умолчанию")
                    Main.save_value("button_text_filter1", "Дискография")
                    v.kod_diskografii.text = Main.save_read("button_text_filter1")
                }
                alertDialog.cancel()
            }

        }

        v.kod_32bit.onClick {
            v.kod_32bit.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            if (find.text.toString() == (PREFIX + v.kod_32bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_32bit.text + PREFIX)
            }
        }
        v.kod_64bit.onClick {
            v.kod_64bit.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            if (find.text.toString() == (PREFIX + v.kod_64bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_64bit.text + PREFIX)
            }
        }
        v.kod_96bit.onClick {
            v.kod_96bit.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            if (find.text.toString() == (PREFIX + v.kod_96bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_96bit.text + PREFIX)
            }
        }
        v.kod_128bit.onClick {
            v.kod_128bit.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            if (find.text.toString() == (PREFIX + v.kod_128bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_128bit.text + PREFIX)
            }
        }
        v.kod_256bit.onClick {
            v.kod_256bit.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            if (find.text.toString() == (PREFIX + v.kod_256bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_256bit.text + PREFIX)
            }
        }

        val setting = v.findViewById<Button>(R.id.button_settig_vse_radio)
        setting.onClick {
            setting.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.setting_vse_radio, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()

            val num = content.findViewById<Button>(R.id.button_seting_number)
            val pouisk = content.findViewById<Button>(R.id.button_poisk)


            if (Numeracia == 1) {
                num.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                num.setTypeface(Main.face, Typeface.BOLD)
            } else {
                num.paintFlags = 0
                num.typeface = Main.face
            }

            if (Poisk_ima_url == 1) {
                pouisk.text = "Поиск по имени и адресу"
            } else {
                pouisk.text = "Поиск по имени"
            }

            num.onClick {
                num.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))

                if (Main.save_read_int("setting_numer") == 0) {
                    Main.save_value_int("setting_numer", 1)
                    Numeracia = 1
                    num.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    num.setTypeface(Main.face, Typeface.BOLD)
                    adapter_vse_list.notifyDataSetChanged()
                } else {
                    Main.save_value_int("setting_numer", 0)
                    Numeracia = 0
                    num.paintFlags = 0
                    num.typeface = Main.face
                    adapter_vse_list.notifyDataSetChanged()

                }


            }

            pouisk.onClick {
                pouisk.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.myalpha))

                if (Main.save_read_int("setting_poisk") == 1) {
                    Poisk_ima_url = 0
                    Main.save_value_int("setting_poisk", 0)
                    pouisk.text = "Поиск по имени"
                    adapter_vse_list.notifyDataSetChanged()
                } else {
                    Poisk_ima_url = 1
                    Main.save_value_int("setting_poisk", 1)
                    pouisk.text = "Поиск по имени и адресу"
                    adapter_vse_list.notifyDataSetChanged()
                }

            }


        }

        return v
    }
}