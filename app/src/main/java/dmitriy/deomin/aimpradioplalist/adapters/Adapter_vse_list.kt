package dmitriy.deomin.aimpradioplalist.adapters

import android.annotation.SuppressLint
import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.Vse_radio
import dmitriy.deomin.aimpradioplalist.menu.menu_vse_radio
import dmitriy.deomin.aimpradioplalist.`fun`.save_value_int
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class Adapter_vse_list(val data: ArrayList<Radio>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_vse_list.ViewHolder>(), Filterable {


    private lateinit var context: Context
    var raduoSearchList: ArrayList<Radio> = data
    //у списка нажатых строк при первом запуске будем отмечать последнию нажатую строку
    //потом будем добавлять но сохранится только последняя
    private var cho_nagimal: MutableSet<Int> = mutableSetOf(Vse_radio.cho_nagimali_poslednee)

    override fun getFilter(): Filter {
        //если поиск включили удалим метки с ранее нажатых строк
        cho_nagimal.clear()

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    this@Adapter_vse_list.raduoSearchList = data
                } else {
                    val filteredList = ArrayList<Radio>()
                        for (row in data) {
                            if (row.name.toLowerCase().contains(charString.toLowerCase())
                                    || row.url.toLowerCase().contains(charString.toLowerCase())
                                    || row.kbps.toLowerCase().contains(charString.toLowerCase())
                                    || row.kategory.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                    }
                    this@Adapter_vse_list.raduoSearchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = this@Adapter_vse_list.raduoSearchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                if(filterResults.values != null){
                    this@Adapter_vse_list.raduoSearchList = filterResults.values as ArrayList<Radio>
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
        val ganr = itemView.findViewById<TextView>(R.id.ganr_radio)
        val liner_kbps = itemView.findViewById<LinearLayout>(R.id.liner_kbps)
        val liner_ganr = itemView.findViewById<LinearLayout>(R.id.liner_ganr)
        val kbps = itemView.findViewById<TextView>(R.id.kbps_radio)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        this.context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //будем слать сигнал с текущим количеством
        signal("vse_radio_list_size").putExtra("size",raduoSearchList.size.toString()).send(Main.context)
        return this.raduoSearchList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //заполним данными(тут в логах бывает падает - обращение к несуществующему элементу)
        //поэтому будем проверять чтобы общее количество было больше текушего номера
        val radio: Radio = if(this.raduoSearchList.size>p1){
            this.raduoSearchList[p1]
        }else{
            //иначе вернём пустой элемент(дальше будут проверки и он не отобразится)
            Radio("","","","")
        }

        p0.url_radio.text = radio.url

        //если kbps нет вообще скроем
        if (radio.kbps.isNotEmpty()) {
            p0.liner_kbps.visibility = View.VISIBLE
            p0.kbps.text = radio.kbps
        } else {
            p0.liner_kbps.visibility = View.GONE
        }


        p0.name_radio.text = radio.name
        //нумерация списка
        if (Vse_radio.Numeracia == 1) {
            p0.nomer_radio.text = (p1 + 1).toString() + ". "
        } else {
            p0.nomer_radio.text = ""
        }
        //ganr
        if (radio.kategory.isNotEmpty()) {
            p0.liner_ganr.visibility = View.VISIBLE
            p0.ganr.text = radio.kategory
        } else {
            p0.liner_ganr.visibility = View.GONE
        }

        //поменяем цвет у строк которые уже нажимали
        if (Vse_radio.cho_nagimali_poslednee > 0) {
            if (cho_nagimal.any { it == p1 }) {
                p0.name_radio.textColor = Main.COLOR_SELEKT
                p0.url_radio.textColor = Main.COLOR_SELEKT
                p0.nomer_radio.textColor = Main.COLOR_SELEKT
                p0.kbps.textColor = Main.COLOR_SELEKT
                p0.ganr.textColor = Main.COLOR_SELEKT
            } else {
                p0.name_radio.textColor = Main.COLOR_TEXT
                p0.url_radio.textColor = Main.COLOR_TEXT
                p0.nomer_radio.textColor = Main.COLOR_TEXT
                p0.kbps.textColor = Main.COLOR_TEXT
                p0.ganr.textColor = Main.COLOR_TEXT
            }
        }

        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            GlobalScope.launch {
                Vse_radio.cho_nagimali_poslednee = p1
                //сохраним в список нажатых временый
                cho_nagimal.add(p1)
                //сохраняем позицию будет последняя и при запуске сработает
                save_value_int("cho_nagimali_poslednee", p1)
            }

            //перекрасим
            p0.name_radio.textColor = Main.COLOR_SELEKT
            p0.url_radio.textColor = Main.COLOR_SELEKT
            p0.nomer_radio.textColor = Main.COLOR_SELEKT
            p0.kbps.textColor = Main.COLOR_SELEKT
            p0.ganr.textColor = Main.COLOR_SELEKT


            menu_vse_radio(context,radio)
        }
    }
}
