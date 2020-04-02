package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.data_time.data_time
import dmitriy.deomin.aimpradioplalist.`fun`.getText_сlipboard
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

fun window_add_new_url_obmenik(context:Context){
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




    (menu_add_new.view().findViewById<Button>(R.id.button_paste_iz_bufera_obmenik)).onClick {
        ed_url.setText(getText_сlipboard(context))
    }

    (menu_add_new.view().findViewById<Button>(R.id.button_add)).onClick {

        if (ed_name.text.toString().isEmpty() || ed_url.text.toString().isEmpty()) {
            context.toast("Введите данные")
        } else {

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

            //добавление в базу
            val db = FirebaseFirestore.getInstance()
            val user = hashMapOf(
                    "date" to data_time(),
                    "user_name" to Main.NAME_USER,
                    "user_id" to Main.ID_USER,
                    "kat" to kategoria,
                    "kbps" to kbps,
                    "name" to ed_name.text.toString(),
                    "url" to ed_url.text.toString()
            )

            // Add a new document with a generated ID
            db.collection("radio_obmenik")
                    .add(user)
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