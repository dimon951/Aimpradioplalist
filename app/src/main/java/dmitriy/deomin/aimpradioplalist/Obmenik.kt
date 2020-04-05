package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.`fun`.windows.window_add_new_url_obmenik
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_obmenik
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.android.synthetic.main.obmenik.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

class Obmenik : Activity() {

    lateinit var ao: Adapter_obmenik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.obmenik)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val context: Context = this

        fon_obmenik.backgroundColor = Main.COLOR_FON

        val find = findViewById<EditText>(R.id.editText_find_obmennik)
        find.typeface = Main.face
        find.textColor = Main.COLOR_TEXT
        find.hintTextColor = Main.COLOR_TEXTcontext

        button_close_list_obmenik.onClick { finish() }

        val recikl_list = list_obmenik
        recikl_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this) as RecyclerView.LayoutManager?

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

        //размер текста
        button_menu_obmenik.textSize=Main.SIZE_TEXT_ONLINE_BUTTON
        button_close_list_obmenik.textSize=Main.SIZE_TEXT_ONLINE_BUTTON

        val d = ArrayList<Radio>()

        Slot(context, "Obmennik").onRun { it ->
            //получим данные
            when (it.getStringExtra("update")) {

                "zaebis" -> {

                    //напишем на кнопке что происходит
                    button_menu_obmenik.text = "Загрузка.."
                    //включим анимацию
                    load_anim(true)

                    //очистим список если был
                    d.clear()

                    //-----------------получаем список из базы------------------------------------
                    val db = FirebaseFirestore.getInstance()
                    db.collection("radio_obmenik")
                            .orderBy("date")
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    d.add(Radio(
                                            (if (document.data["name"] != null) {
                                                document.data["name"].toString()
                                            } else {
                                                ""
                                            }),
                                            (if (document.data["kat"] != null) {
                                                document.data["kat"].toString()
                                            } else {
                                                ""
                                            }),
                                            (if (document.data["kbps"] != null) {
                                                document.data["kbps"].toString()
                                            } else {
                                                ""
                                            }),
                                            (if (document.data["url"] != null) {
                                                document.data["url"].toString()
                                            } else {
                                                ""
                                            }),
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
                                            (document.id))
                                    )
                                }

                                //перевернём список
                                //d.reverse()

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

        button_add_new_obmenik.onClick {
            window_add_new_url_obmenik(context)
        }

        button_update_all_obmenik.onClick {
            //пошлём сигнал для загрузки дааных п спискок
            signal("Obmennik").putExtra("update", "zaebis").send(context)
        }

        //пошлём сигнал для загрузки дааных п спискок
        signal("Obmennik").putExtra("update", "zaebis").send(context)

    }

    fun load_anim(b: Boolean) {
        if (b) {
            progressBar_load_obmenik.visibility = View.VISIBLE
        } else {
            progressBar_load_obmenik.visibility = View.GONE
        }
    }
}
