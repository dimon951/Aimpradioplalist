package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.text.Spannable
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*

class Main : FragmentActivity() {

    lateinit var imageSwitcher: ImageSwitcher
    lateinit var mImageIds: IntArray

    lateinit var vse_r: Button
    lateinit var popul: Button
    lateinit var moy_pl: Button
    val APP_PREFERENCES = "mysettings" // файл сохранялки

    internal var visi: Boolean = false//true при активном приложении



    companion object {

       @SuppressLint("StaticFieldLeak")
       lateinit var context: Context

       @SuppressLint("StaticFieldLeak")
       lateinit var liner_boss: LinearLayout

        @SuppressLint("StaticFieldLeak")
       lateinit var viewPager: ViewPager

       lateinit var myadapter: Myadapter
        var number_page: Int = 0


        //шрифт
       lateinit  var face: Typeface
        //для текста
       lateinit var text: Spannable
        //сохранялка
       lateinit var mSettings: SharedPreferences // сохранялка

        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0

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

        fun install_app(app: String): Boolean {
            val pm = Main.context.packageManager
            var pi: PackageInfo? = null
            try {
                pi = pm.getPackageInfo(app, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return if (pi == null) {
                false
            } else {
                true
            }
        }

        fun setup_aimp(potok: String, file: String) {

            val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_no_aimp, null)
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()

            val dw_aimp_market = content.findViewById<View>(R.id.button_dialog_dowload_aimp_market) as Button
            val dw_aimp_link = content.findViewById<View>(R.id.button_dialog_dowload_aimp_link) as Button
            val open_sys = content.findViewById<View>(R.id.button_dialog_open_sistem) as Button

            //если есть магазин покажем и установку через него
            if (install_app("com.google.android.gms")) {
                dw_aimp_market.visibility = View.VISIBLE
            } else {
                dw_aimp_market.visibility = View.GONE
            }

            dw_aimp_market.typeface = Main.face
            dw_aimp_market.setTextColor(Main.COLOR_TEXT)
            dw_aimp_market.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=com.aimp.player")
                Main.context.startActivity(intent)
            }


            dw_aimp_link.typeface = Main.face
            dw_aimp_link.setTextColor(Main.COLOR_TEXT)
            dw_aimp_link.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)
                val openlink = Intent(Intent.ACTION_VIEW, Uri.parse("https://yadi.sk/d/QhgzmO7t3GnBb3"))
                context.startActivity(openlink)
            }

            open_sys.typeface = Main.face
            open_sys.setTextColor(Main.COLOR_TEXT)
            open_sys.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                v.startAnimation(anim)

                //передаётся один поток то создадим файл и откроем его иначе передаётся уже созданый файл
                if (potok.length > 0) {
                    val name = file.replace("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/", "")

                    //сохраним  временый файл сслку
                    val file_function = File_function()
                    file_function.Save_temp_file(name, potok)
                }

                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse(file), "audio/mpegurl")
                //проверим есть чем открыть или нет
                if (i.resolveActivity(Main.context.packageManager) != null) {
                    Main.context.startActivity(i)
                } else {
                    Toast.makeText(context, "Системе не удалось ( ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    
    
    
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        context = this
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)


        face = Typeface.createFromAsset(assets, if (save_read("fonts") == "") "fonts/Tweed.ttf" else save_read("fonts"))


        //ставим цвет фона
        if (save_read_int("color_fon") == 0) {
            COLOR_FON = Color.DKGRAY
        } else {
            COLOR_FON = save_read_int("color_fon")
        }
        //ставим цвет постов
        if (save_read_int("color_post1") == 0) {
            COLOR_ITEM = resources.getColor(R.color.green)
        } else {
            COLOR_ITEM = save_read_int("color_post1")
        }
        //ставим цвеи текста
        if (save_read_int("color_text") == 0) {
            COLOR_TEXT = Color.BLACK
        } else {
            COLOR_TEXT = save_read_int("color_text")
        }


        liner_boss = findViewById<View>(R.id.main) as LinearLayout
        liner_boss.setBackgroundColor(COLOR_FON)



        //анимация на кнопках*****************************************.
        val anim = AnimationUtils.loadAnimation(context, R.anim.myscale)
        vse_r = findViewById<View>(R.id.vse_radio) as Button
        vse_r.typeface = face
        vse_r.setTextColor(COLOR_TEXT)
        vse_r.text = vse_r.text.toString() + "(" + resources.getStringArray(R.array.vse_radio).size.toString() + ")"
        vse_r.setOnTouchListener { v, event ->
            v.startAnimation(anim)
            viewPager.currentItem = 0
            false
        }
        popul = findViewById<View>(R.id.popularnoe) as Button
        popul.typeface = face
        popul.setTextColor(COLOR_TEXT)
        popul.setOnTouchListener { v, event ->
            v.startAnimation(anim)
            viewPager.currentItem = 1
            false
        }
        moy_pl = findViewById<View>(R.id.moy_plalist) as Button
        moy_pl.typeface = face
        moy_pl.setTextColor(COLOR_TEXT)
        moy_pl.setOnTouchListener { v, event ->
            v.startAnimation(anim)
            viewPager.currentItem = 2
            false
        }
        //****************************************************************


        mImageIds = intArrayOf(R.drawable.titl_text1, R.drawable.titl_text2, R.drawable.titl_text3)
        imageSwitcher = findViewById<View>(R.id.imageSwitcher) as ImageSwitcher
        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
          //  myView.layoutParams = ViewGroup.LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT)
            myView
        }
        imageSwitcher.setImageResource(mImageIds[1])


        myadapter = Myadapter(supportFragmentManager)
        viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = myadapter
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // номер страницы
                fon_button(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        //пролистаем на вип радио
        viewPager.currentItem = 1

        visi = true  // приложение активно
    }

    fun Menu_progi(view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
        view.startAnimation(anim)


        val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo))
        val content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null)
        builder.setView(content)

        val alertDialog = builder.create()
        alertDialog.show()

        val b_a = content.findViewById<View>(R.id.button_abaut) as Button
        b_a.setTextColor(COLOR_TEXT)
        b_a.typeface = face
        b_a.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)
            val i = Intent(applicationContext, Abaut::class.java)
            startActivity(i)
            alertDialog.cancel()
        }
        val b_s = content.findViewById<View>(R.id.button_setting) as Button
        b_s.setTextColor(COLOR_TEXT)
        b_s.typeface = face
        b_s.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.myalpha)
            v.startAnimation(anim)
            val s = Intent(this@Main, Setting::class.java)
            startActivity(s)
            alertDialog.cancel()
        }
    }

    //заполняем наш скролер
    inner class Myadapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return Vse_radio()
                1 -> return Vip_radio()
                2 -> return Moy_plalist()
            }
            return null
        }

        override fun getCount(): Int {
            return 3
        }
    }

    fun fon_button(button: Int) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.myscale)

        when (button) {
            0 -> {
                vse_r.setBackgroundColor(COLOR_ITEM)
                vse_r.startAnimation(anim)
                imageSwitcher.setImageResource(mImageIds[0])

                popul.setBackgroundColor(COLOR_FON)
                moy_pl.setBackgroundColor(COLOR_FON)
            }
            1 -> {
                popul.setBackgroundColor(COLOR_ITEM)
                popul.startAnimation(anim)
                imageSwitcher.setImageResource(mImageIds[1])

                vse_r.setBackgroundColor(COLOR_FON)
                moy_pl.setBackgroundColor(COLOR_FON)
            }
            2 -> {
                moy_pl.setBackgroundColor(COLOR_ITEM)
                moy_pl.startAnimation(anim)
                imageSwitcher.setImageResource(mImageIds[2])

                vse_r.setBackgroundColor(COLOR_FON)
                popul.setBackgroundColor(COLOR_FON)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        visi = false
    }

    override fun onResume() {
        super.onResume()
        visi = true
    }
    
}
