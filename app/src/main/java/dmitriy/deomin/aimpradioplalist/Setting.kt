package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_FON
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_ITEM
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_SELEKT
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_TEXT
import dmitriy.deomin.aimpradioplalist.Main.Companion.COLOR_TEXTcontext
import dmitriy.deomin.aimpradioplalist.Main.Companion.face
import dmitriy.deomin.aimpradioplalist.Main.Companion.save_value_int
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.setting.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.*


class Setting : FragmentActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    private lateinit var edit_fon: Button
    private lateinit var edit_pos1: Button
    private lateinit var edit_text_color: Button
    private lateinit var edit_textcontext_color: Button
    lateinit var context: Context

    private lateinit var linerfon: LinearLayout

    @SuppressLint("WrongConstant")
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
        button_edit_color_selekt.typeface = face


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

        button_edit_color_selekt.onClick {
            DIALOG_ID = 5
            val f = ColorPickerDialogFragment
                    .newInstance(DIALOG_ID, null, null, resources.getColor(R.color.textselekt), true)

            f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme)
            f.show(fragmentManager, "d")
        }

        //сохраниьт в список тему
        button_save_them_list.onClick {

            var name_them = ""
            //покажем оконо в котором нужно будет ввести имя
            val nsf = DialogWindow(context, R.layout.name_save_file)

            val name = nsf.view().findViewById<EditText>(R.id.edit_new_name)
            name.hint = "Моя тема"
            name.typeface = face
            name.textColor = COLOR_TEXT

            (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                if (name.text.toString().isEmpty()) {
                    //пока покажем это потом будум генерерить свои если не захотят вводить
                    context.toast("Введите имя")
                } else {
                    name_them = name.text.toString()

                    //сначала получим сохранёные данные а потом к ним допишем
                    val f = File_function()
                    val savedata = f.readArrayList(Main.F_THEM_list)
                    savedata.add(name_them+"$"+COLOR_FON+"$"+COLOR_ITEM+"$"+COLOR_TEXT+"$"+COLOR_TEXTcontext+"$"+ COLOR_SELEKT)
                    f.saveArrayList(Main.F_THEM_list,savedata)

                    //закроем окошко
                    nsf.close()
                }
            }


            //когда все запишется пошлём сигнал чтобы добавилась тема в список
            Slot(context, "File_created_save_vse", false).onRun {
                //получим данные
                val s = it.getStringExtra("update")
                when (s) {
                    //пошлём сигнал пусть мой плейлист обновится
                    "zaebis" -> {signal("list_them__load").send(context);context.toast("Готово");}
                    "pizdec" -> {
                        context.toast(context.getString(R.string.error))
                        //запросим разрешения
                        Main.EbuchieRazreshenia()
                    }
                }
            }


        }

        //----список тем-------------------------
        //---------------------------------------------------------------------------------
        if(Main.save_read_int("visible")==1){
            list_them.visibility=View.VISIBLE
        }else{
            list_them.visibility=View.GONE
        }

        open_list_them.onClick {
            if(list_them.visibility== View.GONE){
                list_them.visibility=View.VISIBLE
                save_value_int("visible",1)
            }else{
                list_them.visibility = View.GONE
                save_value_int("visible",0)
            }
        }

        list_them.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        list_them.setHasFixedSize(true)

        Slot(context,"list_them__load").onRun {
            //одна строка - тема
            //добыаем сначала стандартные темы а потом сохранёные в памяти
            val data_res = ArrayList<String>()
            data_res.addAll(resources.getStringArray(R.array.list_theme))
            //и посмотрим есть ли в памяти чо если есть добавим
            val f = File_function()
            val data_mamory = f.readArrayList(Main.F_THEM_list)
            if(data_mamory.size>0){
                for (s in data_mamory.iterator()){
                    if(s.length>7){
                        data_res.add(s)
                    }

                }
            }


            //состовляем список обьектов тем из добытых ранее данных
            val data=ArrayList<Theme>()

            for(i in data_res.indices){
                val tl = data_res[i].split("$")
                if(tl.size>5){
                    data.add(Theme(tl[0],tl[1].toInt(),tl[2].toInt(),tl[3].toInt(),tl[4].toInt(),tl[5].toInt()))
                }
            }



            val adapter_list_theme = Adapter_list_theme(data)
            list_them.adapter = adapter_list_theme

            list_them.smoothScrollToPosition( list_them.adapter!!.itemCount)
        }



       //Будем слушать когда тему тыкнут
        Slot(context,"pererisovka").onRun {
            pererisovka_color()
        }

        //загрузим спсок тем
        signal("list_them__load").send(context)

        ///устанавливаем цвет и шрифт
        pererisovka_color(false)
    }


    class Adapter_list_theme(val data: ArrayList<Theme>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_list_theme.ViewHolder>() {

        private lateinit var context: Context

        class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
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
                Theme("",COLOR_FON,COLOR_ITEM,COLOR_TEXT,COLOR_TEXTcontext, COLOR_SELEKT)
            }

            p0.name_theme.text = theme.name
            p0.name_theme.textColor = theme.text
            p0.name_theme.backgroundColor = theme.fon

            //при клике будем устанавливать тему
            p0.name_theme.onClick {
                //обновляем константы
                COLOR_FON=theme.fon
                COLOR_TEXT = theme.text
                COLOR_ITEM = theme.item
                COLOR_TEXTcontext = theme.text_context
                COLOR_SELEKT = theme.color_selekt
                // сохраняем в память
                save_value_int("color_fon", COLOR_FON)
                save_value_int("color_post1", COLOR_ITEM)
                save_value_int("color_text", COLOR_TEXT)
                save_value_int("color_textcontext", COLOR_TEXTcontext)
                save_value_int("color_selekt", COLOR_SELEKT)
                //сохраним в память выбраную тему имя
                Main.save_value(Main.F_THEM_list,theme.name)
                // перерисовываем
                signal("pererisovka").send(context)
            }
            //при долгом нажатии предлагать её переименовать , удалить, или отправить
            p0.name_theme.onLongClick {
                p0.name_theme.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                //на сандартных темах окно не будем показывать
                if(p1 >Main.SIZE_LIST_THEM_DEFALT){
                    //общее окошко с кнопками удалить,переименовать
                    val empid = DialogWindow(context, R.layout.edit_my_plalist_item_dialog)

                    //скроем кнопки плей и поделится
                    (empid.view().findViewById<LinearLayout>(R.id.liner_btn_open_share)).visibility = View.GONE
                    (empid.view().findViewById<Button>(R.id.open_aimp_my_list_one)).visibility = View.GONE

                    //кнопка удалить
                    //------------------------------------------------------------------------------
                    (empid.view().findViewById<Button>(R.id.del)).onClick {
                        empid.close()
                        //получим весь список , удалим нужный и перезапишем
                        val f = File_function()
                        val list = f.readArrayList(Main.F_THEM_list)
                        list.remove(theme.name+"$"+theme.fon+"$"+theme.item+"$"+theme.text+"$"+theme.text_context+"$"+theme.color_selekt)
                        //когда все запишется пошлём сигнал чтобы список обновился
                        Slot(context, "File_created_save_vse", false).onRun {
                            //получим данные
                            val s = it.getStringExtra("update")
                            when (s) {
                                //пошлём сигнал пусть мой плейлист обновится
                                "zaebis" -> signal("list_them__load").send(context)
                                "pizdec" -> {
                                    context.toast(context.getString(R.string.error))
                                    //запросим разрешения
                                    Main.EbuchieRazreshenia()
                                }
                            }
                        }
                        f.saveArrayList(Main.F_THEM_list,list)
                    }

                    //кнопка переименовать
                    (empid.view().findViewById<Button>(R.id.reneme)).onClick {
                        //закрываем основное окошко
                        empid.close()

                        //откроем меню ввода имени
                        var name_them = theme.name
                        //покажем оконо в котором нужно будет ввести имя
                        val nsf = DialogWindow(context, R.layout.name_save_file)

                        val name = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                        name.setText(name_them)
                        name.typeface = face
                        name.textColor = COLOR_TEXT

                        (nsf.view().findViewById<Button>(R.id.button_save)).onClick {

                            if (name.text.toString().isEmpty()) {
                                //пока покажем это потом будум генерерить свои если не захотят вводить
                                context.toast("Введите имя")
                            } else {
                                name_them = name.text.toString()

                                //получим весь список , переименуем нужный и перезапишем
                                val f = File_function()
                                val list = f.readArrayList(Main.F_THEM_list)
                                list[p1-Main.SIZE_LIST_THEM_DEFALT-1] = name_them+"$"+theme.fon+"$"+theme.item+"$"+theme.text+"$"+theme.text_context+"$"+theme.color_selekt
                                //когда все запишется пошлём сигнал чтобы список обновился
                                Slot(context, "File_created_save_vse", false).onRun {
                                    //получим данные
                                    when (it.getStringExtra("update")) {
                                        //пошлём сигнал пусть мой плейлист обновится
                                        "zaebis" -> signal("list_them__load").send(context)
                                        "pizdec" -> {
                                            context.toast(context.getString(R.string.error))
                                            //запросим разрешения
                                            Main.EbuchieRazreshenia()
                                        }
                                    }
                                }
                                f.saveArrayList(Main.F_THEM_list,list)
                                //закроем окошко
                                nsf.close()
                            }
                        }
                    }
                }else{
                    context.toast("Нельзя редактировать")
                }

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
        button_edit_color_selekt.backgroundColor = COLOR_FON
        button_edit_color_selekt.textColor = COLOR_SELEKT
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
            5 -> {
                save_value_int("color_selekt", color)
                COLOR_SELEKT = color
                pererisovka_color()
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}

}
