package dmitriy.deomin.aimpradioplalist.`fun`

import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.Koment
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

//-----------------получаем список из базы------------------------------------
fun load_koment(id_item: String) {
    val d = ArrayList<Koment>()
    val db = FirebaseFirestore.getInstance()

    db.collection(id_item)
            .orderBy("date")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    d.add(Koment(
                            (if (document.data["user_name"] != null) {
                                document.data["user_name"].toString()
                            } else {
                                ""
                            }),
                            (if (document.data["user_id"] != null) {
                                document.data["user_id"].toString()
                            } else {
                                ""
                            }),
                            (if (document.data["text"] != null) {
                                document.data["text"].toString()
                            } else {
                                ""
                            }),
                            (if (document.data["date"] != null) {
                                document.data["date"].toString()
                            } else {
                                ""
                            }),
                            (document.id))
                    )
                }
                signal("load_koment")
                        .putExtra("data", d)
                        .putExtra("id", id_item)
                        .send(Main.context)

            }
}
//-----------------------------------------------------------------------------------


//            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//            val currentDate = sdf.format(Date())
fun add_koment(id_item: String, text: String) {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
    val currentDate = calendar.getTime()

    //добавление в базу
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
            "user_name" to Main.NAME_USER,
            "user_id" to Main.ID_USER,
            "text" to text,
            "date" to currentDate.toString()
    )

    // Add a new document with a generated ID
    db.collection(id_item)
            .add(user)
            .addOnSuccessListener {
                //если все пучком пошлём сигнал для обновления(пока всего плейлиста)
                signal("add_koment").putExtra("update", "zaebis").send(Main.context)
            }
            .addOnFailureListener { e ->
                Main.context.toast(e.toString())
            }
}

fun edit_koment(id_item: String, text: String, id_komenta: String) {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
    val currentDate = calendar.getTime()
    //добавление в базу
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
            "user_name" to Main.NAME_USER,
            "user_id" to Main.ID_USER,
            "text" to text,
            "date" to currentDate
    )

    // Add a new document with a generated ID
    db.collection(id_item).document(id_komenta).set(user)
            .addOnSuccessListener { documentReference ->
                //если все пучком пошлём сигнал для обновления(пока всего плейлиста)
                signal("edit_koment").putExtra("update", "zaebis").send(Main.context)
            }
            .addOnFailureListener { e ->
                Main.context.toast(e.toString())
            }
}
//-----------------------------------------------------------------------