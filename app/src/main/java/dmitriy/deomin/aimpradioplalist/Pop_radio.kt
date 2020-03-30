package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import dmitriy.deomin.aimpradioplalist.`fun`.m3u.create_m3u_file
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_pop_radio
import dmitriy.deomin.aimpradioplalist.custom.Link
import dmitriy.deomin.aimpradioplalist.custom.Radio
import dmitriy.deomin.aimpradioplalist.custom.RadioPop
import dmitriy.deomin.aimpradioplalist.custom.Slot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
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

            for (s in mas_radio.iterator()) if (s != null) {
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
                //составим норм список
                val data_save = ArrayList<Radio>()

                for (s in data.iterator()) {
                    data_save.add(Radio(name = s.name, kbps = s.link1.kbps, url = s.link1.url))
                    //дополнительные ссылки
                    if (s.link2.url.isNotEmpty()) data_save.add(Radio(name = s.name, kbps = s.link2.kbps, url = s.link2.url))
                    if (s.link3.url.isNotEmpty()) data_save.add(Radio(name = s.name, kbps = s.link3.kbps, url = s.link3.url))
                    if (s.link4.url.isNotEmpty()) data_save.add(Radio(name = s.name, kbps = s.link4.kbps, url = s.link4.url))
                    if (s.link5.url.isNotEmpty()) data_save.add(Radio(name = s.name, kbps = s.link5.kbps, url = s.link5.url))
                }

                Slot(context, "File_created", false).onRun {
                    //получим данные
                    when (it.getStringExtra("update")) {
                        //узнаем результат сохранения
                        "zaebis" -> Main.context.toast("Весь список сохранен в " + it.getStringExtra("name"))
                        "pizdec" -> Main.context.toast(Main.context.getString(R.string.error))
                    }
                }
                create_m3u_file("vse_pop_radio", data_save)
            }
        }

        return v
    }
}
