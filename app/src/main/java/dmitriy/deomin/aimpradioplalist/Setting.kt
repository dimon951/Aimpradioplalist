package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.app.DialogFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
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
import java.util.ArrayList


class Setting : Activity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    private lateinit var edit_fon: Button
    private lateinit var edit_pos1: Button
    private lateinit var edit_text_color: Button
    lateinit var context:Context

    internal lateinit var textView_edit_color_posty: TextView
    internal lateinit var textView_edit_fon_color: TextView
    internal lateinit var textView_edit_color_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        context = this

        //ставим шрифт и цвет текста
        textView_edit_color_posty = findViewById<View>(R.id.textView_edit_color_posty) as TextView
        textView_edit_color_posty.typeface = Main.face
        textView_edit_color_posty.setTextColor(Main.COLOR_TEXT)

        textView_edit_fon_color = findViewById<View>(R.id.textView_edit_fon_color) as TextView
        textView_edit_fon_color.typeface = Main.face
        textView_edit_fon_color.setTextColor(Main.COLOR_TEXT)

        textView_edit_color_text = findViewById<View>(R.id.textView_edit_color_text) as TextView
        textView_edit_color_text.typeface = Main.face
        textView_edit_color_text.setTextColor(Main.COLOR_TEXT)


        (findViewById<View>(R.id.button_edit_fonts) as Button).typeface = Main.face
        (findViewById<View>(R.id.button_edit_fonts) as Button).setTextColor(Main.COLOR_TEXT)

        //ставим цвет
        (findViewById<View>(R.id.fon_setting) as LinearLayout).setBackgroundColor(Main.COLOR_FON)

        edit_fon = findViewById<View>(R.id.button_edit_fon_color) as Button
        edit_fon.typeface = Main.face
        edit_fon.setTextColor(Main.COLOR_TEXT)
        edit_fon.setBackgroundColor(Main.COLOR_FON)
        edit_fon.setOnClickListener {
            DIALOG_ID = 0
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.fon), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        edit_pos1 = findViewById<View>(R.id.button_edit_color_posty) as Button
        edit_pos1.typeface = Main.face
        edit_pos1.setTextColor(Main.COLOR_TEXT)
        edit_pos1.setTextColor(Main.COLOR_TEXT)
        edit_pos1.setBackgroundColor(Main.COLOR_ITEM)
        edit_pos1.setOnClickListener {
            DIALOG_ID = 1
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.green), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }


        edit_text_color = findViewById<View>(R.id.button_edit_color_text) as Button
        edit_text_color.typeface = Main.face
        edit_text_color.setTextColor(Main.COLOR_TEXT)
        edit_text_color.setBackgroundColor(Main.COLOR_FON)
        edit_text_color.setOnClickListener {
            DIALOG_ID = 3
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, Color.BLACK, true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        val bt_save_theme = findViewById<View>(R.id.save_this_theme) as Button
        bt_save_theme.typeface = Main.face
        bt_save_theme.textColor = Main.COLOR_TEXT
        bt_save_theme.onClick {v->
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
                            context.toast("Сохранено")
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

        //в рецикле будет пример нескольких цветовых тем


    }


    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            0 -> {
                Main.save_value_int("color_fon", color)
                Main.COLOR_FON = color
                Main.liner_boss.setBackgroundColor(color)
                (findViewById<View>(R.id.fon_setting) as LinearLayout).setBackgroundColor(color)
                edit_fon.setBackgroundColor(Main.COLOR_FON)
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
            val list_theme = f.split("----------")

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

        file_function.SaveFile(Main.MY_SAVE_SETTING_FILE, f + new_data_theme)


    }

    fun edit_fonts(v: View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.myalpha)
        v.startAnimation(anim)
        val i = Intent(this, Fonts_vibor::class.java)
        startActivity(i)
    }
}
