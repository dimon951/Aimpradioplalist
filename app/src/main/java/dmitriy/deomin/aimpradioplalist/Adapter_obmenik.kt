package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.obmenik.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Adapter_obmenik(val data: ArrayList<Radio>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_obmenik.ViewHolder>(), Filterable {

    private lateinit var context: Context
    var raduoSearchList: ArrayList<Radio> = data


    override fun getFilter(): Filter {


        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    this@Adapter_obmenik.raduoSearchList = data
                } else {
                    val filteredList = ArrayList<Radio>()
                    for (row in data) {
                        if (row.name.replace("<List>", "").toLowerCase().contains(charString.toLowerCase())
                                || row.url.toLowerCase().contains(charString.toLowerCase())
                                || row.kbps.toLowerCase().contains(charString.toLowerCase())
                                || row.kategory.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    this@Adapter_obmenik.raduoSearchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = this@Adapter_obmenik.raduoSearchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                if (filterResults.values != null) {
                    this@Adapter_obmenik.raduoSearchList = filterResults.values as ArrayList<Radio>
                    notifyDataSetChanged()
                }
            }
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.user_name_info)
        val fon = itemView.findViewById<androidx.cardview.widget.CardView>(R.id.fon_item_radio)
        val kbps = itemView.findViewById<TextView>(R.id.kbps_radio)
        val ganr = itemView.findViewById<TextView>(R.id.ganr_radio)
        val liner_kbps = itemView.findViewById<LinearLayout>(R.id.liner_kbps)
        val liner_ganr = itemView.findViewById<LinearLayout>(R.id.liner_ganr)
        val liner_url = itemView.findViewById<LinearLayout>(R.id.liner_url)

        //------------------------------------------------------------------------------------
        // коментарии ,лайки, инфо
        val liner_user = itemView.findViewById<LinearLayout>(R.id.liner_user_add_info)
        val user_name = itemView.findViewById<TextView>(R.id.user_name)
        //
        val liner_reiting = itemView.findViewById<LinearLayout>(R.id.liner_reiting)
        val btn_koment = itemView.findViewById<Button>(R.id.button_komenty)
        val btn_dislike = itemView.findViewById<Button>(R.id.button_dislake)
        val btn_like = itemView.findViewById<Button>(R.id.button_like)
        //
        val liner_text_komentov = itemView.findViewById<LinearLayout>(R.id.liner_text_komentov)
        val btn_add_koment = itemView.findViewById<Button>(R.id.btn_add_new_koment)
        val btn_update_koment = itemView.findViewById<Button>(R.id.button_updete_obmenik)
        val text_komentov = itemView.findViewById<TextView>(R.id.text_komentov)
        //-----------------------------------------------------------------------------------

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return this.raduoSearchList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


        //заполним данными(тут в логах бывает падает - обращение к несуществующему элементу)
        //поэтому будем проверять чтобы общее количество было больше текушего номера
        val radio: Radio = if (this.raduoSearchList.size > p1) {
            this.raduoSearchList[p1]
        } else {
            //иначе вернём пустой элемент(дальше будут проверки и он не отобразится)
            Radio("", "", "", "")
        }

        //из названия будем удалять тип ссылки
        val name = radio.name.replace("<List>", "")
        p0.name_radio.text = name


        if (radio.url.isNotEmpty()) {
            p0.liner_url.visibility = View.VISIBLE
            p0.url_radio.text = radio.url
        } else {
            p0.liner_url.visibility = View.GONE
        }

        //нумерация списка
        if (Vse_radio.Numeracia == 1) {
            p0.nomer_radio.text = (p1 + 1).toString() + ". "
        } else {
            p0.nomer_radio.text = ""
        }
        //kbps
        if (radio.kbps.isNotEmpty()) {
            p0.liner_kbps.visibility = View.VISIBLE
            p0.kbps.text = radio.kbps
        } else {
            p0.liner_kbps.visibility = View.GONE
        }
        //ganr
        if (radio.kategory.isNotEmpty()) {
            p0.liner_ganr.visibility = View.VISIBLE
            p0.ganr.text = radio.kategory
        } else {
            p0.liner_ganr.visibility = View.GONE
        }
        //имя кто добавил ссылку
        if (radio.user_name.isNotEmpty()) {
            p0.liner_user.visibility = View.VISIBLE
            p0.user_name.text = radio.user_name
        } else {
            p0.liner_user.visibility = View.GONE
        }

        //-----------коментарии и лайки-----------------------------------------
        //покажем понель пока коментарии откроем
        val id = radio.id
        p0.liner_reiting.visibility = View.VISIBLE
        p0.btn_dislike.visibility = View.GONE
        p0.btn_like.visibility = View.GONE

        p0.text_komentov.setTextIsSelectable(true)

        Slot(context, "load_koment").onRun {
            if (it.getStringExtra("id") == id) {
                val data = it.getParcelableArrayListExtra<Koment>("data")
                p0.btn_koment.text = "Коментарии: " + (if (data.size > 0) {
                    data.size
                } else {
                    0
                })
                //обнулим количество коментов и заново запишем
                p0.text_komentov.text = ""
                var t =""
                for(kom in data.iterator()){
                     t= t+ "\n"+ (if(kom.user_name.isEmpty()){"no_name"}else{kom.user_name})+ ": "+kom.text
                }
                p0.text_komentov.text = t.drop(1)

            }
        }

        p0.btn_koment.onClick {
            if(p0.liner_text_komentov.visibility==View.GONE){
                p0.liner_text_komentov.visibility = View.VISIBLE
            }else{
                p0.liner_text_komentov.visibility =View.GONE
            }
        }
        p0.btn_add_koment.onClick {
            //добавление коментариев
            //-------------------------------------------------------------------------------
            val add_kom = DialogWindow(context, R.layout.add_koment)
            val ed = add_kom.view().findViewById<EditText>(R.id.ed_add_kom)
            ed.typeface = Main.face
            ed.textColor = Main.COLOR_TEXT
            ed.hintTextColor = Main.COLOR_TEXTcontext
            add_kom.view().findViewById<Button>(R.id.btn_ad_kom).onClick {

                if(ed.text.toString().isEmpty()){
                    context.toast("введите текст")
                }else {
                    Slot(context,"add_koment").onRun {
                        if(it.getStringExtra("update")=="zaebis"){
                            add_kom.close()
                            Main.load_koment(id)
                        }else{
                            context.toast("ошибка")
                        }
                    }
                    Main.add_koment(radio.id,ed.text.toString())
                }
            }
            //-------------------------------------------------------------------------------------

        }
        p0.btn_update_koment.onClick {
            //обновить текуший список коментов
            Main.load_koment(id)
        }

        //Загрузим в начале просто количество коментов
        Main.load_koment(id)
        //------------------------------------------------------------------------

        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            val mvr = DialogWindow(context, R.layout.menu_vse_radio)

            val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
            val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
            val share = mvr.view().findViewById<Button>(R.id.button_cshre)
            val instal_aimp = mvr.view().findViewById<Button>(R.id.button_instal_aimp)
            val instal_aimp2 = mvr.view().findViewById<Button>(R.id.button_download_yandex_aimp)
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
                    ed_url.setText(Main.getText(context))
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


            //если aimp установлен скроем кнопку установить аимп
            if (Main.install_app("com.aimp.player")) {
                instal_aimp.visibility = View.GONE
                instal_aimp2.visibility = View.GONE
                open_aimp.visibility = View.VISIBLE
            } else {
                //если есть магазин покажем и установку через него
                if (Main.install_app("com.google.android.gms")) {
                    instal_aimp.visibility = View.VISIBLE
                } else {
                    instal_aimp.visibility = View.GONE
                }

                //скачать по ссылке будем показывать всегда
                instal_aimp2.visibility = View.VISIBLE
                open_aimp.visibility = View.GONE

            }

            //Имя и урл выбраной станции , при клике будем копировать урл в буфер
            val text_name_i_url = mvr.view().findViewById<TextView>(R.id.textView_vse_radio)
            text_name_i_url.text = name + "\n" + radio.url
            text_name_i_url.onClick {
                text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                Main.putText(radio.url, context)
                context.toast("url скопирован в буфер")
            }


            open_aimp.onLongClick {
                Main.play_system(name, radio.url)
            }

            instal_aimp.onClick {
                context.browse("market://details?id=com.aimp.player")
            }

            instal_aimp2.onClick {
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            add_pls.onClick {
                Main.add_myplalist(name, radio.url)
                mvr.close()
            }

            share.onClick {
                //сосавим строчку как в m3u вайле
                context.share(name + "\n" + radio.url)
            }
            share.onLongClick {
                context.email("deomindmitriy@gmail.com", "aimp_radio_plalist", name + "\n" + radio.url)
            }

            open_aimp.onClick {
                Main.play_aimp(name, radio.url)
                mvr.close()
            }
        }


    }
}
