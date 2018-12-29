package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.*
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import org.jetbrains.anko.sdk27.coroutines.onClick



class Setting : FragmentActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var DIALOG_ID: Int = 0
    private lateinit var edit_fon: Button
    private lateinit var edit_pos1: Button
    private lateinit var edit_text_color: Button
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
        linerfon = findViewById(R.id.fon_setting)

        //устанавливаем шрифт
        edit_fon.typeface = Main.face
        edit_pos1.typeface = Main.face
        edit_text_color.typeface = Main.face

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


        ///устанавливаем цвет и шрифт и сохраняется заодно
        pererisovka_color()

    }
    fun pererisovka_color() {
        Main.liner_boss.setBackgroundColor(Main.COLOR_FON)

        linerfon.setBackgroundColor(Main.COLOR_FON)
        edit_fon.setTextColor(Main.COLOR_TEXT)
        edit_fon.setBackgroundColor(Main.COLOR_FON)
        edit_pos1.setTextColor(Main.COLOR_TEXT)
        edit_pos1.setBackgroundColor(Main.COLOR_ITEM)
        edit_text_color.setTextColor(Main.COLOR_TEXT)
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
