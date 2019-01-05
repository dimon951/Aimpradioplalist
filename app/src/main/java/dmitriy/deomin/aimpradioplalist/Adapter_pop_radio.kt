package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.jetbrains.anko.email
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.ArrayList


class Adapter_pop_radio(val data: ArrayList<RadioPop>) : RecyclerView.Adapter<Adapter_pop_radio.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.Text_name_pop)
        val ava = itemView.findViewById<ImageView>(R.id.ava_pop)
        val add = itemView.findViewById<Button>(R.id.button_add)
        val play = itemView.findViewById<Button>(R.id.button_open)
        val share = itemView.findViewById<Button>(R.id.button_cshre)
        val kbps1 = itemView.findViewById<Button>(R.id.pop_vibr_kbts_1)
        val kbps2 = itemView.findViewById<Button>(R.id.pop_vibr_kbts_2)
        val kbps3 = itemView.findViewById<Button>(R.id.pop_vibr_kbts_3)
        val kbps4 = itemView.findViewById<Button>(R.id.pop_vibr_kbts_4)
        val kbps5 = itemView.findViewById<Button>(R.id.pop_vibr_kbts_5)
    }

    private var transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Main.COLOR_TEXT)
            .borderWidthDp(2f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio_pop, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val radiopop = data[p1]

        //название
        p0.text.text = radiopop.name

        //ставим картинку
        Picasso.get()
                .load("file:///android_asset/ava_pop/" + radiopop.ava_url)
                .transform(transformation)
                .into(p0.ava)

        //кнопки качеста скрываем пустые
        p0.kbps1.visibility = if (!radiopop.link1.kbps.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        p0.kbps2.visibility = if (!radiopop.link2.kbps.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        p0.kbps3.visibility = if (!radiopop.link3.kbps.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        p0.kbps4.visibility = if (!radiopop.link4.kbps.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        p0.kbps5.visibility = if (!radiopop.link5.kbps.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        //заполняем текстом
        p0.kbps1.text = if (!radiopop.link1.kbps.isEmpty()) {
            radiopop.link1.kbps
        } else {
            ""
        }
        p0.kbps2.text = if (!radiopop.link2.kbps.isEmpty()) {
            radiopop.link2.kbps
        } else {
            ""
        }
        p0.kbps3.text = if (!radiopop.link3.kbps.isEmpty()) {
            radiopop.link3.kbps
        } else {
            ""
        }
        p0.kbps4.text = if (!radiopop.link4.kbps.isEmpty()) {
            radiopop.link4.kbps
        } else {
            ""
        }
        p0.kbps5.text = if (!radiopop.link5.kbps.isEmpty()) {
            radiopop.link5.kbps
        } else {
            ""
        }

        //по умолчанию ставим первую кнопку
        bold_underline(p0, 1)
        var popurl = radiopop.link1.url

        p0.kbps1.textColor =  Main.COLOR_TEXT
        p0.kbps2.textColor =  Main.COLOR_TEXT
        p0.kbps3.textColor =  Main.COLOR_TEXT
        p0.kbps4.textColor =  Main.COLOR_TEXT
        p0.kbps5.textColor =  Main.COLOR_TEXT

        //при кликах на кнопках качества будем обновлять вид и ссылку
        p0.kbps1.onClick { bold_underline(p0, 1); popurl = radiopop.link1.url }
        p0.kbps2.onClick { bold_underline(p0, 2); popurl = radiopop.link2.url }
        p0.kbps3.onClick { bold_underline(p0, 3); popurl = radiopop.link3.url }
        p0.kbps4.onClick { bold_underline(p0, 4); popurl = radiopop.link4.url }
        p0.kbps5.onClick { bold_underline(p0, 5); popurl = radiopop.link5.url }


        p0.share.onClick {
            p0.share.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            context.share(popurl)
        }
        p0.share.onLongClick {
            context.email("deomindmitriy@gmail.com", "aimp_radio_plalist", popurl)
        }

        p0.play.onClick {
            p0.play.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))


            //фильтр для нашего сигнала
            val intentFilter = IntentFilter()
            intentFilter.addAction("File_created")

            //приёмник  сигналов
            val broadcastReceiver = object : BroadcastReceiver() {
                @SuppressLint("WrongConstant")
                override fun onReceive(c: Context, intent: Intent) {
                    if (intent.action == "File_created") {
                        //получим данные
                        val s = intent.getStringExtra("update")
                        if (s == "zaebis") {
                            //проверим есть ли аимп
                            if (Main.install_app("com.aimp.player")) {
                                //откроем файл с сылкой в плеере
                                val cm = ComponentName(
                                        "com.aimp.player",
                                        "com.aimp.player.views.MainActivity.MainActivity")

                                val i = Intent()
                                i.component = cm

                                i.action = Intent.ACTION_VIEW
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + radiopop.name + ".m3u"), "audio/mpegurl")
                                i.flags = 0x3000000

                                context.startActivity(i)

                            } else {
                                //иначе предложим системе открыть или установить аимп
                                Main.setup_aimp(popurl,
                                        "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + radiopop.name + ".m3u")
                            }
                        } else {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            Main.EbuchieRazreshenia()
                        }
                        //попробуем уничтожить слушителя
                        context.unregisterReceiver(this)
                    }
                }
            }

            //регистрируем приёмник
            context.registerReceiver(broadcastReceiver, intentFilter)


            //сохраним  временый файл ссылку и будем ждать сигнала чтобы открыть в аимп или системе
            val file_function = File_function()
            file_function.Save_temp_file(radiopop.name + ".m3u",
                    "#EXTM3U"
                            + "\n"
                            + "#EXTINF:-1," + radiopop.name
                            + "\n"
                            + popurl)
        }

        p0.add.onClick {
            p0.add.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

            //фильтр для нашего сигнала
            val intentFilter = IntentFilter()
            intentFilter.addAction("File_created")

            //приёмник  сигналов
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    if (intent.action == "File_created") {
                        //получим данные
                        val s = intent.getStringExtra("update")
                        when (s) {
                            "est" -> context.toast("Такая станция уже есть в плейлисте")
                            "zaebis" -> {
                                //пошлём сигнал пусть мой плейлист обновится
                                val i = Intent("Data_add")
                                i.putExtra("update", "zaebis")
                                context.sendBroadcast(i)
                            }
                            "pizdec" -> {
                                context.toast(context.getString(R.string.error))
                                //запросим разрешения
                                Main.EbuchieRazreshenia()
                            }
                        }
                        //попробуем уничтожить слушителя
                        context.unregisterReceiver(this)
                    }
                }
            }

            //регистрируем приёмник
            context.registerReceiver(broadcastReceiver, intentFilter)

            val file_function = File_function()

            //запишем в файл выбранную станцию
            file_function.Add_may_plalist_stansiy(popurl, radiopop.name)

        }


    }

    private fun bold_underline(p0: ViewHolder, n: Int) {

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
            }
        }
    }

}