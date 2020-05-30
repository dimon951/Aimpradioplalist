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
import dmitriy.deomin.aimpradioplalist.`fun`.data_time.Data_time_milis_to_good_format
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
        if (Main.FULLSCRIN > 0) {
            //во весь экран
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if(Main.NAVBUTTON >0){
            //скрывем кнопки навигации
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
            //будем слушать  если покажется опять - закроем
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) decorView.systemUiVisibility =
                        uiOptions
            }
            //-----------------------------------------------------------------------------
        }

        val context: Context = this

        fon_obmenik.backgroundColor = Main.COLOR_FON

        val find = findViewById<EditText>(R.id.editText_find_obmennik)

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
        button_menu_obmenik.textSize = Main.SIZE_TEXT_ONLINE_BUTTON
        button_close_list_obmenik.textSize = Main.SIZE_TEXT_ONLINE_BUTTON

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

                                    val name = if (document.data["name"] != null) document.data["name"].toString() else ""
                                    val kat = if (document.data["kat"] != null) document.data["kat"].toString() else ""
                                    val kbps = if (document.data["kbps"] != null) document.data["kbps"].toString() else ""
                                    val url = if (document.data["url"] != null) document.data["url"].toString() else ""
                                    var data = if (document.data["date"] != null) document.data["date"].toString() else ""

                                    //переведём дату в норм вид
                                    data = if(data.contains("/")||data.contains(":")||data.contains(" ")) data
                                    else Data_time_milis_to_good_format(data.toLong())

                                    val user_name = if (document.data["user_name"] != null)
                                        document.data["user_name"].toString()+"\n"+data
                                    else "\n"+data

                                    val user_id = if (document.data["user_id"] != null) document.data["user_id"].toString() else ""

                                    d.add(Radio(name, kat, kbps, url, user_name, user_id, (document.id)))
                                }

                                //перевернём список
                                d.reverse()

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
