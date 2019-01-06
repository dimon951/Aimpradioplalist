package dmitriy.deomin.aimpradioplalist

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.support.v4.app.*
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File
import java.util.ArrayList

class Main : FragmentActivity() {

    //тут куча всего что может использоваться в любом классе проекта
    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //кодировка файла плейлиста
        val File_Text_Code: String = "UTF8"

        //ссылка на аимп
        val LINK_DOWLOAD_AIMP = "http://www.aimp.ru/files/android/aimp_2.85.718.apk"

        //текст в пустом плейлисте(много где требуется)
        val PUSTO: String = "Плейлист пуст.\n"

        //название файла моего плейлиста
        val MY_PLALIST = Environment.getExternalStorageDirectory().toString() + "/aimp_radio/my_plalist.m3u"


        //шрифт
        var face: Typeface = Typeface.DEFAULT
        //сохранялка
        lateinit var mSettings: SharedPreferences // сохранялка
        //реклама
        lateinit var mAdView: AdView

        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0

        //сохранялки
        //----------------------------
        fun save_value(Key: String, Value: String) { //сохранение строки
            val editor = mSettings.edit()
            editor.putString(Key, Value)
            editor.apply()
        }

        fun save_read(key_save: String): String {  // чтение настройки
            return if (mSettings.contains(key_save)) {
                mSettings.getString(key_save, "")
            } else ""
        }

        fun save_value_int(Key: String, Value: Int) { //сохранение строки
            val editor = mSettings.edit()
            editor.putInt(Key, Value)
            editor.apply()
        }

        fun save_read_int(key_save: String): Int {  // чтение настройки
            return if (mSettings.contains(key_save)) {
                mSettings.getInt(key_save, 0)
            } else 0
        }
        //-------------------

        //проверка есть ли приложение
        fun install_app(app: String): Boolean {
            val pm = Main.context.packageManager
            var pi: PackageInfo? = null
            try {
                pi = pm.getPackageInfo(app, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return pi != null
        }

        fun setup_aimp(potok: String, file: String) {

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.dialog_no_aimp, null)
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()

            val dw_aimp_market = content.findViewById<Button>(R.id.button_dialog_dowload_aimp_market)
            val dw_aimp_link = content.findViewById<Button>(R.id.button_dialog_dowload_aimp_link)
            val open_sys = content.findViewById<Button>(R.id.button_dialog_open_sistem)

            //если есть магазин покажем и установку через него
            if (install_app("com.google.android.gms")) {
                dw_aimp_market.visibility = View.VISIBLE
            } else {
                dw_aimp_market.visibility = View.GONE
            }

            dw_aimp_market.onClick {
                dw_aimp_market.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse("market://details?id=com.aimp.player")
            }

            dw_aimp_link.onClick {
                dw_aimp_link.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            open_sys.onClick {
                open_sys.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

                //передаётся один поток то создадим файл и откроем его иначе передаётся уже созданый файл
                if (potok.isNotEmpty()) {
                    val name = file.replace("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/", "")

                    //сохраним  временый файл сслку
                    val file_function = File_function()
                    file_function.Save_temp_file(name, potok)
                }

                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse(file), "audio/mpegurl")
                //проверим есть чем открыть или нет
                if (i.resolveActivity(Main.context.packageManager) != null) {
                    context.startActivity(i)
                } else {
                    context.toast("Системе не удалось ( ")
                }
            }
        }

        //даёт рандомну положительную фразу
        fun rnd_ok(): String {
            val mas = context.resources.getStringArray(R.array.list_ok)
            val i = rnd_int(0, mas.size - 1)
            return if (i < mas.size) {
                mas[i]
            } else {
                mas[0]
            }

        }

        fun rnd_int(min: Int, max: Int): Int {
            var Max = max
            Max -= min
            return (Math.random() * ++Max).toInt() + min
        }
        //


        //открыть в аимпе ,если передаётся одна станция то создадим файл с ней и откроем его в аимпе
        //если просто имя то значит это уже сохранёный файл ,откроем сразу по имени-адресу с припиской "file://"
        @SuppressLint("WrongConstant")
        fun play_aimp(name: String, url: String) {

            if (url != "") {
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
                                    i.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u"), "audio/mpegurl")
                                    i.flags = 0x3000000
                                    context.startActivity(i)

                                } else {
                                    //иначе предложим системе открыть или установить аимп
                                    Main.setup_aimp(url,
                                            "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u")
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
                file_function.Save_temp_file(name + ".m3u",
                        "#EXTM3U"
                                + "\n"
                                + "#EXTINF:-1," + name
                                + "\n"
                                + url)

            } else {
                //проверим что файл есть
                val f_old = File(name)
                if (f_old.exists()) {

                    //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется

                    //покажем оконо в котором нужно будет ввести имя
                    val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
                    val content = LayoutInflater.from(context).inflate(R.layout.name_save_file, null)
                    builder.setView(content)
                    val alertDialog = builder.create()
                    alertDialog.show()

                    val vname = content.findViewById<EditText>(R.id.edit_new_name)
                    vname.typeface = Main.face
                    vname.textColor = Main.COLOR_TEXT
                    vname.setText(f_old.name.replace(".m3u", ""))
                    vname.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {}
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                            val n = File(f_old.parent + "/" + text + ".m3u")
                            if (n.exists()) {
                                //если такой файл есть будем подкрашивать красным
                                vname.textColor = Color.RED
                            } else {
                                vname.textColor = Main.COLOR_TEXT
                            }
                        }
                    })

                    val save_buttten = content.findViewById<Button>(R.id.button_save)
                    save_buttten.onClick {
                        save_buttten.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                        //----
                        if (vname.text.toString().isEmpty()) {
                            context.toast("Введите имя")
                        } else {

                            val f_new = File(f_old.parent + "/" + vname.text + ".m3u")
                            val otvet: File
                            otvet = if (f_new.name == f_old.name) {
                                f_old
                            } else {
                                f_old.copyTo(f_new, true)
                            }

                            if (otvet.isFile) {
                                //если переименовалось откроем его
                                //проверим есть ли аимп
                                if (Main.install_app("com.aimp.player")) {
                                    //откроем файл с сылкой в плеере
                                    val cm = ComponentName(
                                            "com.aimp.player",
                                            "com.aimp.player.views.MainActivity.MainActivity")

                                    val i = Intent()
                                    i.component = cm

                                    i.action = Intent.ACTION_VIEW
                                    i.setDataAndType(Uri.parse("file://" + f_new.absolutePath), "audio/mpegurl")
                                    i.flags = 0x3000000

                                    context.startActivity(i)

                                } else {
                                    //иначе предложим системе открыть или установить аимп
                                    Main.setup_aimp(url, "file://" + f_new.absolutePath)
                                }

                            } else {
                                context.toast("Не получилось переименовать")
                            }


                            //закроем окошко
                            alertDialog.cancel()
                        }
                    }


                } else {
                    context.toast("Куда-то пропал файл (")
                }
            }

        }

        //тоже если урл не пустой сохраним файл с сылкой и попытаемся открыть в системе
        //иначе проверяем файл и пробуем его открыть
        fun play_system(name: String, url: String) {

            if (url != "") {
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
                file_function.Save_temp_file("$name.m3u", url)
            } else {


                //проверим что файл есть
                val f_old = File(name)
                if (f_old.exists()) {

                    //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется

                    //покажем оконо в котором нужно будет ввести имя
                    val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
                    val content = LayoutInflater.from(context).inflate(R.layout.name_save_file, null)
                    builder.setView(content)
                    val alertDialog = builder.create()
                    alertDialog.show()

                    val vname = content.findViewById<EditText>(R.id.edit_new_name)
                    vname.typeface = Main.face
                    vname.textColor = Main.COLOR_TEXT
                    vname.setText(f_old.name.replace(".m3u", ""))
                    vname.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {}
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                            val n = File(f_old.parent + "/" + text + ".m3u")
                            if (n.exists()) {
                                //если такой файл есть будем подкрашивать красным
                                vname.textColor = Color.RED
                            } else {
                                vname.textColor = Main.COLOR_TEXT
                            }
                        }
                    })

                    val save_buttten = content.findViewById<Button>(R.id.button_save)
                    save_buttten.onClick {
                        save_buttten.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
                        //----
                        if (vname.text.toString().isEmpty()) {
                            context.toast("Введите имя")
                        } else {

                            //переименовываем
                            val f_new = File(f_old.parent + "/" + vname.text + ".m3u")
                            val otvet: File
                            otvet = if (f_new.name == f_old.name) {
                                f_old
                            } else {
                                f_old.copyTo(f_new, true)
                            }
                            if (otvet.isFile) {
                                //если переименовалось откроем его
                                val i = Intent(android.content.Intent.ACTION_VIEW)
                                i.setDataAndType(Uri.parse("file://" + f_new.absolutePath), "audio/mpegurl")
                                //проверим есть чем открыть или нет
                                if (i.resolveActivity(Main.context.packageManager) != null) {
                                    context.startActivity(i)
                                } else {
                                    context.toast("Системе не удалось ( ")
                                }

                            } else {
                                context.toast("Не получилось переименовать")
                            }


                            //закроем окошко
                            alertDialog.cancel()
                        }
                    }


                } else {
                    context.toast("Куда-то пропал файл (")
                }

            }
        }

        //добавить в мой плейлист
        fun add_myplalist(name: String, url: String) {
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
            file_function.Add_may_plalist_stansiy(url, name)
        }


        @JvmStatic
        fun EbuchieRazreshenia() {

            //посмотрим есть ли разрешения
            // 0 есть
            // -1 нет
            //для интернета вроде всегда есть , спрашивает только для записи , автоматом и на чтение ставится
            //поставлю на все накройняк
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
            val permissionFileR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionFileW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionCheck == 0 && permissionFileR == 0 && permissionFileW == 0) {
            } else {
                //----------------------
                KotlinPermissions.with(context as FragmentActivity)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET)
                        .onAccepted {
                            //пошлём сигнал пусть обновится
                            val i = Intent("Main_update")
                            i.putExtra("signal", "update")
                            context.sendBroadcast(i)
                        }
                        .ask()
                //------------------------
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //эта штука нужна чтобы выключить нововведение чертей, и сократить 100500 строк ненужного кода
        //It will ignore URI exposure  (оставляет "file://" как я понял )
        //---------------------------------------------------------------------------------------------
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        //-----------------------------------------------------------------------------------------
        context = this

        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //сохранялка
        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE)


        face = Typeface.createFromAsset(assets, if (save_read("fonts") == "") "fonts/Tweed.ttf" else save_read("fonts"))
        //ставим цвет фона(тема)
        //--------------------------------------------------------------------
        COLOR_FON = if (save_read_int("color_fon") == 0) {
            Color.DKGRAY
        } else {
            save_read_int("color_fon")
        }
        //ставим цвет постов
        COLOR_ITEM = if (save_read_int("color_post1") == 0) {
            resources.getColor(R.color.green)
        } else {
            save_read_int("color_post1")
        }
        //ставим цвеи текста
        COLOR_TEXT = if (save_read_int("color_text") == 0) {
            Color.BLACK
        } else {
            save_read_int("color_text")
        }
        //------------------------------------------------------------------------------


        fon_main.setBackgroundColor(COLOR_FON)


        val mImageIds: IntArray = intArrayOf(R.drawable.titl_text1, R.drawable.titl_text2, R.drawable.titl_text3)
        val imageSwitcher: ImageSwitcher = findViewById(R.id.imageSwitcher)
        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
            myView
        }
        imageSwitcher.setImageResource(mImageIds[1])

        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.offscreenPageLimit = 3
        val myadapter = Myadapter(supportFragmentManager)
        viewPager.adapter = myadapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                //пошлём сигнал пусть мой плейлист обновится
                val i = Intent("Main_update")
                i.putExtra("signal", position.toString())
                context.sendBroadcast(i)
            }
        })


        //реклама
        //-------------------------------------------------------------------------
        MobileAds.initialize(this, "ca-app-pub-7908895047124036~7402987509")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        //--------------------------------------------------------------------------
        //если нажмут кнопку доната в о проге то отключим показ рекламы
        if (save_read("reklama_pokaz") == "of") {
            mAdView.visibility = View.GONE
        } else {
            mAdView.visibility = View.VISIBLE
        }

        //анимация на кнопках*****************************************.
//тут почемуто глючит текст на кнопках
        //перерисуем
        vse_radio.setTextColor(COLOR_TEXT)
        vse_radio.setTypeface(face)
        popularnoe.setTextColor(COLOR_TEXT)
        popularnoe.setTypeface(face)
        moy_plalist.setTextColor(COLOR_TEXT)
        moy_plalist.setTypeface(face)


        vse_radio.text = vse_radio.text.toString() + "(" + resources.getStringArray(R.array.vse_radio).size.toString() + ")"
        vse_radio.onClick {
            vse_radio.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
            viewPager.currentItem = 0
        }

        popularnoe.onClick {
            popularnoe.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
            viewPager.currentItem = 1
        }

        moy_plalist.onClick {
            moy_plalist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
            viewPager.currentItem = 2
        }
        //****************************************************************


        //пролистаем на вип радио
        viewPager.currentItem = 1


        //будем слушать эфир постоянно если че обновим
        //-------------------------------------------------------------------------------------
        //фильтр для нашего сигнала
        val intentFilter = IntentFilter()
        intentFilter.addAction("Main_update")

        //приёмник  сигналов
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                if (intent.action == "Main_update") {
                    //получим данные
                    val s = intent.getStringExtra("signal")

                    when (s) {
                        //меняем кота
                        "0", "1", "2" -> {
                            when (s.toInt()) {
                                0 -> {
                                    vse_radio.setBackgroundColor(COLOR_ITEM)
                                    vse_radio.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                                    imageSwitcher.setImageResource(mImageIds[0])

                                    popularnoe.setBackgroundColor(COLOR_FON)
                                    moy_plalist.setBackgroundColor(COLOR_FON)
                                }
                                1 -> {
                                    popularnoe.setBackgroundColor(COLOR_ITEM)
                                    popularnoe.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                                    imageSwitcher.setImageResource(mImageIds[1])

                                    vse_radio.setBackgroundColor(COLOR_FON)
                                    moy_plalist.setBackgroundColor(COLOR_FON)
                                }
                                2 -> {
                                    moy_plalist.setBackgroundColor(COLOR_ITEM)
                                    moy_plalist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                                    imageSwitcher.setImageResource(mImageIds[2])

                                    vse_radio.setBackgroundColor(COLOR_FON)
                                    popularnoe.setBackgroundColor(COLOR_FON)
                                }
                            }
                        }
                        //обновляем пайджер
                        "update" -> {
                            //обновим
                            myadapter.notifyDataSetChanged()
                            viewPager.adapter = myadapter
                            viewPager.currentItem = 1
                        }
                        "update_color" -> {
                            fon_main.setBackgroundColor(COLOR_FON)
                        }
                    }
                }
            }
        }
        //регистрируем приёмник
        context.registerReceiver(broadcastReceiver, intentFilter)
        //-------------------------------------------------------------------------------


        //получим ебучие разрешения , если не дали их еще
        EbuchieRazreshenia()

    }

    fun Menu_progi(view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
        view.startAnimation(anim)

        val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
        val content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null)
        builder.setView(content)

        val alertDialog = builder.create()
        alertDialog.show()

        val b_a = content.findViewById<Button>(R.id.button_abaut)
        b_a.setTextColor(COLOR_TEXT)
        b_a.typeface = face
        b_a.onClick {
            b_a.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            startActivity<Abaut>()
            alertDialog.cancel()
        }

        val b_s = content.findViewById<Button>(R.id.button_setting)
        b_s.setTextColor(COLOR_TEXT)
        b_s.typeface = face
        b_s.onClick {
            b_s.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            startActivity<Setting>()
            alertDialog.cancel()
        }

        val b_f = content.findViewById<Button>(R.id.button_edit_fonts)
        b_f.setTextColor(COLOR_TEXT)
        b_f.typeface = face
        b_f.onClick {
            b_f.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            startActivity<Fonts_vibor>()
            alertDialog.cancel()
        }

    }

    //заполняем наш скролер
    class Myadapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return Vse_radio()
                1 -> return Pop_radio()
                2 -> return Moy_plalist()
            }
            return null
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
