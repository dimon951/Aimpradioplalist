package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.vse_radio.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.support.v4.toast

import java.util.ArrayList
import java.util.HashMap


class Vse_radio : Fragment() {
    private lateinit var listView: ListView
    internal lateinit var context: Context
    lateinit var find:EditText
    private val STANCIA = "stancia"


    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vse_radio, null)
        context = container!!.context

        find = v.findViewById<View>(R.id.editText_find) as EditText
        find.typeface = Main.face

        listView = v.findViewById<View>(R.id.listviw_vse_radio) as ListView
        listView.isFastScrollAlwaysVisible = true

        val mas_radio = resources.getStringArray(R.array.vse_radio)

        val data = ArrayList<Map<String, Any>>(mas_radio.size)

        var m: MutableMap<String, Any>

        for (i in mas_radio.indices) {
            m = HashMap()
            m[STANCIA] = mas_radio[i]
            data.add(m)
        }

        // массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf(STANCIA)
        // массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(R.id.textView)

        val adapter_vse_radio = Adapter_vse_radio(context, data, R.layout.delegat_vse_radio_list, from, to)
        listView.adapter = adapter_vse_radio
        listView.isTextFilterEnabled = true


        //пролистываем до нужного элемента
        if (Main.save_read("nomer_stroki") != null) {
            if (Main.save_read("nomer_stroki") != "") {
                if (Integer.valueOf(Main.save_read("nomer_stroki")) > 0) {
                    listView.setSelection(Integer.valueOf(Main.save_read("nomer_stroki")))
                }
            }
        }


        // текст только что изменили в строке поиска
        find.textChangedListener { onTextChanged{text, start, before, count ->adapter_vse_radio.filter.filter(text)}}


        v.kod_diskografii.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_diskografii.startAnimation(anim)
            if (find.text.toString() == v.kod_diskografii.text) {
                find.setText("")
            } else {
                find.setText(v.kod_diskografii.text)
            }
        }
        v.kod_32bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_32bit.startAnimation(anim)
            if (find.text.toString() == v.kod_32bit.text) {
                find.setText("")
            } else {
                find.setText(v.kod_32bit.text)
            }
        }
        v.kod_64bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_64bit.startAnimation(anim)
            if (find.text.toString() == v.kod_64bit.text) {
                find.setText("")
            } else {
                find.setText(v.kod_64bit.text)
            }
        }
        v.kod_96bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_96bit.startAnimation(anim)
            if (find.text.toString() == v.kod_96bit.text) {
                find.setText("")
            } else {
                find.setText(v.kod_96bit.text)
            }
        }
        v.kod_128bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_128bit.startAnimation(anim)
            if (find.text.toString() == v.kod_128bit.text) {
                find.setText("")
            } else {
                find.setText(v.kod_128bit.text)
            }
        }
        v.kod_256bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_256bit.startAnimation(anim)
            if (find.text.toString() == v.kod_256bit.text) {
                find.setText("")
            } else {
                find.setText(v.kod_256bit.text)
            }
        }


        //Обрабатываем щелчки на элементах ListView:
        listView.onItemClickListener = AdapterView.OnItemClickListener { a, v, position, id ->
            //реализация

            //сохраняем позицию
            Main.save_value("nomer_stroki", position.toString())

            //обратываем
            var k = a.adapter.getItem(position).toString()
            k = k.substring(9, k.length - 1)
            val mas = k.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val name = mas[0]
            val url_link = mas[1]

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.menu_vse_radio, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()

            val add_pls = content.findViewById<View>(R.id.button_add_plalist) as Button
            val open_aimp = content.findViewById<View>(R.id.button_open_aimp) as Button
            val instal_aimp = content.findViewById<View>(R.id.button_instal_aimp) as Button
            val instal_aimp2 = content.findViewById<View>(R.id.button_download_yandex_aimp) as Button
            val open_sistem = content.findViewById<View>(R.id.button_open_sistem) as Button


            //если aimp установлен скроем кнопку установить аимп
            if (Main.install_app("com.aimp.player")) {
                instal_aimp.visibility = View.GONE
                instal_aimp2.visibility = View.GONE
                open_aimp.visibility = View.VISIBLE
                open_aimp.typeface = Main.face
                open_aimp.setTextColor(Main.COLOR_TEXT)
            } else {
                //если есть магазин покажем и установку через него
                if (Main.install_app("com.google.android.gms")) {
                    instal_aimp.visibility = View.VISIBLE
                    instal_aimp.typeface = Main.face
                    instal_aimp.setTextColor(Main.COLOR_TEXT)
                } else {
                    instal_aimp.visibility = View.GONE
                }

                //скачать по ссылке будем показывать всегда
                instal_aimp2.visibility = View.VISIBLE
                instal_aimp2.typeface = Main.face
                instal_aimp2.setTextColor(Main.COLOR_TEXT)

                open_aimp.visibility = View.GONE

            }




            add_pls.typeface = Main.face
            add_pls.setTextColor(Main.COLOR_TEXT)
            open_sistem.typeface = Main.face
            open_sistem.setTextColor(Main.COLOR_TEXT)

            val text_name_i_url = content.findViewById<View>(R.id.textView_vse_radio) as TextView
            text_name_i_url.text = name + "\n" + url_link
            text_name_i_url.setOnClickListener { view ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                view.startAnimation(anim)
                putText(url_link, context)
                Toast.makeText(context, "url скопирован в буфер", Toast.LENGTH_SHORT).show()
            }



            open_sistem.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)

                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u", url_link)

                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                //проверим есть чем открыть или нет
                if (i.resolveActivity(Main.context.packageManager) != null) {
                    Main.context.startActivity(i)
                } else {
                    Toast.makeText(context, "Системе не удалось ( ", Toast.LENGTH_LONG).show()
                }
            }


            instal_aimp.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=com.aimp.player")
                Main.context.startActivity(intent)
            }

            instal_aimp2.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                val openlink = Intent(Intent.ACTION_VIEW, Uri.parse("https://yadi.sk/d/XYi0fZps3RrD9i"))
                startActivity(openlink)
            }


            add_pls.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                Main.number_page = 0
                //фильтр для нашего сигнала
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //приёмник  сигналов
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == "File_created") {
                            //получим данные
                            val s = intent.getStringExtra("update")
                            if (s == "zaebis") {
                                //  Toast.makeText(context,"Готово",Toast.LENGTH_SHORT).show();
                                alertDialog.cancel()
                                //обновим старницу
                                Main.myadapter.notifyDataSetChanged()
                                Main.viewPager.adapter = Main.myadapter
                                Main.viewPager.currentItem = Main.number_page
                            } else {
                                toast("Ошибочка вышла тыкниете еще раз")
                            }
                        }
                    }
                }

                //регистрируем приёмник
                Main.context.registerReceiver(broadcastReceiver, intentFilter)


                val file_function = File_function()
                file_function.Add_may_plalist_stansiy(url_link,name)
            }

            open_aimp.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u", url_link)

                //откроем файл с сылкой в плеере
                val cm = ComponentName(
                        "com.aimp.player",
                        "com.aimp.player.views.MainActivity.MainActivity")

                val intent = Intent()
                intent.component = cm

                intent.action = Intent.ACTION_VIEW
                intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                intent.flags = 0x3000000

                startActivity(intent)
            }
        }


        return v
    }


    //запись
    fun putText(text: String, context: Context) {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText(text, text)
            clipboard.primaryClip = clip
        }
    }

}
