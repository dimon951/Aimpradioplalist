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
    lateinit var context: Context

    internal lateinit var textView_edit_color_posty: TextView
    internal lateinit var textView_edit_fon_color: TextView
    internal lateinit var textView_edit_color_text: TextView
    private lateinit var linerfon: LinearLayout

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
        edit_fon = findViewById<Button>(R.id.button_edit_fon_color)
        edit_pos1 = findViewById<Button>(R.id.button_edit_color_posty)
        edit_text_color = findViewById<Button>(R.id.button_edit_color_text)
        linerfon = findViewById<LinearLayout>(R.id.fon_setting)

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


        ///устанавливаем цвет и шрифт и сохраняется заодно
        pererisovka_color()

    }
    fun pererisovka_color() {
        Main.liner_boss.setBackgroundColor(Main.COLOR_FON)

        textView_edit_color_posty.setTextColor(Main.COLOR_TEXT)
        textView_edit_fon_color.setTextColor(Main.COLOR_TEXT)
        textView_edit_color_text.setTextColor(Main.COLOR_TEXT)
        linerfon.setBackgroundColor(Main.COLOR_FON)
        edit_fon.setTextColor(Main.COLOR_TEXT)
        edit_fon.setBackgroundColor(Main.COLOR_FON)
        edit_pos1.setTextColor(Main.COLOR_TEXT)
        edit_pos1.setBackgroundColor(Main.COLOR_ITEM)
        edit_text_color.setTextColor(Main.COLOR_TEXT)
        edit_text_color.setBackgroundColor(Main.COLOR_FON)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            0 -> {
                Main.save_value_int("color_fon", color)
                Main.COLOR_FON = color
                pererisovka_color()
            }

            1 -> {
                Main.save_value_int("color_post1", color)
                Main.COLOR_ITEM = color
                pererisovka_color()
            }

            3 -> {
                Main.save_value_int("color_text", color)
                Main.COLOR_TEXT = color
                pererisovka_color()
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}
}
