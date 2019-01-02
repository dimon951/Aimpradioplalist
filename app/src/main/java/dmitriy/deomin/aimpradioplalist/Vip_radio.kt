package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import java.util.ArrayList


class Vip_radio : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vip_radio, null)

        val context: Context = container!!.context
        val listView: ListView = v.findViewById<View>(R.id.listview_pop) as ListView

        val STANCIA = "stancia"
        val AVAPOP = "avapop"
        val LINK = "link"


        val mas_radio = resources.getStringArray(R.array.pop_radio)

        val data = ArrayList<HashMap<String, String>>(mas_radio.size)

        var m: MutableMap<String, String>

        for (i in mas_radio.indices) {
            m = HashMap()
            m[STANCIA] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            m[AVAPOP] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            //ссылка тоже список ссылок, разбиваться будет в адаптере
            m[LINK] = mas_radio[i].split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]
            data.add(m)
        }


        //кусок какахи чтоб все работало
        // массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf(STANCIA)
        // массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(R.id.Text_name_pop)


        // создаем адаптер
        val adapter_pop_radio = Adapter_pop_radio(context = context, data = data, resource = R.layout.delegat_pop, from = from, to = to)
        listView.adapter = adapter_pop_radio


        return v
    }

}
