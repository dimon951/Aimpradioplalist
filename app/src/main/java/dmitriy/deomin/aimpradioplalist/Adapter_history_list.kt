package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.custom.History
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share

//===========================Адаптер к спику истории ссылок=============================================================
class Adapter_history_list(val data: ArrayList<History>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_history_list.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val url = itemView.findViewById<TextView>(R.id.url_potok)
        val name = itemView.findViewById<TextView>(R.id.name_potok)
        val data_time = itemView.findViewById<TextView>(R.id.data_add_potok)
        val share = itemView.findViewById<Button>(R.id.button_share_url_plalist)
        val liner = itemView.findViewById<LinearLayout>(R.id.liner_online_plalist)
        val fon = itemView.findViewById<LinearLayout>(R.id.fon_item_vvoda_potoka)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_vvoda_potoka, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val history = data[p1]

        p0.url.text = history.url
        p0.name.text = history.name
        p0.data_time.text = history.data_time


        p0.liner.onClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            signal("clik_history_item").putExtra("url", history.url).putExtra("name", history.name).send(Main.context)
        }
        p0.liner.onLongClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //пересоберём список без текущей строки
            GlobalScope.launch {
                val save_data = ArrayList<String>()
                for (d in data) {
                    if (d.url != history.url) {
                        save_data.add(d.name + "$" + d.url + "$" + d.data_time)
                    }
                }
                File_function().saveArrayList(Main.HISTORY_LINK, save_data)
            }
            //не буду нечего слушать и проверять так пока сделаю
            data.removeAt(p1)
            notifyDataSetChanged()
        }


        p0.share.onClick {
            context.share(history.url)
        }
    }
}
//======================================================================================================================