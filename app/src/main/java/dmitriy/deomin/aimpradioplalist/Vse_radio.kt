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

import java.util.ArrayList
import java.util.HashMap


class Vse_radio : Fragment() {
    private lateinit var listView: ListView
    internal lateinit var context: Context
    lateinit var find: EditText
    private val STANCIA = "stancia"
    val PREFIX = "-"


    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vse_radio, null)
        context = container!!.context

        find = v.findViewById(R.id.editText_find)
        find.typeface = Main.face

        listView = v.findViewById<ListView>(R.id.listviw_vse_radio)
        listView.isFastScrollAlwaysVisible = true

        //получаем список радио >1000 штук
        val mas_radio = resources.getStringArray(R.array.vse_radio)

        val data = ArrayList<Map<String, String>>(mas_radio.size)

        var m: MutableMap<String, String>

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
        if (Main.save_read("nomer_stroki") != "") {
            if (Integer.valueOf(Main.save_read("nomer_stroki")) > 0) {
                listView.setSelection(Integer.valueOf(Main.save_read("nomer_stroki")))

            }
        }


        // текст только что изменили в строке поиска
        find.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                adapter_vse_radio.filter.filter(text)
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
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_diskografii.startAnimation(anim)

            if (find.text.toString() == v.kod_diskografii.text) {
                find.setText("")
            } else {
                find.setText(v.kod_diskografii.text)
            }
        }
        //при долгом нажатиии будем предлогать изменить текст
        v.kod_diskografii.onLongClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_diskografii.startAnimation(anim)

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.vvod_new_text_vse_radio_filtr, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()


            (content.findViewById<TextView>(R.id.new_text_filter_logo)).typeface = Main.face
            (content.findViewById<TextView>(R.id.new_text_filter_logo)).textColor = Main.COLOR_TEXT

            val e_t = content.findViewById<EditText>(R.id.new_text_filter_editText)
            e_t.typeface = Main.face
            e_t.setTextColor(Main.COLOR_TEXT)
            e_t.setText(if (Main.save_read("button_text_filter1").length >= 1) {
                Main.save_read("button_text_filter1")
            } else {
                "Дискография"
            })

            val bt_ok = content.findViewById<Button>(R.id.new_text_filter_button)
            bt_ok.typeface = Main.face
            bt_ok.setTextColor(Main.COLOR_TEXT)
            bt_ok.onClick {
                bt_ok.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))


                if (e_t.text.toString().length >= 1) {
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
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_32bit.startAnimation(anim)
            if (find.text.toString() == (PREFIX + v.kod_32bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_32bit.text + PREFIX)
            }
        }
        v.kod_64bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_64bit.startAnimation(anim)
            if (find.text.toString() == (PREFIX + v.kod_64bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_64bit.text + PREFIX)
            }
        }
        v.kod_96bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_96bit.startAnimation(anim)
            if (find.text.toString() == (PREFIX + v.kod_96bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_96bit.text + PREFIX)
            }
        }
        v.kod_128bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_128bit.startAnimation(anim)
            if (find.text.toString() == (PREFIX + v.kod_128bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_128bit.text + PREFIX)
            }
        }
        v.kod_256bit.onClick {
            val anim = AnimationUtils.loadAnimation(v.context, R.anim.myalpha)
            v.kod_256bit.startAnimation(anim)
            if (find.text.toString() == (PREFIX + v.kod_256bit.text + PREFIX)) {
                find.setText("")
            } else {
                find.setText(PREFIX + v.kod_256bit.text + PREFIX)
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



            open_sistem.onClick {
                open_sistem.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //когда прийдёт сигнал что сохранилось передадим этот файл в аим для открытия
                //-----------------------------------------------------------------------
                //приёмник  сигналов
                // фильтр для приёмника
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(c: Context, intent: Intent) {
                        if (intent.action == "File_created") {
                            //получим данные
                            val s = intent.getStringExtra("update")
                            if (s == "zaebis") {

                                val i = Intent(android.content.Intent.ACTION_VIEW)
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                                //проверим есть чем открыть или нет
                                if (i.resolveActivity(Main.context.packageManager) != null) {
                                    context.startActivity(i)
                                } else {
                                    context.toast("Системе не удалось ( ")
                                }

                            } else {
                                context.toast(context.getString(R.string.error))
                                //Изменим текущию вкладку при обновлении что тутж остаться
                                Main.number_page = 0
                                //запросим разрешения
                                Main.EbuchieRazreshenia()
                            }
                            //попробуем уничтожить слушителя
                            context.unregisterReceiver(this)
                        }
                    }
                }
                //регистрируем приёмник
                context.registerReceiver(broadcastReceiver, intentFilter)
                //------------------------------------------------------------------------------------------------

                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u", url_link)

            }


            instal_aimp.onClick {
                instal_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                browse("market://details?id=com.aimp.player")
            }

            instal_aimp2.onClick {
                instal_aimp2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                browse(Main.LINK_DOWLOAD_AIMP)
            }


            add_pls.onClick {
                add_pls.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

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
                            when (s) {
                                "est" -> context.toast("Такая станция уже есть в плейлисте")
                                "zaebis" -> {
                                    //пошлём сигнал пусть мой плейлист обновится
                                    val i = Intent("Data_add")
                                    i.putExtra("update", "zaebis")
                                    context.sendBroadcast(i)
                                }
                                "pizdec" -> {
                                    context.toast(context.getString(R.string.error))
                                    //запросим разрешения
                                    Main.EbuchieRazreshenia()
                                }
                            }
                            //попробуем уничтожить слушителя
                            context.unregisterReceiver(this)
                        }
                    }
                }

                //регистрируем приёмник
                context.registerReceiver(broadcastReceiver, intentFilter)


                val file_function = File_function()
                file_function.Add_may_plalist_stansiy(url_link, name)

                alertDialog.cancel()
            }

            open_aimp.onClick {
                open_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //когда прийдёт сигнал что сохранилось передадим этот файл в аим для открытия
                //-----------------------------------------------------------------------
                //приёмник  сигналов
                // фильтр для приёмника
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(c: Context, intent: Intent) {
                        if (intent.action == "File_created") {
                            //получим данные
                            val s = intent.getStringExtra("update")
                            if (s == "zaebis") {

                                //откроем файл с сылкой в плеере
                                val cm = ComponentName(
                                        "com.aimp.player",
                                        "com.aimp.player.views.MainActivity.MainActivity")

                                val i = Intent()
                                i.component = cm

                                i.action = Intent.ACTION_VIEW
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                                i.flags = 0x3000000

                                context.startActivity(i)

                            } else {
                                context.toast(context.getString(R.string.error))
                                //Изменим текущию вкладку при обновлении что тутж остаться
                                Main.number_page = 0
                                //запросим разрешения
                                Main.EbuchieRazreshenia()
                            }
                            //попробуем уничтожить слушителя
                            context.unregisterReceiver(this)
                        }
                    }
                }
                //регистрируем приёмник
                context.registerReceiver(broadcastReceiver, intentFilter)
                //------------------------------------------------------------------------------------------------


                //сохраним  временый файл ссылку и будем ждать сигнала
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u",
                        "#EXTM3U"
                                + "\n"
                                + "#EXTINF:-1," + "$name.m3u"
                                + "\n"
                                + url_link)


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


class Adapter_vse_radio(context: Context, data: List<Map<String, *>>, resource: Int, from: Array<String>, to: IntArray) : SimpleAdapter(context, data, resource, from, to)