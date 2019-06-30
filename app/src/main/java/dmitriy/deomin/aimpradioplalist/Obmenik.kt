package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.obmenik.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class Obmenik : Activity() {

    lateinit var ao: Adapter_obmenik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.obmenik)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val context: Context = this

        val find = findViewById<EditText>(R.id.editText_find_obmennik)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

        val recikl_list = list_obmenik
        recikl_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        //полоса быстрой прокрутки
        val fastScroller = fast_scroller_obmenik
        //получим текущие пораметры
        val paramL = fastScroller.layoutParams
        //меняем ширину
        paramL.width = Main.SIZE_WIDCH_SCROLL
        //устанавливаем
        fastScroller.layoutParams = paramL
        fastScroller.setRecyclerView(recikl_list)
        recikl_list.setOnScrollListener(fastScroller.onScrollListener)

        val d = ArrayList<Radio>()

        Slot(context, "Obmennik").onRun { it ->
            //получим данные
            when (it.getStringExtra("update")) {

                "zaebis" -> {
                    //включим анимацию
                    load_anim(true)
                    //очистим список если был
                    d.clear()

                    //-----------------получаем список из базы------------------------------------
                    val db = FirebaseFirestore.getInstance()
                    db.collection("radio_obmenik")
                            .get()
                            .addOnSuccessListener { result ->

                                for (document in result) {
                                    //  Log.d(TAG, "${document.id} => ${document.data}")
                                    d.add(Radio(document.data["name"] as String, "", "", document.data["url"] as String))
                                }

                                ao = Adapter_obmenik(d)
                                recikl_list.adapter = ao

                                //скроем или покажем полосу прокрутки и поиск
                                if (d.size > Main.SIZE_LIST_LINE) {
                                    fastScroller.visibility = View.VISIBLE

                                    find.visibility = View.VISIBLE
                                    // текст только что изменили в строке поиска
                                    find.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable) {}
                                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                                            ao.filter.filter(text)
                                        }
                                    })
                                } else {
                                    fastScroller.visibility = View.GONE

                                    find.setText("")
                                    find.visibility = View.GONE
                                }

                                //Напишем на кнопке меню текуию инфу по базе
                                button_menu_obmenik.text = "Всего в базе:" + d.size.toString() + " станций"
                                //выключаем анимацию загрузки
                                load_anim(false)

                            }
                            .addOnFailureListener { exception ->
                                //   Log.w(TAG, "Error getting documents.", exception)
                            }
                    //-----------------------------------------------------------------------------
                }

            }
        }

        button_menu_obmenik.onClick {
            val menu_ob = DialogWindow(context, R.layout.menu_obmenika)
            val user_dannie = (menu_ob.view().findViewById<LinearLayout>(R.id.liner_user_data))
            (menu_ob.view().findViewById<Button>(R.id.visible_user_data)).onClick {
                if (user_dannie.visibility == View.VISIBLE) {
                    user_dannie.visibility = View.GONE
                } else {
                    user_dannie.visibility = View.VISIBLE
                }
            }
            val ed_name = menu_ob.view().findViewById<EditText>(R.id.editText_name_new)
            val ed_url = menu_ob.view().findViewById<EditText>(R.id.editText_url_new)

            (menu_ob.view().findViewById<Button>(R.id.button_paste_iz_bufera_obmenik)).onClick {
              ed_url.setText(Main.getText(context))
            }

            (menu_ob.view().findViewById<Button>(R.id.button_add)).onClick {

                if (ed_name.text.toString().length < 2 || ed_url.text.toString().length < 2) {
                    context.toast("Введите данные")
                } else {

                    //добавление в базу
                    val db = FirebaseFirestore.getInstance()
                    val user = hashMapOf(
                            "name" to ed_name.text.toString(),
                            "url" to ed_url.text.toString()
                    )

                    // Add a new document with a generated ID
                    db.collection("radio_obmenik")
                            .add(user)
                            .addOnSuccessListener { documentReference ->
                                //  Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                menu_ob.close()
                                //если все пучком пошлём сигнал для обновления(пока всего плейлиста)
                                signal("Obmennik").putExtra("update", "zaebis").send(context)
                            }
                            .addOnFailureListener { e ->
                                context.toast(e.toString())
                            }
                }
            }


        }

        //пошлём сигнал для загрузки дааных п спискок
        signal("Obmennik").putExtra("update", "zaebis").send(context)

    }

    fun load_anim(b: Boolean) {
        if (b) {
            button_menu_obmenik.visibility = View.GONE
            progressBar_load_obmenik.visibility = View.VISIBLE
        } else {
            progressBar_load_obmenik.visibility = View.GONE
            button_menu_obmenik.visibility = View.VISIBLE
        }
    }
}
