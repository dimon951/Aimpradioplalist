package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import dmitriy.deomin.aimpradioplalist.*
import dmitriy.deomin.aimpradioplalist.Main.Companion.context
import dmitriy.deomin.aimpradioplalist.`fun`.save_value
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun menu_main(view: View) {
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

    val menu = DialogWindow(context, R.layout.menu_progi)

    menu.view().findViewById<Button>(R.id.button_abaut).onClick {
        context.startActivity<Abaut>()
        menu.close()
    }

    menu.view().findViewById<Button>(R.id.button_setting).onClick {
        context.startActivity<Setting>()
        menu.close()
    }

    menu.view().findViewById<Button>(R.id.button_edit_fonts).onClick {
        context.startActivity<Fonts_vibor>()
        menu.close()
    }

    menu.view().findViewById<Button>(R.id.akkaunt).onClick {
        menu.close()

        val menu_ob = DialogWindow(context, R.layout.akaunt)

        menu_ob.view().findViewById<EditText>(R.id.fon_obmenik).backgroundColor = Main.COLOR_FON

        //имя и ид пользователя, нужны будут для удаления своих ссылок и оображении кто добавил
        val ed_id_user = menu_ob.view().findViewById<EditText>(R.id.editText_id)
        val ed_name_user = menu_ob.view().findViewById<EditText>(R.id.editText_name)

        ed_id_user.typeface = Main.face
        ed_id_user.textColor = Main.COLOR_TEXT
        ed_id_user.hintTextColor = Main.COLOR_TEXTcontext

        ed_name_user.typeface = Main.face
        ed_name_user.textColor = Main.COLOR_TEXT
        ed_name_user.hintTextColor = Main.COLOR_TEXTcontext

        ed_id_user.setText(Main.ID_USER)
        ed_name_user.setText(Main.NAME_USER)

        (menu_ob.view().findViewById<Button>(R.id.button_save_user_dannie)).onClick {

            if (ed_name_user.text.toString() != Main.NAME_USER) {
                //на имя пофиг пусть ставят любое
                Main.NAME_USER = ed_name_user.text.toString()
                save_value("name_user", Main.NAME_USER)
                context.toast("Имя изменено на:" + Main.NAME_USER)
            }

            //id важно будем спрашивать и проверяь
            if (ed_id_user.text.toString() != Main.ID_USER) {
                if (ed_id_user.text.toString().length < 4) {
                    context.toast("Id должен быть не меньше 4-х символов")
                } else {
                    context.alert("При смене Id Вы потеряете ранее добавленнные ссылки", "Внимание") {
                        yesButton {
                            Main.ID_USER = ed_id_user.text.toString()
                            save_value("id_user", Main.ID_USER)
                            menu_ob.close()
                            context.toast("Готово, пароль изменён")
                        }
                        noButton {}
                    }.show()
                }
            } else {
                menu_ob.close()
            }
        }


    }
}