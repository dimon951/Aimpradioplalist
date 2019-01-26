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
    private var url_link = ""
    private var kbps = ""


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                //если поиск включили удалим метки с ранее нажатых строк
                cho_nagimal.clear()

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
                                    || url_link.toLowerCase().contains(charString.toLowerCase())
                                    || kbps.toLowerCase().contains(charString.toLowerCase())
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

        val kbps1 = itemView.findViewById<TextView>(R.id.kbps_radio1)
        val kbps2 = itemView.findViewById<TextView>(R.id.kbps_radio2)
        val kbps3 = itemView.findViewById<TextView>(R.id.kbps_radio3)
        val kbps4 = itemView.findViewById<TextView>(R.id.kbps_radio4)
        val kbps5 = itemView.findViewById<TextView>(R.id.kbps_radio5)
        val kbps6 = itemView.findViewById<TextView>(R.id.kbps_radio6)

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return raduoSearchList!!.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //заполним данными
        val radio: Radio = raduoSearchList!![p1]

        val name = radio.name

        //урл который будем открывать
        //по умолчанию ставим первую кнопку
        bold_underline(p0, 1)
        url_link = radio.link1.url
        kbps = radio.link1.kbps
        p0.url_radio.text = url_link


        //если kbps нет вообще скроем
        if (kbps.isNotEmpty()) {
            p0.liner_kbps.visibility = View.VISIBLE
            p0.kbps1.text = kbps
        } else {
            p0.liner_kbps.visibility = View.GONE
        }


        //при кликах на кнопках качества будем обновлять вид и ссылку
        p0.kbps1.onClick { url_link = radio.link1.url; bold_underline(p0, 1) }
        p0.kbps2.onClick { url_link = radio.link2.url; bold_underline(p0, 2) }
        p0.kbps3.onClick { url_link = radio.link3.url; bold_underline(p0, 3) }
        p0.kbps4.onClick { url_link = radio.link4.url; bold_underline(p0, 4) }
        p0.kbps5.onClick { url_link = radio.link5.url; bold_underline(p0, 5) }
        p0.kbps6.onClick { url_link = radio.link6.url; bold_underline(p0, 6) }


        //покажем отскальные кнопки качества если есть
        if (radio.link2.url.isNotEmpty()) {
            p0.kbps2.visibility = View.VISIBLE
            p0.kbps2.text = radio.link2.kbps
        } else {
            p0.kbps2.visibility = View.GONE
        }

        if (radio.link3.url.isNotEmpty()) {
            p0.kbps3.visibility = View.VISIBLE
            p0.kbps3.text = radio.link3.kbps
        } else {
            p0.kbps3.visibility = View.GONE
        }
        if (radio.link4.url.isNotEmpty()) {
            p0.kbps4.visibility = View.VISIBLE
            p0.kbps4.text = radio.link4.kbps
        } else {
            p0.kbps4.visibility = View.GONE
        }
        if (radio.link5.url.isNotEmpty()) {
            p0.kbps5.visibility = View.VISIBLE
            p0.kbps5.text = radio.link5.kbps
        } else {
            p0.kbps5.visibility = View.GONE
        }
        if (radio.link6.url.isNotEmpty()) {
            p0.kbps6.visibility = View.VISIBLE
            p0.kbps6.text = radio.link6.kbps
        } else {
            p0.kbps6.visibility = View.GONE
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
                p0.kbps1.textColor = Color.DKGRAY
                p0.ganr.textColor = Color.DKGRAY
            } else {
                p0.name_radio.textColor = Main.COLOR_TEXT
                p0.url_radio.textColor = Main.COLOR_TEXT
                p0.nomer_radio.textColor = Main.COLOR_TEXT
                p0.kbps1.textColor = Main.COLOR_TEXT
                p0.ganr.textColor = Main.COLOR_TEXT
            }
        }

        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            GlobalScope.launch {
                //сохраним в список нажатых временый
                cho_nagimal.add(p1)
                //сохраняем позицию будет последняя и при запуске сработает
                Main.save_value_int("nomer_stroki_int", p1)
            }

            //перекрасим
            p0.name_radio.textColor = Color.DKGRAY
            p0.url_radio.textColor = Color.DKGRAY
            p0.nomer_radio.textColor = Color.DKGRAY
            p0.kbps1.textColor = Color.DKGRAY
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
            text_name_i_url.text = name + "\n" + url_link
            text_name_i_url.onClick {
                text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                putText(url_link, context)
                context.toast("url скопирован в буфер")
            }


            open_aimp.onLongClick {
                Main.play_system(name, url_link)
            }

            instal_aimp.onClick {
                context.browse("market://details?id=com.aimp.player")
            }

            instal_aimp2.onClick {
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            add_pls.onClick {
                Main.add_myplalist(name, url_link)
                mvr.close()
            }

            share.onClick {
                //сосавим строчку как в m3u вайле
                context.share(name + "\n" + url_link)
            }

            open_aimp.onClick {
                Main.play_aimp(name, url_link)
                mvr.close()
            }

        }


    }


    private fun bold_underline(p0: ViewHolder, n: Int) {

        p0.url_radio.text = url_link

        when (n) {
            1 -> {
                //поменяем вид кнопки и поток
                p0.kbps1.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps1.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps2.paintFlags = 0
                p0.kbps2.typeface = Main.face
                p0.kbps3.paintFlags = 0
                p0.kbps3.typeface = Main.face
                p0.kbps4.paintFlags = 0
                p0.kbps4.typeface = Main.face
                p0.kbps5.paintFlags = 0
                p0.kbps5.typeface = Main.face
                p0.kbps6.paintFlags = 0
                p0.kbps6.typeface = Main.face
            }
            2 -> {
                //поменяем
                p0.kbps2.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps2.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps1.paintFlags = 0
                p0.kbps1.typeface = Main.face
                p0.kbps3.paintFlags = 0
                p0.kbps3.typeface = Main.face
                p0.kbps4.paintFlags = 0
                p0.kbps4.typeface = Main.face
                p0.kbps5.paintFlags = 0
                p0.kbps5.typeface = Main.face
                p0.kbps6.paintFlags = 0
                p0.kbps6.typeface = Main.face
            }
            3 -> {
                //поменяем
                p0.kbps3.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps3.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps2.paintFlags = 0
                p0.kbps2.typeface = Main.face
                p0.kbps1.paintFlags = 0
                p0.kbps1.typeface = Main.face
                p0.kbps4.paintFlags = 0
                p0.kbps4.typeface = Main.face
                p0.kbps5.paintFlags = 0
                p0.kbps5.typeface = Main.face
                p0.kbps6.paintFlags = 0
                p0.kbps6.typeface = Main.face
            }
            4 -> {
                //поменяем
                p0.kbps4.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps4.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps2.paintFlags = 0
                p0.kbps2.typeface = Main.face
                p0.kbps3.paintFlags = 0
                p0.kbps3.typeface = Main.face
                p0.kbps1.paintFlags = 0
                p0.kbps1.typeface = Main.face
                p0.kbps5.paintFlags = 0
                p0.kbps5.typeface = Main.face
                p0.kbps6.paintFlags = 0
                p0.kbps6.typeface = Main.face
            }
            5 -> {
                //поменяем
                p0.kbps5.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps5.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps2.paintFlags = 0
                p0.kbps2.typeface = Main.face
                p0.kbps3.paintFlags = 0
                p0.kbps3.typeface = Main.face
                p0.kbps4.paintFlags = 0
                p0.kbps4.typeface = Main.face
                p0.kbps1.paintFlags = 0
                p0.kbps1.typeface = Main.face
                p0.kbps6.paintFlags = 0
                p0.kbps6.typeface = Main.face
            }
            6 -> {
                //поменяем
                p0.kbps6.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                p0.kbps6.setTypeface(Main.face, Typeface.BOLD)
                //у остальных сбросим
                p0.kbps2.paintFlags = 0
                p0.kbps2.typeface = Main.face
                p0.kbps3.paintFlags = 0
                p0.kbps3.typeface = Main.face
                p0.kbps4.paintFlags = 0
                p0.kbps4.typeface = Main.face
                p0.kbps1.paintFlags = 0
                p0.kbps1.typeface = Main.face
                p0.kbps5.paintFlags = 0
                p0.kbps5.typeface = Main.face
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
