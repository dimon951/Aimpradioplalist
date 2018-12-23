package dmitriy.deomin.aimpradioplalist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.*


class Setting : FragmentActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    private lateinit var edit_fon: Button
    private lateinit var edit_pos1: Button
    private lateinit var edit_text_color: Button
    private lateinit var bt_save_theme: Button
    private lateinit var bt_eitfonts: Button
    private lateinit var bt_temdel: Button
    lateinit var context: Context

    lateinit var list_theme: ArrayList<HashMap<String, String>>

    internal lateinit var textView_edit_color_posty: TextView
    internal lateinit var textView_edit_fon_color: TextView
    internal lateinit var textView_edit_color_text: TextView
    internal lateinit var text_logo: TextView
    internal lateinit var linerfon: LinearLayout

    var nember: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        context = this

        textView_edit_color_posty = findViewById<TextView>(R.id.textView_edit_color_posty)
        textView_edit_fon_color = findViewById<TextView>(R.id.textView_edit_fon_color)
        textView_edit_color_text = findViewById<TextView>(R.id.textView_edit_color_text)
        bt_eitfonts = findViewById<Button>(R.id.button_edit_fonts)
        edit_fon = findViewById<Button>(R.id.button_edit_fon_color)
        edit_pos1 = findViewById<Button>(R.id.button_edit_color_posty)
        edit_text_color = findViewById<Button>(R.id.button_edit_color_text)
        text_logo = findViewById<TextView>(R.id.logo_pagera_inumber)
        bt_save_theme = findViewById<Button>(R.id.save_this_theme)
        linerfon = findViewById<LinearLayout>(R.id.fon_setting)
        bt_temdel = findViewById<Button>(R.id.button_themedel)

        edit_fon.setOnClickListener {
            DIALOG_ID = 0
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.fon), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        edit_pos1.setOnClickListener {
            DIALOG_ID = 1
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.green), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        edit_text_color.setOnClickListener {
            DIALOG_ID = 3
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, Color.BLACK, true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }




        //по умолчанию 0
        nember = Main.save_read_int("nember")

        ///устанавливаем цвет и шрифт и сохраняется заодно
        pererisovka_color()

        list_theme = read_mamory()
        //проверим номер
        if(nember>list_theme.size){
            nember = 0
        }

        text_logo.text = "Установлена тема :" + (1+nember).toString() + " из:" + list_theme.size.toString()


        bt_save_theme.onClick { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v?.startAnimation(anim)
            //ждём сигнала что все пучком

            //фильтр для нашего сигнала
            val intentFilter = IntentFilter()
            intentFilter.addAction("File_created")

            //приёмник  сигналов
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    if (intent.action == "File_created") {
                        //получим данные
                        val s = intent.getStringExtra("update")
                        if (s == "zaebis") {
                            context.toast("Тема сохранена")

                            list_theme = read_mamory()
                            text_logo.text = "Установлена тема :" + nember.toString() + " из:" + list_theme.size.toString()
                        } else {
                            context.toast("Ошибка")
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

            //сохраням текущию тему в файл (дополняем его еще одной темой)
            //пришлёт сигнал в ответ
            add_new_theme()
        }

        bt_temdel.onClick { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v?.startAnimation(anim)
            //ждём сигнала что все пучком

            //фильтр для нашего сигнала
            val intentFilter = IntentFilter()
            intentFilter.addAction("File_created")

            //приёмник  сигналов
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    if (intent.action == "File_created") {
                        //получим данные
                        val s = intent.getStringExtra("update")
                        if (s == "zaebis") {
                            context.toast("Удалено")

                            list_theme = read_mamory()
                            text_logo.text = "Установлена тема :" + (1 + nember).toString() + " из:" + list_theme.size.toString()
                        } else {
                            context.toast("Ошибка")
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

            //пришлёт сигнал в ответ
            del_item_theme()
        }

        ((findViewById<Button>(R.id.button_themeleft))).onClick { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v!!.startAnimation(anim)

            if (nember > 0 && nember <= list_theme.size - 1) {
                nember--
                Main.COLOR_FON = list_theme[nember]["COLOR_FON"]?.toInt() ?: 0
                Main.COLOR_ITEM = list_theme[nember]["COLOR_ITEM"]?.toInt() ?: 0
                // Main.COLOR_TEXT = list_theme[nember]["COLOR_TEXT"]?.toInt() ?: Color.DKGRAY
                text_logo.text = "Установлена тема :" + (1 + nember).toString() + " из:" + list_theme.size.toString()
                pererisovka_color()
            } else {
                toast("Первая")
            }
        }
        ((findViewById<Button>(R.id.buttonthemenext))).onClick { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v!!.startAnimation(anim)

            if (nember < list_theme.size-1 && nember >= 0) {
                nember++
                Main.COLOR_FON = list_theme[nember]["COLOR_FON"]?.toInt() ?: 0
                Main.COLOR_ITEM = list_theme[nember]["COLOR_ITEM"]?.toInt() ?: 0
                //  Main.COLOR_TEXT = list_theme[nember]["COLOR_TEXT"]?.toInt() ?: Color.DKGRAY
                text_logo.text = "Установлена тема :" + (1 + nember).toString() + " из:" + list_theme.size.toString()
                pererisovka_color()
            } else {
                toast("Последняя")
            }
        }


    }

    fun pererisovka_color() {

        //сохранение
        Main.save_value_int("color_fon", Main.COLOR_FON)
        Main.save_value_int("color_post1", Main.COLOR_ITEM)
        Main.save_value_int("color_text", Main.COLOR_TEXT)

        //сохранение текущего номера темы
        Main.save_value_int("nember", nember)


        Main.liner_boss.setBackgroundColor(Main.COLOR_FON)


        text_logo.textColor = Main.COLOR_TEXT
        textView_edit_color_posty.setTextColor(Main.COLOR_TEXT)
        textView_edit_fon_color.setTextColor(Main.COLOR_TEXT)
        textView_edit_color_text.setTextColor(Main.COLOR_TEXT)
        bt_eitfonts.setTextColor(Main.COLOR_TEXT)
        linerfon.setBackgroundColor(Main.COLOR_FON)
        edit_fon.setTextColor(Main.COLOR_TEXT)
        edit_fon.setBackgroundColor(Main.COLOR_FON)
        edit_pos1.setTextColor(Main.COLOR_TEXT)
        edit_pos1.setBackgroundColor(Main.COLOR_ITEM)
        edit_text_color.setTextColor(Main.COLOR_TEXT)
        edit_text_color.setBackgroundColor(Main.COLOR_FON)
        bt_save_theme.textColor = Main.COLOR_TEXT


        bt_save_theme.typeface = Main.face
        edit_text_color.typeface = Main.face
        bt_eitfonts.typeface = Main.face
        textView_edit_color_posty.typeface = Main.face
        textView_edit_fon_color.typeface = Main.face
        textView_edit_color_text.typeface = Main.face
        edit_fon.typeface = Main.face
        edit_pos1.typeface = Main.face
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            0 -> {
                Main.save_value_int("color_fon", color)
                Main.COLOR_FON = color
                Main.liner_boss.setBackgroundColor(color)
                (findViewById<View>(R.id.fon_setting) as LinearLayout).setBackgroundColor(color)
                edit_fon.setBackgroundColor(Main.COLOR_FON)
                edit_text_color.setBackgroundColor(Main.COLOR_FON)
            }

            1 -> {
                Main.save_value_int("color_post1", color)
                Main.COLOR_ITEM = color
                edit_pos1.setBackgroundColor(Main.COLOR_ITEM)
            }

            3 -> {
                Main.save_value_int("color_text", color)
                Main.COLOR_TEXT = color

                textView_edit_color_posty.setTextColor(Main.COLOR_TEXT)
                textView_edit_fon_color.setTextColor(Main.COLOR_TEXT)
                textView_edit_color_text.setTextColor(Main.COLOR_TEXT)

                edit_fon.setTextColor(Main.COLOR_TEXT)
                edit_pos1.setTextColor(Main.COLOR_TEXT)
                edit_text_color.setTextColor(Main.COLOR_TEXT)
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {

    }


    fun read_mamory(): ArrayList<HashMap<String, String>> {
        //сохраненые данные будем брать из тойже папки где лежат плейлисты
        //прочиаем файл если он есть и есть разрешения на чтение вернётся спискок настроет иначе нечего
        val file_function = File_function()
        val f: String
        f = file_function.read(Main.MY_SAVE_SETTING_FILE)
        Log.e("весь файл", f)
        if (f.length > 1) {
            //файл настроек будет содержать строки: название_пареметра<->значение
            //разделитель +++
            //может содержать много тем, разделятся будут ----------  (10шт)
            val list_theme: ArrayList<String> = f.split("----------") as ArrayList<String>

            //удалим пустые
            list_theme.removeAll(listOf(""))

            //будет список мапов , каждый мап содержит одну тему
            val data = ArrayList<HashMap<String, String>>(list_theme.size)

            var m: MutableMap<String, String>

            Log.e("temy", list_theme.toString())

            //пройдёмся по списку тем
            for (i in list_theme.indices) {
                Log.e("темa", i.toString() + " всего:" + list_theme.size.toString())
                //из каждой темы создадим мап с ключ-начение
                val theme_parametry = list_theme[i].split("+++")
                m = HashMap()
                for (j in theme_parametry.indices) {
                    Log.e("параметры", j.toString() + " всего:" + theme_parametry.size.toString())
                    m[theme_parametry[j].split("<->")[0]] = theme_parametry[j].split("<->")[1]
                }
                data.add(m)
            }
            return data
        }
        return ArrayList<HashMap<String, String>>(0)
    }


    fun add_new_theme() {
        //читаем какае есть и дополняем к ним текущию
        //сохраненые данные будем брать из тойже папки где лежат плейлисты
        //прочиаем файл если он есть и есть разрешения на чтение вернётся спискок настроет иначе нечего
        val file_function = File_function()
        val f: String
        f = file_function.read(Main.MY_SAVE_SETTING_FILE)
        Log.e("весь файл", f)

        //файл настроек будет содержать строки: название_пареметра<->значение
        //разделитель +++
        //может содержать много тем, разделятся будут ----------  (10шт)

        //состовляем данные текущей темы
        val new_data_theme = "----------COLOR_FON<->" + Main.COLOR_FON + "+++" +
                "COLOR_ITEM<->" + Main.COLOR_ITEM + "+++" +
                "COLOR_TEXT<->" + Main.COLOR_TEXT


        Log.e("весь файл с новыми", f + new_data_theme)

        nember++

        file_function.SaveFile(Main.MY_SAVE_SETTING_FILE, f + new_data_theme)


    }
    fun del_item_theme() {
        //читаем какае есть и дополняем к ним текущию
        //сохраненые данные будем брать из тойже папки где лежат плейлисты
        //прочиаем файл если он есть и есть разрешения на чтение вернётся спискок настроет иначе нечего
        val file_function = File_function()
        var f: String
        f = file_function.read(Main.MY_SAVE_SETTING_FILE)
        Log.e("весь файл", f)

        //файл настроек будет содержать строки: название_пареметра<->значение
        //разделитель +++
        //может содержать много тем, разделятся будут ----------  (10шт)

        //удаляем выбраную тему онаже и будет установлена
        //состовляем данные текущей темы
        val del_data = "----------COLOR_FON<->" + Main.COLOR_FON + "+++" +
                "COLOR_ITEM<->" + Main.COLOR_ITEM + "+++" +
                "COLOR_TEXT<->" + Main.COLOR_TEXT


        //переключаем на тему назад(если не последняя) и удаляем текущию
        if (list_theme.size>1) {
            nember--
            Main.COLOR_FON = list_theme[nember]["COLOR_FON"]?.toInt() ?: 0
            Main.COLOR_ITEM = list_theme[nember]["COLOR_ITEM"]?.toInt() ?: 0
            // Main.COLOR_TEXT = list_theme[nember]["COLOR_TEXT"]?.toInt() ?: Color.DKGRAY
            text_logo.text = "Установлена тема :" + (1 + nember).toString() + " из:" + list_theme.size.toString()
            pererisovka_color()

            //удаляем и отправляем на перезапись
            f = f.replace(del_data,"")


            Log.e("весь файл с новыми", f)

            file_function.SaveFile(Main.MY_SAVE_SETTING_FILE, f)

        } else {
            toast("Осталась одна")
        }





    }
    fun edit_fonts(v: View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.myalpha)
        v.startAnimation(anim)
        val i = Intent(this, Fonts_vibor::class.java)
        startActivity(i)
    }
}
