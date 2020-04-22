package dmitriy.deomin.aimpradioplalist.`fun`.menu

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.Play_audio
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.file.is_existence_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.text.SimpleDateFormat
import java.util.*

fun menu_vse_radio_obmenik(context: Context, radio: Radio,name:String){

    //проврим существование файла
    val file_pach = Main.ROOT + radio.name + "." + radio.url.substringAfterLast('.')
    val file_save= is_existence_file(file_pach)

    val mvr = DialogWindow(context, R.layout.menu_vse_radio)

    val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
    val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
    val open_custom = mvr.view().findViewById<Button>(R.id.open_custom_plaer)
    val share = mvr.view().findViewById<Button>(R.id.button_cshre)
    //
    val liner_admin = mvr.view().findViewById<LinearLayout>(R.id.liner_admin)
    val btn_del = mvr.view().findViewById<Button>(R.id.button_delete_admin)
    val btn_edit = mvr.view().findViewById<Button>(R.id.button_edit_admin)

    //если эту ссылку добавлял пользователь покажем понель редактирования
    if (radio.id_user == Main.ID_USER) {
        liner_admin.visibility = View.VISIBLE
    } else {
        liner_admin.visibility = View.GONE
    }

    btn_del.onClick {
        context.alert("Удалить ссылку?", "Внимание") {
            yesButton {
                val db = FirebaseFirestore.getInstance()
                db.collection("radio_obmenik").document(radio.id).delete()
                //пошлём сигнал для загрузки дааных п спискок
                signal("Obmennik").putExtra("update", "zaebis").send(context)
                mvr.close()
            }
            noButton {}
        }.show()
    }

    //для кнопки редактирования мы откроем форму добавления новой ссылки с подставлеными данными
    //при сохраниении удалим старую и запишем новую
    btn_edit.onClick {

        mvr.close()

        val menu_add_new = DialogWindow(context, R.layout.add_new_url_obmenik)

        val ed_name = menu_add_new.view().findViewById<EditText>(R.id.editText_name_new)
        val ed_url = menu_add_new.view().findViewById<EditText>(R.id.editText_url_new)
        val ed_kat = menu_add_new.view().findViewById<EditText>(R.id.editText_kategoria_new)
        val ed_kbps = menu_add_new.view().findViewById<EditText>(R.id.editText_kbps_new)

        ed_name.typeface = Main.face
        ed_name.textColor = Main.COLOR_TEXT
        ed_name.hintTextColor = Main.COLOR_TEXTcontext

        ed_url.typeface = Main.face
        ed_url.textColor = Main.COLOR_TEXT
        ed_url.hintTextColor = Main.COLOR_TEXTcontext

        ed_kat.typeface = Main.face
        ed_kat.textColor = Main.COLOR_TEXT
        ed_kat.hintTextColor = Main.COLOR_TEXTcontext

        ed_kbps.typeface = Main.face
        ed_kbps.textColor = Main.COLOR_TEXT
        ed_kbps.hintTextColor = Main.COLOR_TEXTcontext


        ed_name.setText(radio.name)
        ed_url.setText(radio.url)
        ed_kbps.setText(radio.kbps)
        ed_kat.setText(radio.kategory)


        (menu_add_new.view().findViewById<Button>(R.id.button_paste_iz_bufera_obmenik)).onClick {
            ed_url.setText(getText_сlipboard(context))
        }

        (menu_add_new.view().findViewById<Button>(R.id.button_add)).onClick {

            if (ed_name.text.toString().isEmpty() || ed_url.text.toString().isEmpty()) {
                context.toast("Введите данные")
            } else {
                //удаляем старую
                val db = FirebaseFirestore.getInstance()
                db.collection("radio_obmenik").document(radio.id).delete()

                //добавляем исправленый вариант
                var kategoria = ""
                if (ed_kat.text.isNotEmpty()) {
                    kategoria = ed_kat.text.toString()
                }
                var kbps = ""
                if (ed_kbps.text.isNotEmpty()) {
                    kbps = ed_kbps.text.toString()
                    if (!kbps.contains("kbps")) {
                        kbps += "kbps"
                    }
                }

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())


                //добавление в базу
                val user = hashMapOf(
                        "date" to currentDate,
                        "user_name" to Main.NAME_USER,
                        "user_id" to Main.ID_USER,
                        "kat" to kategoria,
                        "kbps" to kbps,
                        "name" to ed_name.text.toString(),
                        "url" to ed_url.text.toString()
                )

                db.collection("radio_obmenik").document(radio.id).set(user)
                        // Add a new document with a generated ID
                        .addOnSuccessListener { documentReference ->
                            //  Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            menu_add_new.close()
                            //если все пучком пошлём сигнал для обновления(пока всего плейлиста)
                            signal("Obmennik").putExtra("update", "zaebis").send(context)
                        }
                        .addOnFailureListener { e ->
                            context.toast(e.toString())
                        }
            }
        }
    }

    //Имя и урл выбраной станции , при клике будем копировать урл в буфер
    val text_name_i_url = mvr.view().findViewById<TextView>(R.id.textView_vse_radio)
    text_name_i_url.text = name + "\n" + radio.url
    text_name_i_url.onClick {
        text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
        putText_сlipboard(radio.url, context)
        context.toast("Url скопирован в буфер")
        mvr.close()
    }

    open_custom.onClick {
        if(isValidURL(radio.url)){
            Play_audio(radio.name, radio.url, context = context)
        }else{
            context.toast("Возможно ссылка битая, нельзя открыть")
        }
        mvr.close()
    }

    open_aimp.onLongClick {
        play_system(name, radio.url)
        mvr.close()
    }

    add_pls.onClick {
        add_myplalist(name, radio.url)
        mvr.close()
    }

    share.onClick {
        //сосавим строчку как в m3u вайле
        share_text(name + "\n" + radio.url)
    }
    share.onLongClick {
        send_email("deomindmitriy@gmail.com",  name + "\n" + radio.url)
    }

    open_aimp.onClick {
        play_aimp(name, radio.url)
        mvr.close()
    }
}