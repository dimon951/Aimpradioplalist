package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_FON
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_ITEM
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_TEXT
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_TEXTcontext
import dmitriy.deomin.aimpradioplalist.Main.Companion.face
import dmitriy.deomin.aimpradioplalist.Main.Companion.save_value_int
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.setting.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor


class Setting : FragmentActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    private lateinit var edit_fon: Button
    private lateinit var edit_pos1: Button
    private lateinit var edit_text_color: Button
    private lateinit var edit_textcontext_color: Button
    lateinit var context: Context

    private lateinit var linerfon: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        context = this

        edit_fon = findViewById(R.id.button_edit_fon_color)
        edit_pos1 = findViewById(R.id.button_edit_color_posty)
        edit_text_color = findViewById(R.id.button_edit_color_text)
        edit_textcontext_color = findViewById(R.id.button_edit_color_textcontext)
        linerfon = findViewById(R.id.fon_setting)

        //устанавливаем шрифт
        edit_fon.typeface = face
        edit_pos1.typeface = face
        edit_text_color.typeface = face


        edit_fon.onClick {
            DIALOG_ID = 0
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.fon), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        edit_pos1.onClick {
            DIALOG_ID = 1
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.green), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        edit_text_color.onClick {
            DIALOG_ID = 3
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, Color.BLACK, true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }
        edit_textcontext_color.onClick {
            DIALOG_ID = 4
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.textcontext), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }


        //----список тем-------------------------
        if(Main.save_read_int("visible")==1){
            list_them.visibility=View.VISIBLE
        }else{
            list_them.visibility=View.GONE
        }

        open_list_them.onClick {
            if(list_them.visibility== View.GONE){
                list_them.visibility=View.VISIBLE
                Main.save_value_int("visible",1)
            }else{
                list_them.visibility = View.GONE
                Main.save_value_int("visible",0)
            }
        }

        list_them.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        list_them.setHasFixedSize(true)

        //одна строка - тема
        val data_res = resources.getStringArray(R.array.list_theme)

        val data=ArrayList<Theme>()

        for(i in data_res.indices){
            val tl = data_res[i].split("$")
            if(tl.size>4){
                data.add(Theme(tl[0],tl[1].toInt(),tl[2].toInt(),tl[3].toInt(),tl[4].toInt()))
            }
        }



        val adapter_list_theme = Adapter_list_theme(data)
        list_them.adapter = adapter_list_theme
        //----------------------------------------



       //Будем слушать когда тему тыкнут
        Slot(context,"pererisovka").onRun {
            pererisovka_color()
        }
        ///устанавливаем цвет и шрифт и сохраняется заодно
        pererisovka_color(false)
    }


    class Adapter_list_theme(val data: ArrayList<Theme>) : RecyclerView.Adapter<Adapter_list_theme.ViewHolder>() {

        private lateinit var context: Context



        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name_theme = itemView.findViewById<Button>(R.id.name_thme)
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_theme, p0, false)
            context = p0.context
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


            //заполним данными(тут в логах бывает падает - обращение к несуществующему элементу)
            //поэтому будем проверять чтобы общее количество было больше текушего номера
            val theme: Theme = if(this.data.size>p1){
                this.data[p1]
            }else{
                //иначе вернём пустой элемент(дальше будут проверки и он не отобразится)
                Theme("",Main.COLOR_FON,Main.COLOR_ITEM,Main.COLOR_TEXT,Main.COLOR_TEXTcontext)
            }

            p0.name_theme.text = theme.name
            p0.name_theme.textColor = theme.text
            p0.name_theme.backgroundColor = theme.fon

            p0.name_theme.onClick {
                //обновляем константы
                COLOR_FON=theme.fon
                COLOR_TEXT = theme.text
                COLOR_ITEM = theme.item
                COLOR_TEXTcontext = theme.text_context
                // сохраняем в память
                save_value_int("color_fon", COLOR_FON)
                save_value_int("color_post1", COLOR_ITEM)
                save_value_int("color_text", COLOR_TEXT)
                save_value_int("color_textcontext", COLOR_TEXTcontext)
                // перерисовываем
                signal("pererisovka").send(context)
            }


        }
    }

    fun pererisovka_color(update_main:Boolean=true) {

        if(update_main){
            //пошлём сигнал пусть обновится все остальное
            signal("Main_update").putExtra("signal", "update_color").send(Main.context)
        }
        linerfon.backgroundColor = COLOR_FON
        edit_fon.textColor = COLOR_TEXT
        edit_fon.backgroundColor = COLOR_FON
        edit_pos1.textColor = COLOR_TEXT
        edit_pos1.backgroundColor = COLOR_ITEM
        edit_text_color.textColor = COLOR_TEXT
        edit_textcontext_color.textColor = COLOR_TEXTcontext
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            0 -> {
                save_value_int("color_fon", color)
                COLOR_FON = color
                pererisovka_color()
            }

            1 -> {
                save_value_int("color_post1", color)
                COLOR_ITEM = color
                pererisovka_color()
            }

            3 -> {
                save_value_int("color_text", color)
                COLOR_TEXT = color
                pererisovka_color()
            }
            4 -> {
                save_value_int("color_textcontext", color)
                COLOR_TEXTcontext = color
                pererisovka_color()
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}

}
