package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import java.util.ArrayList


class Pop_radio : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.pop_radio, null)

        val context: Context = container!!.context

        val recycl_pop_radio = v.findViewById<RecyclerView>(R.id.recicl_pop_radio)
        recycl_pop_radio.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        //получаем список мудрёный
        val mas_radio = resources.getStringArray(R.array.pop_radio)

        val data = ArrayList<RadioPop>()

        for (i in mas_radio.indices) {
            val m = mas_radio[i].split("\n")
            val links:List<String> = m[2].split("~kbps~")

            data.add(RadioPop(m[0], m[1],
                    (if(links.elementAtOrNull(1)!=null){Link(links[0],links[1])}else{Link("","")}),
                    (if(links.elementAtOrNull(3)!=null){Link(links[2],links[3])}else{Link("","")}),
                    (if(links.elementAtOrNull(5)!=null){Link(links[4],links[5])}else{Link("","")}),
                    (if(links.elementAtOrNull(7)!=null){Link(links[6],links[7])}else{Link("","")}),
                    (if(links.elementAtOrNull(9)!=null){Link(links[8],links[9])}else{Link("","")})))
        }

        // создаем адаптер
        val adapter_pop_radio = Adapter_pop_radio(data)
        recycl_pop_radio.adapter = adapter_pop_radio

        return v
    }

}
