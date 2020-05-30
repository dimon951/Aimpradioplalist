package dmitriy.deomin.aimpradioplalist.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.Vse_radio
import dmitriy.deomin.aimpradioplalist.`fun`.load_koment
import dmitriy.deomin.aimpradioplalist.`fun`.windows.add_koment_window
import dmitriy.deomin.aimpradioplalist.menu.menu_vse_radio_obmenik
import dmitriy.deomin.aimpradioplalist.custom.Koment
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.Slot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick

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
        val btn_koment = itemView.findViewById<TextView>(R.id.button_komenty)

        //
        val liner_text_komentov = itemView.findViewById<LinearLayout>(R.id.liner_text_komentov)
        val btn_add_koment = itemView.findViewById<Button>(R.id.btn_add_new_koment)
        val btn_update_koment = itemView.findViewById<Button>(R.id.button_updete_obmenik)
        val text_komentov = itemView.findViewById<TextView>(R.id.text_komentov)
        //-----------------------------------------------------------------------------------

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_online_plalist, p0, false)
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

        GlobalScope.launch {
            Slot(context, "load_koment").onRun {
                if (it.getStringExtra("id") == id) {
                    val data = it.getParcelableArrayListExtra<Koment>("data")
                    if (data != null) {
                        p0.btn_koment.text = "Коментарии: " + (if (data.size > 0) {
                            data.size
                        } else {
                            0
                        })
                    }
                    //обнулим количество коментов и заново запишем
                    p0.text_komentov.text = ""
                    var t = ""
                    if (data != null) {
                        for (kom in data.iterator()) {
                            t = t + "\n" + (if (kom.user_name.isEmpty()) {
                                "no_name"
                            } else {
                                kom.user_name
                            }) + ": " + kom.text
                        }
                    }
                    p0.text_komentov.text = t
                }
            }
        }


        p0.btn_koment.onClick {
            if (p0.liner_text_komentov.visibility == View.GONE) {
                p0.liner_text_komentov.visibility = View.VISIBLE
            } else {
                p0.liner_text_komentov.visibility = View.GONE
            }
        }
        p0.btn_add_koment.onClick {
            //добавление коментариев
            add_koment_window(context, id)
        }
        p0.btn_update_koment.onClick {
            //обновить текуший список коментов
            load_koment(id)
        }

        GlobalScope.launch {
            //Загрузим в начале просто количество коментов
            load_koment(id)
        }
        //------------------------------------------------------------------------

        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
            menu_vse_radio_obmenik(context, radio, name)
        }
    }
}
