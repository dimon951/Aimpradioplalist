package dmitriy.deomin.aimpradioplalist

import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick


class Adapter_vse_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_vse_list.ViewHolder>(), Filterable {


    private lateinit var context: Context
    private var raduoSearchList: List<Radio>? = data


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                raduoSearchList = if (charString.isEmpty()) {
                    data
                } else {
                    val filteredList = ArrayList<Radio>()

                    //поиск по имени или еще и по урл
                    if(Vse_radio.Poisk_ima_url==0){
                        for (row in data) {
                            if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                    }else{
                        for (row in data) {
                            if (row.name.toLowerCase().contains(charString.toLowerCase())||row.url.toLowerCase().contains(charString.toLowerCase())) {
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
                raduoSearchList = filterResults.values as ArrayList<Radio>
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.url_radio)
        val fon = itemView.findViewById<CardView>(R.id.fon_item_radio)
        // val fon2 = itemView.findViewById<LinearLayout>(R.id.fon2)
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

        //  p0.fon2.setBackgroundColor(Main.COLOR_ITEM)

        //настроим вид тутже
        //-----------------------------------------------
        p0.name_radio.typeface = Main.face
        p0.name_radio.textColor = Main.COLOR_TEXT

        p0.url_radio.typeface = Main.face
        p0.url_radio.textColor = Main.COLOR_TEXT

        p0.nomer_radio.typeface = Main.face
        p0.nomer_radio.textColor = Main.COLOR_TEXT
        //-------------------------------------------------------


        //заполним данными
        val radio: Radio = raduoSearchList!![p1]
        p0.name_radio.text = radio.name
        p0.url_radio.text = radio.url
        if(Vse_radio.Numeracia==1){
            p0.nomer_radio.text = (p1 + 1).toString() + ". "
        }else{
            p0.nomer_radio.text = ""
        }



        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

            // p0.fon2.setBackgroundColor(Color.DKGRAY)

            p0.name_radio.textColor = Color.DKGRAY
            p0.url_radio.textColor = Color.DKGRAY
            p0.nomer_radio.textColor = Color.DKGRAY

            //сохраняем позицию
            Main.save_value("nomer_stroki", p1.toString())

            val name = radio.name
            val url_link = radio.url

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.menu_vse_radio, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()

            val add_pls = content.findViewById<Button>(R.id.button_add_plalist)
            val open_aimp = content.findViewById<Button>(R.id.button_open_aimp)
            val share = content.findViewById<Button>(R.id.button_cshre)
            val instal_aimp = content.findViewById<Button>(R.id.button_instal_aimp)
            val instal_aimp2 = content.findViewById<Button>(R.id.button_download_yandex_aimp)


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
            val text_name_i_url = content.findViewById<TextView>(R.id.textView_vse_radio)
            text_name_i_url.text = name + "\n" + url_link
            text_name_i_url.onClick {
                text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                putText(url_link, context)
                context.toast("url скопирован в буфер")
            }


            open_aimp.onLongClick {
                open_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                Main.play_system(name,url_link)
            }

            instal_aimp.onClick {
                instal_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse("market://details?id=com.aimp.player")
            }

            instal_aimp2.onClick {
                instal_aimp2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            add_pls.onClick {
                add_pls.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                Main.add_myplalist(name, url_link)
                alertDialog.cancel()
            }

            share.onClick {
                share.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                //сосавим строчку как в m3u вайле
                context.share(name+"\n"+url_link)
            }

            open_aimp.onClick {
                open_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                Main.play_aimp(name, url_link)
                alertDialog.cancel()
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
