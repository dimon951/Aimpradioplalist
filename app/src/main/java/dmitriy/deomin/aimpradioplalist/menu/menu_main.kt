package dmitriy.deomin.aimpradioplalist.menu

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import dmitriy.deomin.aimpradioplalist.Main.Companion.context
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.Setting_color
import dmitriy.deomin.aimpradioplalist.`fun`.windows.abaut
import dmitriy.deomin.aimpradioplalist.`fun`.windows.akkaunt
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

fun menu_main(view: View) {
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

    val menu = DialogWindow(context, R.layout.menu_progi)

    menu.view().findViewById<Button>(R.id.button_abaut).onClick {
        abaut()
        menu.close()
    }

    menu.view().findViewById<Button>(R.id.button_setting).onClick {
        context.startActivity<Setting_color>()
        menu.close()
    }

    menu.view().findViewById<Button>(R.id.akkaunt).onClick {
        menu.close()
        akkaunt()
    }
    menu.view().findViewById<Button>(R.id.button_other_settings).onClick {
        other_settings()
        menu.close()
    }

}