package dmitriy.deomin.aimpradioplalist
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.custom.History
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick

//===========================Адаптер к спику истории ссылок=============================================================
class Adapter_history_online_plalist(val data: ArrayList<History>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_history_online_plalist.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val data_time = itemView.findViewById<TextView>(R.id.data)
        val liner = itemView.findViewById<LinearLayout>(R.id.liner)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_history_online_plalist, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val history = data[p1]

        p0.name.text = history.name
        p0.data_time.text = history.data_time


        p0.liner.onClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //пошдём сигнал закрыть окно истории
            signal("History_online_plalist").send(Main.context)

            //если тыкают в плейлист
            if(history.url.substringAfterLast('.')=="mp3"){
                Main.play_aimp_file(Main.ROOT + history.url)
            }else{
                //пошлём сигнал для загрузки дааных п спискок
                signal("Online_plalist")
                        .putExtra("update", "zaebis")
                        .putExtra("listfile", history.url)
                        .send(Main.context)
            }

        }
        p0.liner.onLongClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //если долго тыкают в плейлист
            if(history.url.substringAfterLast('.')=="mp3"){
                Main.play_aimp_file(Main.ROOT + history.url)
            }else{

            }
        }

//        //пересоберём список без текущей строки
//        GlobalScope.launch {
//            val save_data = ArrayList<String>()
//            for (d in data) {
//                if (d.url != history.url) {
//                    save_data.add(d.name + "$" + d.url + "$" + d.data_time)
//                }
//            }
//            File_function().saveArrayList(Main.HISTORY_LINK, save_data)
//        }
//        //не буду нечего слушать и проверять так пока сделаю
//        data.removeAt(p1)
//        notifyDataSetChanged()
    }
}
//======================================================================================================================