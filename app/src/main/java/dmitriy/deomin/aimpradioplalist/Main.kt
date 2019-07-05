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
import android.telephony.TelephonyManager
import androidx.core.app.*
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kotlinpermissions.KotlinPermissions
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.io.File

class Main : FragmentActivity() {

    //размер в байтах при который не учитывать для отображения размера кеша
    val SIZEFILETHEME = 1000

    //    //Displayed 2c

    //тут куча всего что может использоваться в любом классе проекта
    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //кодировка файла плейлиста
        const val File_Text_Code: String = "UTF8"

        //ссылка на аимп
        const val LINK_DOWLOAD_AIMP = "https://www.aimp.ru/files/android/aimp_2.90.845.apk"


        //текст в пустом плейлисте(много где требуется)
        const val PUSTO: String = "Плейлист пуст.\n"

        //количество строк в моём плейлисте при котором будет показана полоа прокрутки
        const val SIZE_LIST_LINE = 12

        val ROOT = Environment.getExternalStorageDirectory().toString() + "/aimp_radio/"

        //название файла моего плейлиста
        val MY_PLALIST = ROOT + "my_plalist.m3u"

        //название файла темы и путь его
        const val F_THEM_list = "theme.txt"

        //название файла истории ввода сылок на плейлисты
        const val HISTORY_LINK = "history_url.txt"

        //количество стандартных тем
        const val SIZE_LIST_THEM_DEFALT = 7

        //толщина полосы прокрутки
        const val SIZE_WIDCH_SCROLL = 50

        //Имя пользователя
        var NAME_USER = ""

        //ид пользователя
        var ID_USER = ""


        //шрифт
        var face: Typeface = Typeface.DEFAULT
        //сохранялка
        lateinit var mSettings: SharedPreferences // сохранялка
        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0
        var COLOR_TEXTcontext: Int = 0

        var cho_nagimali_poslednee: Int = 0

        //запись в буфер
        fun putText(text: String, context: Context) {
            val sdk = android.os.Build.VERSION.SDK_INT
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = text
            } else {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(text, text)
                clipboard.primaryClip = clip
            }
        }

        //чтение из буфера
        fun getText(c: Context): String {
            val text: String
            val sdk = android.os.Build.VERSION.SDK_INT
            text = if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager?
                clipboard!!.text.toString()
            } else {
                val clipboard = c.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                if (clipboard!!.text == null) {
                    context.toast("Буфер обмена пуст")
                    ""
                } else {
                    clipboard.text.toString()
                }
            }
            return text
        }

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
                context.browse(LINK_DOWLOAD_AIMP)
            }

            open_sys.onClick {

                //передаётся один поток то создадим файл и откроем его иначе передаётся уже созданый файл
                if (potok.isNotEmpty()) {
                    val name = file.replace("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/", "")

                    //сохраним  временый файл сслку
                    val file_function = File_function()
                    file_function.SaveFile(ROOT + name, potok)
                }

                val i = Intent(Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse(file), "audio/mpegurl")
                //проверим есть чем открыть или нет
                if (i.resolveActivity(Main.context.packageManager) != null) {
                    context.startActivity(i)
                } else {
                    context.toast("Системе не удалось ( ")
                }
            }
        }

        //даёт рандомную положительную фразу
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
                        if (install_app("com.aimp.player")) {
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
                            setup_aimp(url,
                                    "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + name + ".m3u")
                        }
                    } else {
                        context.toast(context.getString(R.string.error))
                        //запросим разрешения
                        EbuchieRazreshenia()
                    }

                }
                //сохраним  временый файл ссылку и будем ждать сигнала чтобы открыть в аимп или системе
                val file_function = File_function()
                file_function.SaveFile(ROOT + name + ".m3u",
                        "#EXTM3U"
                                + "\n"
                                + "#EXTINF:-1," + name
                                + "\n"
                                + url)

                //если юрл пуст значит передали список что открыть
            } else {
                //проверим что файл есть
                val f_old = File(name)
                if (f_old.exists()) {
                    //если файл есть предложим переименовать тк хз может он там был изменён и потом потребуется

                    //покажем оконо в котором нужно будет ввести имя
                    val nsf = DialogWindow(context, R.layout.name_save_file)

                    val vname = nsf.view().findViewById<EditText>(R.id.edit_new_name)
                    vname.typeface = face
                    vname.textColor = COLOR_TEXT
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
                                vname.textColor = COLOR_TEXT
                            }
                        }
                    })

                    val save_buttten = nsf.view().findViewById<Button>(R.id.button_save)
                    save_buttten.onClick {
                        //----
                        if (vname.text.toString().isEmpty()) {
                            context.toast("Введите имя")
                        } else {
                            //если нечего не изменилось
                            if (f_old.name.replace(".m3u", "") == vname.text.toString()) {
                                //проверим есть ли аимп и откроем
                                if (install_app("com.aimp.player")) {
                                    //откроем файл с сылкой в плеере
                                    val cm = ComponentName(
                                            "com.aimp.player",
                                            "com.aimp.player.views.MainActivity.MainActivity")

                                    val i = Intent()
                                    i.component = cm

                                    i.action = Intent.ACTION_VIEW
                                    i.setDataAndType(Uri.parse("file://" + f_old.absolutePath), "audio/mpegurl")
                                    i.flags = 0x3000000

                                    context.startActivity(i)
                                } else {
                                    //если аимпа нет попробуем открыть в системе
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.setDataAndType(Uri.parse("file://" + f_old.absolutePath), "audio/mpegurl")
                                    //проверим есть чем открыть или нет
                                    if (i.resolveActivity(Main.context.packageManager) != null) {
                                        context.startActivity(i)
                                    } else {
                                        context.toast("Системе не удалось ( ")
                                    }
                                }
                            } else {
                                val f_new = File(f_old.parent + "/" + vname.text + ".m3u")
                                f_old.renameTo(f_new)
                                //проверим есть ли аимп и откроем
                                if (install_app("com.aimp.player")) {
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
                                    //если аимпа нет попробуем открыть в системе
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.setDataAndType(Uri.parse("file://" + f_new.absolutePath), "audio/mpegurl")
                                    //проверим есть чем открыть или нет
                                    if (i.resolveActivity(Main.context.packageManager) != null) {
                                        context.startActivity(i)
                                    } else {
                                        context.toast("Системе не удалось ( ")
                                    }
                                }
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
                    when (it.getStringExtra("update")) {
                        "zaebis" -> {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.setDataAndType(Uri.parse("file://$ROOT$name.m3u"), "audio/mpegurl")
                            //проверим есть чем открыть или нет
                            if (i.resolveActivity(Main.context.packageManager) != null) {
                                context.startActivity(i)
                            } else {
                                context.toast("Системе не удалось ( ")
                            }
                        }
                        "pizdec" -> {
                            context.toast(context.getString(R.string.error))
                            //запросим разрешения
                            EbuchieRazreshenia()
                        }

                    }
                }

                //сохраним  временый файл сслку
                val file_function = File_function()
                file_function.SaveFile(ROOT + name + ".m3u",
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
                    val msf = DialogWindow(context, R.layout.name_save_file)

                    //покажем оконо в котором нужно будет ввести имя
                    val vname = msf.view().findViewById<EditText>(R.id.edit_new_name)
                    vname.typeface = face
                    vname.textColor = COLOR_TEXT
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
                                vname.textColor = COLOR_TEXT
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
                                val i = Intent(Intent.ACTION_VIEW)
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
                when (it.getStringExtra("update")) {
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
                        EbuchieRazreshenia()
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


        //данные пользователя
        //имя загрузим из сохранялки, а id часть имайла
        NAME_USER = save_read("name_user")
        ID_USER = if (save_read("id_user").isEmpty()) {
            rnd_int(10000,1000000).toString()
        } else {
            save_read("id_user")
        }



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
        val imageSwitcher: ImageSwitcher = this.findViewById(R.id.imageSwitcher)
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
        vse_radio.onLongClick {

            //спросим имя для файла
            //покажем оконо в котором нужно будет ввести имя
            val nsf = DialogWindow(context, R.layout.name_save_file)

            val name = nsf.view().findViewById<EditText>(R.id.edit_new_name)
            name.typeface = face
            name.textColor = COLOR_TEXT
            name.setText("Всё радио")
            (nsf.view().findViewById<Button>(R.id.button_save)).onClick {
                if (name.text.toString().isNotEmpty()) {
                    nsf.close()
                    //запустим анимацию
                    vse_radio.visibility = View.GONE
                    progress_vse_radio.visibility = View.VISIBLE
                    //пошлём сигнал для сохранения
                    signal("save_all_vse_lest").putExtra("name_list", name.text.toString()).send(context)
                } else {
                    context.toast("Введите имя")
                }
            }


            //когда все запишется отключим анимацию
            Slot(context, "File_created_save_vse", false).onRun {
                //получим данные
                when (it.getStringExtra("update")) {
                    "zaebis" -> context.toast(rnd_ok())
                    "pizdec" -> context.toast(Main.context.getString(R.string.error))
                }
                when (it.getStringExtra("anim")) {
                    "anim_of" -> {
                        progress_vse_radio.visibility = View.GONE
                        vse_radio.visibility = View.VISIBLE
                    }
                }
            }
        }

        popularnoe.onClick {
            viewPager.currentItem = 1
        }
        //при долгом нажатии будем весь список популярного радио сохранять
        popularnoe.onLongClick {
            //когда все запишется пошлём сигнал чтобы список обновился
            Slot(context, "File_created_save_vse", false).onRun {
                //получим данные
                when (it.getStringExtra("update")) {
                    //пошлём сигнал пусть мой плейлист обновится
                    "zaebis" -> context.toast("Весь список сохранен в " + ROOT)

                    "pizdec" -> context.toast(Main.context.getString(R.string.error))
                }
            }
            signal("save_all_popularnoe").send(context)
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
            when (val s = it.getStringExtra("signal")) {
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
                "load_stop_vse_radio" -> {
                    progress_vse_radio.visibility = View.GONE
                    vse_radio.visibility = View.VISIBLE
                    signal("update_vse_radio").send(context)
                }
                "start_anim_my_list" -> {
                    moy_plalist.visibility = View.GONE
                    progress_moy_plalist.visibility = View.VISIBLE
                }
                "stop_anim_my_list" -> {
                    progress_moy_plalist.visibility = View.GONE
                    moy_plalist.visibility = View.VISIBLE
                }

            }
        }


        //получим ебучие разрешения , если не дали их еще
        EbuchieRazreshenia()

        //при первом запуске программы покажем окошко с изменениями один раз
        // newUpdate()
    }

    fun newUpdate() {
        val startWindow = DialogWindow(context, R.layout.error_import)
        val t = startWindow.view().findViewById<TextView>(R.id.textView_error_import_podrobno)
        t.text = ""

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

        //кнопка очистить кеш
        //при открытии меню будем показыват размер этого кеша
        val b_c = menu.view().findViewById<Button>(R.id.button_clear_kesh)

        //получим размер
        val file = File(ROOT)
        if (file.exists()) {
            val s = getDirSize(file)
            if (s > SIZEFILETHEME) {
                b_c.text = "Очистить кэш(" + (s / 1024).toString() + " kb" + ")"
            } else {
                b_c.text = "Кэш очищен"
            }
        } else
            b_c.text = "Кэш очищен"

        b_c.onClick {

            if (b_c.text == "Кэш очищен") {

                context.toast("Пусто")

            } else {

                //покажем предупреждающее окошко
                val v_d = DialogWindow(context, R.layout.dialog_delete_plalist)

                v_d.view().findViewById<TextView>(R.id.text_voprosa_del_stncii).text = "Удалить кеш?"

                v_d.view().findViewById<Button>(R.id.button_dialog_delete).onClick {
                    v_d.close()
                    //удаляем все
                    if (file.exists()) {
                        deleteAllFilesFolder(ROOT)
                    }
                    if (file.exists()) {
                        val s = getDirSize(file)
                        if (s > SIZEFILETHEME) {
                            b_c.text = "Очистить кэш(" + (s / 1024).toString() + " kb" + ")"
                        } else {
                            b_c.text = "Кэш очищен"
                        }

                    } else
                        b_c.text = "Кэш очищен"

                }
                v_d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
                    v_d.close()
                }

            }

        }


    }

    fun deleteAllFilesFolder(path: String) {
        for (myFile in File(path).listFiles())
            if (myFile.isFile && myFile.name != "theme.txt" && myFile.name != "history_url.txt") myFile.delete()
    }

    fun getDirSize(dir: File): Long {
        var size: Long = 0
        if (dir.isFile) {
            size = dir.length()
        } else {
            val subFiles = dir.listFiles()
            for (file in subFiles) {
                size += if (file.isFile) {
                    file.length()
                } else {
                    getDirSize(file)
                }
            }
        }
        return size
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
