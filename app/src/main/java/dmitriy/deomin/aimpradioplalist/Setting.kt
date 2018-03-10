package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.app.DialogFragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment


class Setting : Activity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    internal lateinit var edit_fon: Button
    internal lateinit var edit_pos1: Button
    internal lateinit var edit_text_color: Button

    internal lateinit var textView_edit_color_posty: TextView
    internal lateinit var textView_edit_fon_color: TextView
    internal lateinit var textView_edit_color_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

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

    fun edit_fonts(v: View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.myalpha)
        v.startAnimation(anim)
        val i = Intent(this, Fonts_vibor::class.java)
        startActivity(i)
    }
}
