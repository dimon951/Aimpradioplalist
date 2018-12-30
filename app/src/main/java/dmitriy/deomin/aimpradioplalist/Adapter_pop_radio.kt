package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.Contacts
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.email
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.share
import org.jetbrains.anko.support.v4.email
import org.jetbrains.anko.support.v4.share
import org.jetbrains.anko.toast
import java.util.ArrayList


class Adapter_pop_radio(context: Context?, data: ArrayList<HashMap<String, String>>, resource: Int, from: Array<out String>?, to: IntArray?) : SimpleAdapter(context, data, resource, from, to) {

    val results: ArrayList<HashMap<String, String>> = data
    val context: Context = context!!


    class ViewHolder {
        lateinit var link: String
        lateinit var text: TextView
        lateinit var ava: ImageView
        lateinit var add: Button
        lateinit var play: Button
        lateinit var share: Button

        lateinit var kbps1: Button
        lateinit var kbps2: Button
        lateinit var kbps3: Button
        lateinit var kbps4: Button
        lateinit var kbps5: Button
    }

    private var transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Main.COLOR_TEXT)
            .borderWidthDp(2f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

    @SuppressLint("ViewHolder")
    override fun getView(p: Int, v: View?, parent: ViewGroup?): View {
        val viewHolder = ViewHolder()

        //получаем все наши виджеты
        //---------------------------------------------------
        val view: View
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.delegat_pop, parent, false)

        viewHolder.text = view.findViewById(R.id.Text_name_pop)
        viewHolder.ava = view.findViewById(R.id.ava_pop)
        viewHolder.add = view.findViewById(R.id.button_add)
        viewHolder.play = view.findViewById(R.id.button_open)
        viewHolder.share = view.findViewById(R.id.button_cshre)
        viewHolder.kbps1 = view.findViewById(R.id.pop_vibr_kbts_1)
        viewHolder.kbps2 = view.findViewById(R.id.pop_vibr_kbts_2)
        viewHolder.kbps3 = view.findViewById(R.id.pop_vibr_kbts_3)
        viewHolder.kbps4 = view.findViewById(R.id.pop_vibr_kbts_4)
        viewHolder.kbps5 = view.findViewById(R.id.pop_vibr_kbts_5)
        //-------------------------------------------------------


        //смотрим че там передалось в параметрах какаое качество и покажем нужные кнопки
        val mass_link_parametr = results[p]["link"].toString().split("~kbps~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        //по умолчанию ставим первую ссылку()
        viewHolder.link = mass_link_parametr[1]

        viewHolder.text.typeface = Main.face
        viewHolder.text.text = results[p]["stancia"].toString()


        // скроем и покажем кнопки , заполним их
        when (mass_link_parametr.size) {
            2 -> {
                viewHolder.kbps1.visibility = View.VISIBLE
                viewHolder.kbps1.text = mass_link_parametr[0]
                viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps2.visibility = View.GONE
                viewHolder.kbps3.visibility = View.GONE
                viewHolder.kbps4.visibility = View.GONE
                viewHolder.kbps5.visibility = View.GONE
            }
            4 -> {
                viewHolder.kbps1.visibility = View.VISIBLE
                viewHolder.kbps1.text = mass_link_parametr[0]
                viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps2.visibility = View.VISIBLE
                viewHolder.kbps2.text = mass_link_parametr[2]
                viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps3.visibility = View.GONE
                viewHolder.kbps4.visibility = View.GONE
                viewHolder.kbps5.visibility = View.GONE
            }
            6 -> {
                viewHolder.kbps1.visibility = View.VISIBLE
                viewHolder.kbps1.text = mass_link_parametr[0]
                viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps2.visibility = View.VISIBLE
                viewHolder.kbps2.text = mass_link_parametr[2]
                viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps3.visibility = View.VISIBLE
                viewHolder.kbps3.text = mass_link_parametr[4]
                viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps4.visibility = View.GONE
                viewHolder.kbps5.visibility = View.GONE
            }
            8 -> {
                viewHolder.kbps1.visibility = View.VISIBLE
                viewHolder.kbps1.text = mass_link_parametr[0]
                viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps2.visibility = View.VISIBLE
                viewHolder.kbps2.text = mass_link_parametr[2]
                viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps3.visibility = View.VISIBLE
                viewHolder.kbps3.text = mass_link_parametr[4]
                viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps4.visibility = View.VISIBLE
                viewHolder.kbps4.text = mass_link_parametr[6]
                viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps5.visibility = View.GONE
            }
            10 -> {
                viewHolder.kbps1.visibility = View.VISIBLE
                viewHolder.kbps1.text = mass_link_parametr[0]
                viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps2.visibility = View.VISIBLE
                viewHolder.kbps2.text = mass_link_parametr[2]
                viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps3.visibility = View.VISIBLE
                viewHolder.kbps3.text = mass_link_parametr[4]
                viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps4.visibility = View.VISIBLE
                viewHolder.kbps4.text = mass_link_parametr[6]
                viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                viewHolder.kbps5.visibility = View.VISIBLE
                viewHolder.kbps5.text = mass_link_parametr[8]
                viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
            }
        }


        //по умолчанию сделаем первую кнопку жирным и подчеркнем
        Main.text = SpannableString(viewHolder.kbps1.text.toString())
        Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps1.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps1.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewHolder.kbps1.text = Main.text

        //ставим картинку
        Picasso.get()
                .load("file:///android_asset/ava_pop/" + results[p]["avapop"].toString())
                .transform(transformation)
                .into(viewHolder.ava)

        viewHolder.add.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.number_page = 1

            //фильтр для нашего сигнала
            val intentFilter = IntentFilter()
            intentFilter.addAction("File_created")

            //приёмник  сигналов
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    if (intent.action == "File_created") {
                        //получим данные
                        val s = intent.getStringExtra("update")
                        if (s == "zaebis") {
                            //обновим старницу
                            Main.myadapter.notifyDataSetChanged()
                            Main.viewPager.adapter = Main.myadapter
                            Main.viewPager.currentItem = Main.number_page
                        } else {
                            context.toast(context.getString(R.string.error))
                            //Изменим текущию вкладку при обновлении что тутж остаться
                            Main.number_page = 1
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

            val file_function = File_function()

            //запишем в файл выбранную станцию
            file_function.Add_may_plalist_stansiy(viewHolder.link, results[p]["stancia"].toString())
        }
        viewHolder.play.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            //Изменим текущию вкладку при обновлении что тутж остаться
            Main.number_page = 1

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
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + results[p]["stancia"].toString() + ".m3u"), "audio/mpegurl")
                                i.flags = 0x3000000

                                context.startActivity(i)

                            } else {
                                //иначе предложим системе открыть или установить аимп
                                Main.setup_aimp(viewHolder.link,
                                        "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + results[p]["stancia"].toString() + ".m3u")
                            }
                        } else {
                            context.toast(context.getString(R.string.error))
                            //Изменим текущию вкладку при обновлении что тутж остаться
                            Main.number_page = 1
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
            file_function.Save_temp_file(results[p]["stancia"].toString() + ".m3u",
                    "#EXTM3U"
                            + "\n"
                            + "#EXTINF:-1," + results[p]["stancia"].toString()
                            + "\n"
                            + viewHolder.link)


        }
        viewHolder.share.onClick {
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            viewHolder.share.startAnimation(anim)
            context.share("Поделиться через:", viewHolder.link)
        }
        viewHolder.share.onLongClick {
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            viewHolder.share.startAnimation(anim)
            context.email("deomindmitriy@gmail.com", "aimp_radio_plalist", viewHolder.link)
        }

        //будем слушать кнопки и менять ссылку
        viewHolder.kbps1.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.text = SpannableString(viewHolder.kbps1.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps1.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps1.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            //сбросим другие кнопки
            when (mass_link_parametr.size) {
                2 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.GONE
                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                4 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                6 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                8 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.GONE
                }
                10 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.VISIBLE
                    viewHolder.kbps5.text = mass_link_parametr[8]
                    viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
                }
            }


            viewHolder.kbps1.text = Main.text

            viewHolder.link = mass_link_parametr[1]
        }
        viewHolder.kbps2.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.text = SpannableString(viewHolder.kbps2.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps2.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps2.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            //сбросим другие кнопки
            when (mass_link_parametr.size) {
                2 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.GONE
                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                4 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                6 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                8 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.GONE
                }
                10 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.VISIBLE
                    viewHolder.kbps5.text = mass_link_parametr[8]
                    viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
                }
            }

            viewHolder.kbps2.text = Main.text

            viewHolder.link = mass_link_parametr[3]
        }
        viewHolder.kbps3.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.text = SpannableString(viewHolder.kbps3.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps3.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps3.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            //сбросим другие кнопки
            when (mass_link_parametr.size) {
                2 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.GONE
                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                4 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                6 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                8 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.GONE
                }
                10 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.VISIBLE
                    viewHolder.kbps5.text = mass_link_parametr[8]
                    viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
                }
            }

            viewHolder.kbps3.text = Main.text

            viewHolder.link = mass_link_parametr[5]
        }
        viewHolder.kbps4.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.text = SpannableString(viewHolder.kbps4.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps4.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps4.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            //сбросим другие кнопки
            when (mass_link_parametr.size) {
                2 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.GONE
                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                4 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                6 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                8 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.GONE
                }
                10 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.VISIBLE
                    viewHolder.kbps5.text = mass_link_parametr[8]
                    viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
                }
            }

            viewHolder.kbps4.text = Main.text

            viewHolder.link = mass_link_parametr[7]
        }
        viewHolder.kbps5.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            Main.text = SpannableString(viewHolder.kbps5.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, viewHolder.kbps5.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(StyleSpan(Typeface.BOLD), 0, viewHolder.kbps5.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            //сбросим другие кнопки
            when (mass_link_parametr.size) {
                2 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.GONE
                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                4 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.GONE
                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                6 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.GONE
                    viewHolder.kbps5.visibility = View.GONE
                }
                8 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.GONE
                }
                10 -> {
                    viewHolder.kbps1.visibility = View.VISIBLE
                    viewHolder.kbps1.text = mass_link_parametr[0]
                    viewHolder.kbps1.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps2.visibility = View.VISIBLE
                    viewHolder.kbps2.text = mass_link_parametr[2]
                    viewHolder.kbps2.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps3.visibility = View.VISIBLE
                    viewHolder.kbps3.text = mass_link_parametr[4]
                    viewHolder.kbps3.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps4.visibility = View.VISIBLE
                    viewHolder.kbps4.text = mass_link_parametr[6]
                    viewHolder.kbps4.setTextColor(Main.COLOR_TEXT)

                    viewHolder.kbps5.visibility = View.VISIBLE
                    viewHolder.kbps5.setTextColor(Main.COLOR_TEXT)
                }
            }

            viewHolder.kbps5.text = Main.text

            viewHolder.link = mass_link_parametr[9]
        }

        return super.getView(p, view, parent)
    }

}