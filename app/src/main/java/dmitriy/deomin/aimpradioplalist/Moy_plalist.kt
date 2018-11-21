package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.support.v4.toast

import java.util.ArrayList
import java.util.HashMap
import android.content.Intent





class Moy_plalist : Fragment(), AdapterView.OnItemLongClickListener {



    lateinit var file_function: File_function

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.my_plalist, null)
        val context: Context = container!!.context

        val STANCIA = "stancia"

        val listView: ListView = v.findViewById<View>(R.id.listvew_my_plalist) as ListView

        file_function= File_function()

        val mas_radio = file_function.My_plalist()
        val data = ArrayList<Map<String, Any>>(mas_radio.size)

        var m: MutableMap<String, Any>

        for (i in mas_radio.indices) {
            m = HashMap()
            m[STANCIA] = mas_radio[i]
            data.add(m)
        }

        // массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf(STANCIA)
        // массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(R.id.textView)

        val adapter_vse_radio = Adapter_vse_radio(context, data, R.layout.delegat_vse_radio_list, from, to)
        listView.adapter = adapter_vse_radio


        //Слушаем кнопки

        (v.findViewById<View>(R.id.button_delete) as Button).setOnClickListener {
            if (file_function.My_plalist()[0] != "Плейлист пуст") {

                val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
                val content = LayoutInflater.from(context).inflate(R.layout.custon_dialog_delete_plalist, null)
                builder.setView(content)

                val alertDialog = builder.create()
                alertDialog.show()

                val b_d_D = content.findViewById<View>(R.id.button_dialog_delete) as Button
                b_d_D.setTextColor(Main.COLOR_TEXT)
                b_d_D.typeface = Main.face
                b_d_D.setOnClickListener { v ->
                    val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                    v.startAnimation(anim)
                    alertDialog.dismiss()
                    Main.number_page = 2
                    file_function.Delet_my_plalist()
                    Main.myadapter.notifyDataSetChanged()
                    Main.viewPager.adapter = Main.myadapter
                    Main.viewPager.currentItem = Main.number_page
                }
                val b_d_N = content.findViewById<View>(R.id.button_dialog_no) as Button
                b_d_N.setTextColor(Main.COLOR_TEXT)
                b_d_N.typeface = Main.face
                b_d_N.setOnClickListener { v ->
                    val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                    v.startAnimation(anim)
                    alertDialog.dismiss()
                }
            } else {
               toast("Плейлист пуст")
            }
        }


        (v.findViewById<View>(R.id.button_add_url) as Button).setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.add_url_user, null)
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()

            (content.findViewById<View>(R.id.textView_logo_add) as TextView).typeface = Main.face
            val edit = content.findViewById<View>(R.id.editText_add_url) as EditText
            edit.typeface = Main.face

            val paste = content.findViewById<View>(R.id.button_paste_url_add) as Button
            paste.typeface = Main.face
            paste.setOnClickListener { view1 ->
                view1.startAnimation(anim)
                edit.setText(getText(context))
            }

            val add = content.findViewById<View>(R.id.button_add_url) as Button
            add.typeface = Main.face
            add.setOnClickListener { vie ->
                vie.startAnimation(anim)

                //проверим на пустоту
                if (edit.text.toString().length > 0) {

                    Main.number_page = 2

                    //фильтр для нашего сигнала
                    val intentFilter = IntentFilter()
                    intentFilter.addAction("File_created")

                    //приёмник  сигналов
                    val broadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            if (intent.action == "File_created") {
                                //получим данные
                                val s = intent.getStringExtra("update")
                                if (s == "zaebis") {
                                    //  Toast.makeText(context,"Готово",Toast.LENGTH_SHORT).show();
                                    alertDialog.cancel()
                                    //обновим старницу
                                    Main.myadapter.notifyDataSetChanged()
                                    Main.viewPager.adapter = Main.myadapter
                                    Main.viewPager.currentItem = Main.number_page
                                } else {
                                    Toast.makeText(context, "Ошибочка вышла тыкниете еще раз", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    //регистрируем приёмник
                    Main.context.registerReceiver(broadcastReceiver, intentFilter)


                    val file_function = File_function()
                    file_function.Add_may_plalist_stansiy(edit.text.toString())

                    alertDialog.cancel()

                } else {
                    Toast.makeText(context, "Нечего добавлять", Toast.LENGTH_SHORT).show()
                }
            }
        }

        (v.findViewById<View>(R.id.load_file) as Button).setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.load_file, null)
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()


            var file_m3u_custom:String

            val add_fs = content.findViewById<View>(R.id.load_fs) as Button
                add_fs.typeface = Main.face
                add_fs.setOnClickListener { vie ->
                    vie.startAnimation(anim)
                    //----
                    val fileDialog = OpenFileDialog(context)
                            .setFilter(".*\\.m3u")
                            .setOpenDialogListener {
                                if(it!=null) {
                                    file_m3u_custom = it
                                    //проверим на наличие файла и будем действовать дальше
                                    alertDialog.cancel()
                                    toast(file_m3u_custom)
                                }
                            }
                    fileDialog.show()

                    //----
                }




        }





        (v.findViewById<View>(R.id.open_aimp) as Button).setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)
            if (file_function.My_plalist()[0] != "Плейлист пуст") {

                if (Main.install_app("com.aimp.player")) {
                    //откроем файл с сылкой в плеере
                    val cm = ComponentName(
                            "com.aimp.player",
                            "com.aimp.player.views.MainActivity.MainActivity")

                    val intent = Intent()
                    intent.component = cm

                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u"), "audio/mpegurl")
                    intent.flags = 0x3000000

                    startActivity(intent)

                } else {
                    Main.setup_aimp("",
                            "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u")

                }

            } else {
                toast("Плёйлист пуст, добавьте хотябы одну станцию")
            }
        }

        (v.findViewById<View>(R.id.button_otpravit) as Button).setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)

            if (file_function.My_plalist()[0] != "Плейлист пуст") {
                var send = ""

                for (s in file_function.My_plalist()) {
                    send += s + "\n"
                }


                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"

                intent.putExtra(Intent.EXTRA_TEXT, send)
                try {
                    startActivity(Intent.createChooser(intent, "Поделиться через"))
                } catch (ex: ActivityNotFoundException) {
                   toast("Ошибка")
                }

            } else {
               toast("Нечего отпралять, плейлист пуст")
            }
        }

        listView.onItemLongClickListener = this

        return v
    }



    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val selectedItem = parent.getItemAtPosition(position).toString() //получаем строку


        val m = file_function.My_plalist()
        if (m[0] != "Плейлист пуст") {

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete_stancii, null)
            builder.setView(content)

            val alertDialog = builder.create()
            alertDialog.show()


            (content.findViewById<View>(R.id.text_voprosa_del_stncii) as TextView).typeface = Main.face
            (content.findViewById<View>(R.id.text_voprosa_del_stncii) as TextView).text = "Точно удалить: \n" + selectedItem.substring(9, selectedItem.length - 1) + " ?"

            (content.findViewById<View>(R.id.button_dialog_delete) as Button).setTextColor(Main.COLOR_TEXT)
            (content.findViewById<View>(R.id.button_dialog_delete) as Button).typeface = Main.face
            (content.findViewById<View>(R.id.button_dialog_delete) as Button).setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                alertDialog.dismiss()

                val masiv = ArrayList<String>()

                for (s in m) {
                    masiv.add(s)
                }

                //пробуем удалить
                if (masiv.remove(selectedItem.substring(9, selectedItem.length - 1))) {
                    Main.number_page = 2
                    //всё удаляем
                    file_function.Delet_my_plalist()
                    //записываем заново

                    //фильтр для нашего сигнала
                    val intentFilter = IntentFilter()
                    intentFilter.addAction("File_created")

                    //приёмник  сигналов
                    val broadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            if (intent.action == "File_created") {
                                //получим данные
                                val s = intent.getStringExtra("update")
                                if (s == "zaebis") {
                                    //обновим старницу
                                    Main.myadapter.notifyDataSetChanged()
                                    Main.viewPager.adapter = Main.myadapter
                                    Main.viewPager.currentItem = Main.number_page
                                } else {
                                    toast("Ошибочка вышла тыкниете еще раз")
                                }
                            }
                        }
                    }

                    //регистрируем приёмник
                    Main.context.registerReceiver(broadcastReceiver, intentFilter)


                    var url_link = ""
                    for (s in masiv) {
                        url_link += s + "\n"
                    }

                    val file_function = File_function()
                    file_function.Add_may_plalist_stansiy(url_link)
                } else {
                    Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }

            (content.findViewById<View>(R.id.button_dialog_no) as Button).setTextColor(Main.COLOR_TEXT)
            (content.findViewById<View>(R.id.button_dialog_no) as Button).typeface = Main.face
            (content.findViewById<View>(R.id.button_dialog_no) as Button).setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                alertDialog.dismiss()
            }

        } else {
            toast("Плейлист пуст")
        }


        return true
    }

    //чтение
    fun getText(context: Context): String {
        var text: String? = null
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            text = clipboard.text.toString()
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            text = clipboard.text.toString()
        }
        return text
    }
}
