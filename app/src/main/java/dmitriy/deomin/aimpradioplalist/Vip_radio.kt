package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import java.util.ArrayList
import java.util.HashMap


class Vip_radio : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vip_radio, null)

        val context: Context = container!!.context
        val listView: ListView = v.findViewById<View>(R.id.listview_pop) as ListView

        val STANCIA = "stancia"
        val AVAPOP = "avapop"
        val LINK = "link"

        val mas_radio = resources.getStringArray(R.array.pop_radio)

        val data = ArrayList<Map<String, Any>>(mas_radio.size)

        var m: MutableMap<String, Any>

        for (i in mas_radio.indices) {
            m = HashMap()
            m[STANCIA] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            m[AVAPOP] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            m[LINK] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2] //ссылка тоже список ссылок разбываться буде в адаптере
            data.add(m)
        }

        val adapter_pop_radio = Adapter_pop_radio(context, data, R.layout.delegat_vse_radio_list, null, null)
        listView.adapter = adapter_pop_radio


        return v
    }
}
