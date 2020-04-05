package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.load_text_size_CONSTANTS
import dmitriy.deomin.aimpradioplalist.`fun`.save_read_float
import dmitriy.deomin.aimpradioplalist.`fun`.save_value_float
import dmitriy.deomin.aimpradioplalist.custom.FloatingActionButton
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal


fun window_edit_size(content:String){

    val fabButton_pluss:FloatingActionButton = FloatingActionButton.Builder(Main.context as Activity)
            .withDrawable(Main.context.resources.getDrawable(dmitriy.deomin.aimpradioplalist.R.drawable.iconka_plus))
            .withButtonColor(Color.alpha(0))
            .withGravity(Gravity.BOTTOM or Gravity.RIGHT)
            .withMargins(0, 0, 1, 1)
            .create()
    fabButton_pluss.setOnClickListener {
        var s = save_read_float(content)
        s += 0.5F
        save_value_float(content,s)

        load_text_size_CONSTANTS()
        signal("Main_update").putExtra("signal","update_text_size").send(Main.context)
    }



    val fabButton_minus:FloatingActionButton = FloatingActionButton.Builder(Main.context as Activity)
            .withDrawable(Main.context.resources.getDrawable(dmitriy.deomin.aimpradioplalist.R.drawable.iconka_minus))
            .withButtonColor(Color.alpha(0))
            .withGravity(Gravity.BOTTOM or Gravity.LEFT)
            .withMargins(0, 0, 1, 1)
            .create()
    fabButton_minus.setOnClickListener {
        var s = save_read_float(content)
        s -= 0.5F
        save_value_float(content,s)

        load_text_size_CONSTANTS()
        signal("Main_update").putExtra("signal","update_text_size").send(Main.context)
    }



    val fabButton_gotovo:FloatingActionButton = FloatingActionButton.Builder(Main.context as Activity)
            .withDrawable(Main.context.resources.getDrawable(dmitriy.deomin.aimpradioplalist.R.drawable.iconka_gotovo))
            .withButtonColor(Color.alpha(0))
            .withGravity(Gravity.BOTTOM or Gravity.CENTER)
            .withMargins(0, 0, 1, 1)
            .create()
    fabButton_gotovo.setOnClickListener {
        fabButton_pluss.visibility = View.GONE
        fabButton_gotovo.visibility = View.GONE
        fabButton_minus.visibility = View.GONE

        setting_size_text_vidgets()
    }
}