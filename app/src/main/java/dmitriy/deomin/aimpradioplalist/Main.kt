package dmitriy.deomin.aimpradioplalist

import android.Manifest
import android.annotation.SuppressLint
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
import android.text.TextWatcher
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kotlinpermissions.KotlinPermissions
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import kotlinx.android.synthetic.main.main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File

class Main : FragmentActivity() {

    //    //Displayed 2c

    //тут куча всего что может использоваться в любом классе проекта
    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //кодировка файла плейлиста
        val File_Text_Code: String = "UTF8"

        //ссылка на аимп
        val LINK_DOWLOAD_AIMP = "http://www.aimp.ru/files/android/aimp_2.85.720.apk"

        //текст в пустом плейлисте(много где требуется)
        val PUSTO: String = "Плейлист пуст.\n"

        val ROOT = Environment.getExternalStorageDirectory().toString() + "/aimp_radio/"

        //название файла моего плейлиста
        val MY_PLALIST = ROOT + "my_plalist.m3u"

        //название файла темы и путь его
        val F_THEM_list = "theme.txt"

        //количество стандартных тем
        val SIZE_LIST_THEM_DEFALT = 7


        //шрифт
        var face: Typeface = Typeface.DEFAULT
        //сохранялка
        lateinit var mSettings: SharedPreferences // сохранялка
        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0
        var COLOR_TEXTcontext: Int = 0

        var cho_nagimali_poslednee: Int = 0

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

            val sa = DialogWindow(context, R.layout.dialog_no_aimp)

            val dw_aimp_market = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_market)
            val dw_aimp_link = sa.view().findViewById<Button>(R.id.button_dialog_dowload_aimp_link)
            val open_sys = sa.view().findViewById<Button>(R.id.button_dialog_open_sistem)

            //если есть магазин покажем и установку через него
            if (install_app("com.google.android.gms")) {
                dw_aimp_market.visibility = View.VISIBLE
            } else {
                dw_aimp_market.visibility = View.GONE
            }

            dw_aimp_market.onClick {
                context.browse("market://details?id=com.aimp.player")
            }

            dw_aimp_link.onClick {
                context.browse(Main.LINK_DOWLOAD_AIMP)
            }

            open_sys.onClick {

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
                Slot(context, "File_created").onRun {
                    //получим данные
                    val s = it.getStringExtra("update")
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

                }
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
                    val nsf = DialogWindow(context, R.layout.name_save_file)

                    val vname = nsf.view().findViewById<EditText>(R.id.edit_new_name)
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

                    val save_buttten = nsf.view().findViewById<Button>(R.id.button_save)
                    save_buttten.onClick {
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
                            nsf.close()
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
                Slot(context, "File_created").onRun {
                    //получим данные
                    val s = it.getStringExtra("update")
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
                }


                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.Save_temp_file("$name.m3u", url)
            } else {


                //проверим что файл есть
                val f_old = File(name)
                if (f_old.exists()) {
                    //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется
                    val msf = DialogWindow(context, R.layout.name_save_file)

                    //покажем оконо в котором нужно будет ввести имя
                    val vname = msf.view().findViewById<EditText>(R.id.edit_new_name)
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

                    val save_buttten = msf.view().findViewById<Button>(R.id.button_save)
                    save_buttten.onClick {
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
                            msf.close()
                        }
                    }


                } else {
                    context.toast("Куда-то пропал файл (")
                }

            }
        }

        //добавить в мой плейлист
        fun add_myplalist(name: String, url: String) {

            Slot(context, "File_created").onRun {
                //получим данные
                val s = it.getStringExtra("update")
                when (s) {
                    "est" -> context.toast("Такая станция уже есть в плейлисте")
                    "zaebis" -> {
                        //пошлём сигнал пусть мой плейлист обновится
                        signal("Data_add")
                                .putExtra("run", true)
                                .putExtra("update", "zaebis")
                                .send(context)
                    }
                    "pizdec" -> {
                        context.toast(context.getString(R.string.error))
                        //запросим разрешения
                        Main.EbuchieRazreshenia()
                    }
                }
            }

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
                //пошлём сигнал пусть мой плейлист обновится
                signal("Main_update")
                        .putExtra("signal", "1")
                        .send(context)
            } else {
                //----------------------
                KotlinPermissions.with(context as FragmentActivity)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET)
                        .onAccepted {
                            //пошлём сигнал пусть обновится
                            signal("Main_update")
                                    .putExtra("signal", "update")
                                    .send(context)
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
        //ставим цвеи текста для контекста
        COLOR_TEXTcontext = if (save_read_int("color_textcontext") == 0) {
            resources.getColor(R.color.textcontext)
        } else {
            save_read_int("color_textcontext")
        }
        //------------------------------------------------------------------------------


        cho_nagimali_poslednee = save_read_int("nomer_stroki_int")
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
                signal("Main_update").putExtra("signal", position.toString()).send(context)
            }
        })


        //реклама
        //-------------------------------------------------------------------------
        MobileAds.initialize(this, "ca-app-pub-7908895047124036~7402987509")
        val mAdView: AdView = findViewById(R.id.adView)
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
        vse_radio.typeface = face
        popularnoe.setTextColor(COLOR_TEXT)
        popularnoe.typeface = face
        moy_plalist.setTextColor(COLOR_TEXT)
        moy_plalist.typeface = face


        val size_vse_list = resources.getStringArray(R.array.vse_radio).size.toString()
        //Сначала будем ставить общее количество всего радио
        vse_radio.text = "Все радио $size_vse_list"

        //будем слушать изменение списка всего радио и рисовать на кнопке общее/текущее количество
        Slot(context, "vse_radio_list_size").onRun {
            val size = it.getStringExtra("size")
            if (size_vse_list != size) {
                vse_radio.text = "Все радио $size\\$size_vse_list"
            } else {
                vse_radio.text = "Все радио $size_vse_list"
            }
        }

        vse_radio.onClick {
            viewPager.currentItem = 0
        }

        popularnoe.onClick {
            viewPager.currentItem = 1
        }
        //при долгом нажатии будем весь список популярного радио сохранять
        popularnoe.onLongClick {
            signal("save_all_popularnoe").send(context)

            //когда все запишется пошлём сигнал чтобы список обновился
            Slot(Main.context, "File_created", false).onRun {
                //получим данные
                val s = it.getStringExtra("update")
                when (s) {
                    //пошлём сигнал пусть мой плейлист обновится
                    "zaebis" -> Main.context.toast("Весь список сохранен в "+ ROOT)

                    "pizdec" -> Main.context.toast(Main.context.getString(R.string.error))
                }
            }
        }

        moy_plalist.onClick {
            viewPager.currentItem = 2
        }
        //****************************************************************

        //скроем кнопки , покажем анимацию загрузки
        //************************************************
        progress_vse_radio.visibility = View.VISIBLE
        vse_radio.visibility = View.GONE
        //**************************************************

        //пролистаем на вип радио
        viewPager.currentItem = 1

        //будем слушать эфир постоянно если че обновим
        //-------------------------------------------------------------------------------------
        Slot(context, "Main_update").onRun {
            //получим данные
            val s = it.getStringExtra("signal")
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
                    vse_radio.setTextColor(COLOR_TEXT)
                    popularnoe.setTextColor(COLOR_TEXT)
                    moy_plalist.setTextColor(COLOR_TEXT)

                    myadapter.notifyDataSetChanged()
                    viewPager.adapter = myadapter
                    viewPager.currentItem = 1
                }
                "load_good_vse_radio" -> {
                    progress_vse_radio.visibility = View.GONE
                    vse_radio.visibility = View.VISIBLE
                    signal("update_vse_radio").send(context)
                }

            }
        }


        //получим ебучие разрешения , если не дали их еще
        EbuchieRazreshenia()
    }

    fun Menu_progi(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))

        val menu = DialogWindow(context, R.layout.menu_progi)

        val b_a = menu.view().findViewById<Button>(R.id.button_abaut)
        b_a.onClick {
            startActivity<Abaut>()
            menu.close()
        }

        val b_s = menu.view().findViewById<Button>(R.id.button_setting)
        b_s.onClick {
            startActivity<Setting>()
            menu.close()
        }

        val b_f = menu.view().findViewById<Button>(R.id.button_edit_fonts)
        b_f.onClick {
            startActivity<Fonts_vibor>()
            menu.close()
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
