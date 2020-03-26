package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_pop_radio
import dmitriy.deomin.aimpradioplalist.custom.Link
import dmitriy.deomin.aimpradioplalist.custom.RadioPop
import dmitriy.deomin.aimpradioplalist.custom.Slot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import java.io.FileReader
import java.util.*


class Pop_radio : Fragment() {

    @SuppressLint("InflateParams", "WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.pop_radio, null)

        val context: Context = Main.context

        val recycl_pop_radio = v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recicl_pop_radio)
        recycl_pop_radio.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        recycl_pop_radio.setHasFixedSize(true)

        val data = ArrayList<RadioPop>()

        GlobalScope.launch {
            //получаем список мудрёный
            val mas_radio = resources.getStringArray(R.array.pop_radio)

            for (s in mas_radio.iterator()) if (s!=null) {
                val m = s.split("\n")
                if (m.size > 1) {
                    val links: List<String> = m[2].split("~kbps~")

                    data.add(RadioPop(m[0], m[1],
                            (if (links.elementAtOrNull(1) != null) {
                                Link(links[0], links[1])
                            } else {
                                Link("", "")
                            }),
                            (if (links.elementAtOrNull(3) != null) {
                                Link(links[2], links[3])
                            } else {
                                Link("", "")
                            }),
                            (if (links.elementAtOrNull(5) != null) {
                                Link(links[4], links[5])
                            } else {
                                Link("", "")
                            }),
                            (if (links.elementAtOrNull(7) != null) {
                                Link(links[6], links[7])
                            } else {
                                Link("", "")
                            }),
                            (if (links.elementAtOrNull(9) != null) {
                                Link(links[8], links[9])
                            } else {
                                Link("", "")
                            })))
                }
            }
            try {
                // создаем адаптер
                val adapter_pop_radio = Adapter_pop_radio(data)
                recycl_pop_radio.adapter = adapter_pop_radio
            } catch (e: Exception) {
               toast("Ошибка чтения данных")
            }
        }


        Slot(Main.context, "save_all_popularnoe").onRun {
            GlobalScope.launch {
                val f = File_function()
                //составим норм список
                val s_data = ArrayList<String>()
                s_data.add("#EXTM3U")
                for (s in data.iterator()) {
                    s_data.add("\n#EXTINF:-1," + s.name + " " + s.link1.kbps + "\n" + s.link1.url +
                            potok_ne_pustoy(s.name + " " + s.link2.kbps, s.link2.url) +
                            potok_ne_pustoy(s.name + " " + s.link3.kbps, s.link3.url) +
                            potok_ne_pustoy(s.name + " " + s.link4.kbps, s.link4.url) +
                            potok_ne_pustoy(s.name + " " + s.link5.kbps, s.link5.url))

                }
                f.saveArrayList("vse_pop_radio.m3u", s_data)
            }
        }

        return v
    }

    fun potok_ne_pustoy(name: String, url: String): String {
        return if (url.length > 2) {
            "\n#EXTINF:-1,$name\n$url"
        } else {
            ""
        }
    }
}
