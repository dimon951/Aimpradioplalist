package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import kotlinx.android.synthetic.main.vse_radio.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller

import java.util.ArrayList
import java.util.HashMap


class Vse_radio : Fragment() {

    internal lateinit var context: Context
    lateinit var find: EditText
    val PREFIX = "-"


    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vse_radio, null)
        context = container!!.context

        find = v.findViewById(R.id.editText_find)
        find.typeface = Main.face


        val recikl_vse_list = v.findViewById<RecyclerView>(R.id.recicl_vse_radio)
        recikl_vse_list.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        recikl_vse_list.setHasFixedSize(true)

        val fastScroller: VerticalRecyclerViewFastScroller = v.findViewById(R.id.fast_scroller)

        fastScroller.setRecyclerView(recikl_vse_list)

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recikl_vse_list.setOnScrollListener(fastScroller.getOnScrollListener())




        //получаем список радио >1000 штук
        val mas_radio = resources.getStringArray(R.array.vse_radio)


        //адаптеру будем слать список классов Radio
        val data = ArrayList<Radio>()

        for (i in mas_radio.indices) {
            val m = mas_radio[i].split("\n")
            data.add(Radio(m[0], m[1]))
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

        return v
    }
}