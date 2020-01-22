package dmitriy.deomin.aimpradioplalist.`fun`

import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.Like
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.toast

//------------------лайки---------------------------------------------------
fun load_like(id_item: String) {
    val d = ArrayList<Like>()
    val db = FirebaseFirestore.getInstance()
    db.collection(id_item)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    d.add(Like(
                            (if (document.data["user_id"] != null) {
                                document.data["user_id"]
                            } else {
                                ""
                            }) as String,
                            (document.id),
                            (if (document.data["like"] != null) {
                                document.data["like"]
                            } else {
                                ""
                            }) as String))
                }
                signal("load_like")
                        .putExtra("data", d)
                        .putExtra("id", id_item)
                        .send(Main.context)

            }
}

fun like(id_item: String, like: String) {
    //добавление в базу
    val db = FirebaseFirestore.getInstance()
    val user = hashMapOf(
            "user_id" to Main.ID_USER,
            "item_id" to id_item,
            "like" to like
    )

    // Add a new document with a generated ID
    db.collection(id_item)
            .add(user)
            .addOnSuccessListener { documentReference ->
                //если все пучком пошлём сигнал для обновления(пока всего плейлиста)
                signal("add_like").putExtra("update", "zaebis").send(Main.context)
            }
            .addOnFailureListener { e ->
                Main.context.toast(e.toString())
            }
}
//----------------------------------------------------------------------------
