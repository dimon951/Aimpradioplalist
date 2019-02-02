package dmitriy.deomin.aimpradioplalist

import android.content.*
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Radio
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick


class Adapter_vse_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_vse_list.ViewHolder>(), Filterable {


    private lateinit var context: Context
    private var raduoSearchList: List<Radio>? = data
    //у списка нажатых строк при первом запуске будем отмечать последнию нажатую строку
    //потом будем добавлять но сохранится только последняя
    private var cho_nagimal: MutableSet<Int> = mutableSetOf(Main.cho_nagimali_poslednee)

    override fun getFilter(): Filter {
        //если поиск включили удалим метки с ранее нажатых строк
        cho_nagimal.clear()

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()
                raduoSearchList = if (charString.isEmpty()) {
                    data
                } else {
                    val filteredList = ArrayList<Radio>()

                    //поиск по имени или по всему что есть
                    if (Vse_radio.Poisk_ima_url == 0) {
                        for (row in data) {
                            if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                    } else {
                        for (row in data) {
                            if (row.name.toLowerCase().contains(charString.toLowerCase())
                                    || row.url.toLowerCase().contains(charString.toLowerCase())
                                    || row.kbps.toLowerCase().contains(charString.toLowerCase())
                                    || row.kategory.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                    }


                    filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = raduoSearchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                raduoSearchList = filterResults.values as List<Radio>?
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.url_radio)
        val fon = itemView.findViewById<CardView>(R.id.fon_item_radio)
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
        return raduoSearchList!!.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //заполним данными
        val radio: Radio = this.raduoSearchList!![p1]

        val name = radio.name

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
        if (Main.cho_nagimali_poslednee > 0) {
            if (cho_nagimal.any { it == p1 }) {
                p0.name_radio.textColor = Color.DKGRAY
                p0.url_radio.textColor = Color.DKGRAY
                p0.nomer_radio.textColor = Color.DKGRAY
                p0.kbps.textColor = Color.DKGRAY
                p0.ganr.textColor = Color.DKGRAY
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
                Main.cho_nagimali_poslednee = p1
                //сохраним в список нажатых временый
                cho_nagimal.add(p1)
                //сохраняем позицию будет последняя и при запуске сработает
                Main.save_value_int("nomer_stroki_int", p1)
            }

            //перекрасим
            p0.name_radio.textColor = Color.DKGRAY
            p0.url_radio.textColor = Color.DKGRAY
            p0.nomer_radio.textColor = Color.DKGRAY
            p0.kbps.textColor = Color.DKGRAY
            p0.ganr.textColor = Color.DKGRAY


            val mvr = DialogWindow(context, R.layout.menu_vse_radio)

            val add_pls = mvr.view().findViewById<Button>(R.id.button_add_plalist)
            val open_aimp = mvr.view().findViewById<Button>(R.id.button_open_aimp)
            val share = mvr.view().findViewById<Button>(R.id.button_cshre)
            val instal_aimp = mvr.view().findViewById<Button>(R.id.button_instal_aimp)
            val instal_aimp2 = mvr.view().findViewById<Button>(R.id.button_download_yandex_aimp)


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
                putText(radio.url, context)
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

            open_aimp.onClick {
                Main.play_aimp(name, radio.url)
                mvr.close()
            }
        }
    }
    //запись
    fun putText(text: String, context: Context) {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText(text, text)
            clipboard.primaryClip = clip
        }
    }


}
