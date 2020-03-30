package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import androidx.viewpager.widget.ViewPager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import dmitriy.deomin.aimpradioplalist.`fun`.*
import dmitriy.deomin.aimpradioplalist.`fun`.menu.menu_main
import dmitriy.deomin.aimpradioplalist.custom.*
import kotlinx.android.synthetic.main.main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.io.File

class Main : FragmentActivity() {


    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val File_Text_Code: String = "UTF-8"//кодировка файла плейлиста

        const val LINK_DOWLOAD_AIMP = "https://www.aimp.ru/?do=download.file&id=13" //ссылка на аимп

        const val PUSTO: String = "Плейлист пуст" //текст в пустом плейлисте(много где требуется)

        const val SIZE_LIST_LINE = 12//количество строк в моём плейлисте при котором будет показана полоа прокрутки

        val ROOT = Environment.getExternalStorageDirectory().toString() + "/aimp_radio/"

        val MY_PLALIST = ROOT + "my_plalist.m3u" //название файла моего плейлиста

        const val F_THEM_list = "theme.txt" //название файла темы и путь его

        const val HISTORY_LINK = "history_url.txt"//название файла истории ввода сылок на плейлисты

        const val SIZE_LIST_THEM_DEFALT = 7//количество стандартных тем

        const val SIZE_WIDCH_SCROLL = 50//толщина полосы прокрутки

        const val SIZEFILETHEME = 2000//размер в байтах при который не учитывать для отображения размера кеша

        var NAME_USER = ""//Имя пользователя

        var ID_USER = ""  //ид пользователя

        var face: Typeface = Typeface.DEFAULT//шрифт

        lateinit var mSettings: SharedPreferences // сохранялка
        var COLOR_FON: Int = 0
        var COLOR_ITEM: Int = 0
        var COLOR_TEXT: Int = 0
        var COLOR_TEXTcontext: Int = 0
        var COLOR_SELEKT: Int = 0

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
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //сохранялка
        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE)

        //данные пользователя
        load_user_data()

        face = if (save_read("fonts") == "system") {
            Typeface.DEFAULT
        } else {
            Typeface.createFromAsset(assets, if (save_read("fonts") == "") "fonts/Tweed.ttf" else save_read("fonts"))
        }


        load_color_in_KONSTANTS()

        //ставим цвет фона(тема)
        fon_main.setBackgroundColor(COLOR_FON)


        val mImageIds: IntArray = intArrayOf(R.drawable.titl_text1, R.drawable.titl_text2, R.drawable.titl_text3, R.drawable.titl_text4)
        val imageSwitcher: ImageSwitcher = this.findViewById(R.id.imageSwitcher)
        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
            myView
        }
        imageSwitcher.setImageResource(mImageIds[1])
        imageSwitcher.onClick {
            menu_main(imageSwitcher)
        }


        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.offscreenPageLimit = 4
        val adapter = Adapter_main_viewpager(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                //пошлём сигнал пусть мой плейлист обновится
                signal("Main_update").putExtra("signal", position.toString()).send(context)
            }
        })


        //реклама
        reklama(this.contentView!!)


        //анимация на кнопках*****************************************.
        //тут почемуто глючит текст на кнопках
        //перерисуем
        vse_radio.setTextColor(COLOR_TEXT)
        vse_radio.typeface = face
        popularnoe.setTextColor(COLOR_TEXT)
        popularnoe.typeface = face
        moy_plalist.setTextColor(COLOR_TEXT)
        moy_plalist.typeface = face
        online_plalist.setTextColor(COLOR_TEXT)
        online_plalist.typeface = face


        val size_vse_list = resources.getStringArray(R.array.vse_radio).size.toString()
        //Сначала будем ставить общее количество всего радио
        vse_radio.text = "Все радио $size_vse_list"

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

            var name_save_file_vse_radio = "Всё радио"

            //если в поиске есть текст то подставм его
            if(Vse_radio.find_text.isNotEmpty())name_save_file_vse_radio = Vse_radio.find_text

            name.setText(name_save_file_vse_radio)

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
                when (it.getStringExtra("anim")) {
                    "anim_of" ->{
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
            signal("save_all_popularnoe").send(context)
        }

        moy_plalist.onClick {
            viewPager.currentItem = 2
        }
        online_plalist.onClick {
            viewPager.currentItem = 3
        }
        //****************************************************************

        //скроем кнопки , покажем анимацию загрузки
        //************************************************
        progress_vse_radio.visibility = View.VISIBLE
        vse_radio.visibility = View.GONE
        //**************************************************

        //посмотрим есть ли ранее сохраненая позиция пейджера(умноженая на 10)
        // и если есть перейдём , иначе по умолчанию откроем вип радио
        viewPager.currentItem = konvert_read_save_pos(save_read_int("page_aktiv"))

        //будем слушать эфир постоянно если че обновим
        //-------------------------------------------------------------------------------------
        Slot(context, "Main_update").onRun {
            //получим данные
            when (val s = it.getStringExtra("signal")) {
                //меняем кота
                "0", "1", "2", "3" -> {
                    when (s.toInt()) {
                        0 -> {
                            vse_radio.setBackgroundColor(COLOR_ITEM)
                            vse_radio.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                            imageSwitcher.setImageResource(mImageIds[0])
                            save_value_int("page_aktiv", 10)

                            popularnoe.setBackgroundColor(COLOR_FON)
                            moy_plalist.setBackgroundColor(COLOR_FON)
                            online_plalist.setBackgroundColor(COLOR_FON)
                        }
                        1 -> {
                            popularnoe.setBackgroundColor(COLOR_ITEM)
                            popularnoe.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                            imageSwitcher.setImageResource(mImageIds[1])
                            save_value_int("page_aktiv", 100)

                            vse_radio.setBackgroundColor(COLOR_FON)
                            moy_plalist.setBackgroundColor(COLOR_FON)
                            online_plalist.setBackgroundColor(COLOR_FON)
                        }
                        2 -> {
                            moy_plalist.setBackgroundColor(COLOR_ITEM)
                            moy_plalist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                            imageSwitcher.setImageResource(mImageIds[2])
                            save_value_int("page_aktiv", 200)

                            vse_radio.setBackgroundColor(COLOR_FON)
                            popularnoe.setBackgroundColor(COLOR_FON)
                            online_plalist.setBackgroundColor(COLOR_FON)
                        }
                        3 -> {
                            online_plalist.setBackgroundColor(COLOR_ITEM)
                            online_plalist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale))
                            imageSwitcher.setImageResource(mImageIds[3])
                            save_value_int("page_aktiv", 300)

                            vse_radio.setBackgroundColor(COLOR_FON)
                            popularnoe.setBackgroundColor(COLOR_FON)
                            moy_plalist.setBackgroundColor(COLOR_FON)
                        }
                    }
                }
                //обновляем пайджер
                "update" -> {
                    //обновим
                    adapter.notifyDataSetChanged()
                    viewPager.adapter = adapter
                    viewPager.currentItem = konvert_read_save_pos(save_read_int("page_aktiv"))
                }
                "update_color" -> {
                    fon_main.setBackgroundColor(COLOR_FON)
                    vse_radio.setTextColor(COLOR_TEXT)
                    popularnoe.setTextColor(COLOR_TEXT)
                    moy_plalist.setTextColor(COLOR_TEXT)

                    adapter.notifyDataSetChanged()
                    viewPager.adapter = adapter
                    viewPager.currentItem = konvert_read_save_pos(save_read_int("page_aktiv"))
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
                "start_anim_online_plalist" -> {
                    online_plalist.visibility = View.GONE
                    progress_online_plalist.visibility = View.VISIBLE
                }
                "stop_anim_online_plalist" -> {
                    online_plalist.visibility = View.VISIBLE
                    progress_online_plalist.visibility = View.GONE
                }
                "move2" -> viewPager.setCurrentItem(2)
                "send_mp3" -> shareAudioFile(it.getStringExtra("pach_mp3_file"))
            }
        }

        //будем слушать изменение списка всего радио и рисовать на кнопке общее/текущее количество
        Slot(context, "vse_radio_list_size").onRun {
            val size = it.getStringExtra("size")
            if (size_vse_list != size) {
                vse_radio.text = "Все радио $size"
            } else {
                vse_radio.text = "Все радио $size_vse_list"
            }
        }

        //--------------------------------------------------------------------------------------


        //получим ебучие разрешения , если не дали их еще
        EbuchieRazreshenia()

        //пошлём првый раз сигнал пусть все отработает
        signal("Main_update").putExtra("signal", konvert_read_save_pos(save_read_int("page_aktiv")).toString()).send(context)

        //при первом запуске программы покажем окошко с изменениями один раз
        // newUpdate()
    }


    fun shareAudioFile(audioFile: String) {
        /*
       manifest
       <provider
       android:name="androidx.core.content.FileProvider"
       android:authorities="${applicationId}.fileprovider"
       android:exported="false"
       android:grantUriPermissions="true">

       <meta-data
       android:name="android.support.FILE_PROVIDER_PATHS"
       android:resource="@xml/file_paths"/>
       </provider>

       file_paths.xml
       <?xml version="1.0" encoding="utf-8"?>
       <paths xmlns:android="http://schemas.android.com/apk/res/android">
           <external-path
               name="external_files"
               path="." />
       </paths>
        */
        val uri = FileProvider.getUriForFile(context, "dmitriy.deomin.aimpradioplalist.fileprovider", File(audioFile))

        val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
                .setType("audio/mp3")
                .setStream(uri)
                .intent

        startActivity(Intent.createChooser(shareIntent, "Выберите как поделится файлом"))
    }

}
