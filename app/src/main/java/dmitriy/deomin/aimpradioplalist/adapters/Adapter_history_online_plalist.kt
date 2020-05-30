package dmitriy.deomin.aimpradioplalist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.menu.menu_saved_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.History
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.io.File

//===========================Адаптер к спику истории ссылок=============================================================
class Adapter_history_online_plalist(val data: ArrayList<History>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter_history_online_plalist.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val data_time = itemView.findViewById<TextView>(R.id.data)
        val size_file = itemView.findViewById<TextView>(R.id.size_file)
        val liner = itemView.findViewById<LinearLayout>(R.id.liner)
        val btn_del = itemView.findViewById<Button>(R.id.btn_del_history)
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
        p0.size_file.text = history.size



        p0.liner.onClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //пошдём сигнал закрыть окно истории
            signal("History_online_plalist").send(Main.context)

            //если тыкают в плейлист
            if (history.url.substringAfterLast('.') == "mp3") {
                menu_saved_file(context, history.url, false)
            } else {
                //пошлём сигнал для загрузки дааных п спискок
                signal("Data_add")
                        .putExtra("update", "zaebis")
                        .putExtra("listfile", history.url)
                        .send(Main.context)
                signal("Main_update").putExtra("signal","move2").send(Main.context)
            }

        }
        p0.liner.onLongClick {
            p0.liner.startAnimation(AnimationUtils.loadAnimation(Main.context, R.anim.myalpha))
            //если долго тыкают в плейлист
            if (history.url.substringAfterLast('.') == "mp3") {
                play_system_file(history.url)
            } else {

            }
        }

        p0.btn_del.onClick {
            if(history.url.substringAfterLast('.') == "mp3"){
                val d = DialogWindow(context, R.layout.dialog_delete_stancii)
                val t = d.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
                t.text = "Удалить "+history.name +" ("+history.size+") ?"
                val da = d.view().findViewById<Button>(R.id.button_dialog_delete)
                da.onClick {
                    d.close()
                    if(File(history.url).delete()){
                        data.removeAt(p1)
                        //пошлём сигнал пусть из истории удалится тоже
                        signal("histori_del_item").putExtra("item",history.name).send(context)
                        notifyDataSetChanged()
                        //если список пуст закроем
                        if(data.size==0){
                            signal("History_online_plalist").send(context)
                            signal("Online_plalist")
                                    .putExtra("update", "zaebis")
                                    .send(context)
                        }

                    }
                }
                d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
                    d.close()
                }

            }else{
                if(File(history.url).delete()){
                    data.removeAt(p1)
                    notifyDataSetChanged()
                    //если список пуст закроем
                    if(data.size==0){
                        signal("History_online_plalist").send(context)
                        signal("Online_plalist")
                                .putExtra("update", "zaebis")
                                .send(context)
                    }
                }
            }
        }

    }
}
//======================================================================================================================