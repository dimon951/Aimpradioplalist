package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast


class Adapter_vse_list(val data: ArrayList<Radio>) : RecyclerView.Adapter<Adapter_vse_list.ViewHolder>(), Filterable {


    private lateinit var context: Context
    private var raduoSearchList: List<Radio>? = data

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                raduoSearchList = if (charString.isEmpty()) {
                    data
                } else {
                    val filteredList = ArrayList<Radio>()
                    for (row in data) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = raduoSearchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                raduoSearchList = filterResults.values as ArrayList<Radio>
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_radio = itemView.findViewById<TextView>(R.id.name_radio)
        val nomer_radio = itemView.findViewById<TextView>(R.id.nomer_radio)
        val url_radio = itemView.findViewById<TextView>(R.id.url_radio)
        val fon = itemView.findViewById<CardView>(R.id.fon_item_radio)
       // val fon2 = itemView.findViewById<LinearLayout>(R.id.fon2)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_radio, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return raduoSearchList!!.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

      //  p0.fon2.setBackgroundColor(Main.COLOR_ITEM)

        //настроим вид тутже
        //-----------------------------------------------
        p0.name_radio.typeface = Main.face
        p0.name_radio.textColor = Main.COLOR_TEXT

        p0.url_radio.typeface = Main.face
        p0.url_radio.textColor = Main.COLOR_TEXT

        p0.nomer_radio.typeface = Main.face
        p0.nomer_radio.textColor = Main.COLOR_TEXT
        //-------------------------------------------------------


        //заполним данными
        val radio: Radio = raduoSearchList!![p1]
        p0.name_radio.text = radio.name
        p0.url_radio.text = radio.url
        p0.nomer_radio.text = (p1 + 1).toString() + ". "


        //обработка нажатий
        p0.itemView.onClick {
            p0.fon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))

           // p0.fon2.setBackgroundColor(Color.DKGRAY)

            p0.name_radio.textColor = Color.DKGRAY
            p0.url_radio.textColor = Color.DKGRAY
            p0.nomer_radio.textColor = Color.DKGRAY

            //сохраняем позицию
            Main.save_value("nomer_stroki", p1.toString())

            val name = radio.name
            val url_link = radio.url

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.menu_vse_radio, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()

            val add_pls = content.findViewById<Button>(R.id.button_add_plalist)
            val open_aimp = content.findViewById<Button>(R.id.button_open_aimp)
            val instal_aimp = content.findViewById<Button>(R.id.button_instal_aimp)
            val instal_aimp2 = content.findViewById<Button>(R.id.button_download_yandex_aimp)


            //если aimp установлен скроем кнопку установить аимп
            if (Main.install_app("com.aimp.player")) {
                instal_aimp.visibility = View.GONE
                instal_aimp2.visibility = View.GONE
                open_aimp.visibility = View.VISIBLE
            } else {
                //если есть магазин покажем и установку через него
                if (Main.install_app("com.google.android.gms")) {
                    instal_aimp.visibility = View.VISIBLE
                } else {
                    instal_aimp.visibility = View.GONE
                }

                //скачать по ссылке будем показывать всегда
                instal_aimp2.visibility = View.VISIBLE
                open_aimp.visibility = View.GONE

            }

            val text_name_i_url = content.findViewById<TextView>(R.id.textView_vse_radio)
            text_name_i_url.text = name + "\n" + url_link
            text_name_i_url.onClick {
                text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                putText(url_link, context)
                context.toast("url скопирован в буфер")
            }



            open_aimp.onLongClick {
                open_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //когда прийдёт сигнал что сохранилось передадим этот файл в аим для открытия
                //-----------------------------------------------------------------------
                //приёмник  сигналов
                // фильтр для приёмника
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(c: Context, intent: Intent) {
                        if (intent.action == "File_created") {
                            //получим данные
                            val s = intent.getStringExtra("update")
                            if (s == "zaebis") {

                                val i = Intent(android.content.Intent.ACTION_VIEW)
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                                //проверим есть чем открыть или нет
                                if (i.resolveActivity(Main.context.packageManager) != null) {
                                    context.startActivity(i)
                                } else {
                                    context.toast("Системе не удалось ( ")
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
                //------------------------------------------------------------------------------------------------

                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u", url_link)

            }

            instal_aimp.onClick {
                instal_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse("market://details?id=com.aimp.player")
            }

            instal_aimp2.onClick {
                instal_aimp2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            add_pls.onClick {
                add_pls.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //фильтр для нашего сигнала
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //приёмник  сигналов
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
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
                file_function.Add_may_plalist_stansiy(url_link, name)

                alertDialog.cancel()
            }


            open_aimp.onClick {
                open_aimp.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //когда прийдёт сигнал что сохранилось передадим этот файл в аим для открытия
                //-----------------------------------------------------------------------
                //приёмник  сигналов
                // фильтр для приёмника
                val intentFilter = IntentFilter()
                intentFilter.addAction("File_created")

                //
                val broadcastReceiver = object : BroadcastReceiver() {
                    @SuppressLint("WrongConstant")
                    override fun onReceive(c: Context, intent: Intent) {
                        if (intent.action == "File_created") {
                            //получим данные
                            val s = intent.getStringExtra("update")
                            if (s == "zaebis") {

                                //откроем файл с сылкой в плеере
                                val cm = ComponentName(
                                        "com.aimp.player",
                                        "com.aimp.player.views.MainActivity.MainActivity")

                                val i = Intent()
                                i.component = cm

                                i.action = Intent.ACTION_VIEW
                                i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                                i.flags = 0x3000000

                                context.startActivity(i)

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
                //------------------------------------------------------------------------------------------------


                //сохраним  временый файл ссылку и будем ждать сигнала
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u",
                        "#EXTM3U"
                                + "\n"
                                + "#EXTINF:-1," + "$name.m3u"
                                + "\n"
                                + url_link)


            }


        }
    }

    //запись
    fun putText(text: String, context: Context) {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText(text, text)
            clipboard.primaryClip = clip
        }
    }
}
