package dmitriy.deomin.aimpradioplalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;


public class Main extends FragmentActivity {

    public static Context context;
    public static LinearLayout liner_boss;

    public static ViewPager viewPager;
    public static Myadapter myadapter;
    public static int number_page;

    ImageSwitcher imageSwitcher;
    int[] mImageIds;

    Button vse_r;
    Button popul;
    Button moy_pl;


    //шрифт
    public static Typeface face;
    //для текста
    public static Spannable text;
    //сохранялка
    public static SharedPreferences mSettings; // сохранялка
    public final String APP_PREFERENCES = "mysettings"; // файл сохранялки

    public static int COLOR_FON;
    public static int COLOR_ITEM;
    public static int COLOR_TEXT;

    boolean visi;//true при активном приложении

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        face = Typeface.createFromAsset(getAssets(), ((save_read("fonts").equals("")) ? "fonts/Tweed.ttf" : save_read("fonts")));


        //ставим цвет фона
        if (save_read_int("color_fon") == 0) {
            COLOR_FON = Color.DKGRAY;
        } else {
            COLOR_FON = save_read_int("color_fon");
        }
        //ставим цвет постов
        if (save_read_int("color_post1") == 0) {
            COLOR_ITEM = getResources().getColor(R.color.green);
        } else {
            COLOR_ITEM = save_read_int("color_post1");
        }
        //ставим цвеи текста
        if (save_read_int("color_text") == 0) {
            COLOR_TEXT = Color.BLACK;
        } else {
            COLOR_TEXT = save_read_int("color_text");
        }


        liner_boss = (LinearLayout) findViewById(R.id.main);
        liner_boss.setBackgroundColor(COLOR_FON);


        //анимация на кнопках*****************************************.
        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        vse_r = (Button) findViewById(R.id.vse_radio);
        vse_r.setTypeface(face);
        vse_r.setTextColor(COLOR_TEXT);
        vse_r.setText(vse_r.getText()+"("+String.valueOf(getResources().getStringArray(R.array.vse_radio).length)+")");
        vse_r.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                viewPager.setCurrentItem(0);
                return false;
            }
        });
        popul =(Button) findViewById(R.id.popularnoe);
        popul.setTypeface(face);
        popul.setTextColor(COLOR_TEXT);
        popul.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                viewPager.setCurrentItem(1);
                return false;
            }
        });
        moy_pl = (Button) findViewById(R.id.moy_plalist);
        moy_pl.setTypeface(face);
        moy_pl.setTextColor(COLOR_TEXT);
        moy_pl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                viewPager.setCurrentItem(2);
                return false;
            }
        });
        //****************************************************************


        mImageIds = new int[]{R.drawable.titl_text1,R.drawable.titl_text2,R.drawable.titl_text3};
        imageSwitcher  = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        imageSwitcher.setImageResource(mImageIds[1]);


        myadapter = new Myadapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(myadapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // номер страницы
                fon_button(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //пролистаем на вип радио
        viewPager.setCurrentItem(1);

        visi = true;  // приложение активно
    }

    public void Menu_progi(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
        view.startAnimation(anim);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null);
        builder.setView(content);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button b_a = (Button)content.findViewById(R.id.button_abaut);
        b_a.setTextColor(COLOR_TEXT);
        b_a.setTypeface(face);
        b_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i  = new Intent(getApplicationContext(),Abaut.class);
                startActivity(i);
                alertDialog.cancel();
            }
        });
        Button b_s =(Button)content.findViewById(R.id.button_setting);
        b_s.setTextColor(COLOR_TEXT);
        b_s.setTypeface(face);
        b_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent s = new Intent(Main.this, Setting.class);
                startActivity(s);
                alertDialog.cancel();

            }
        });
    }

    //заполняем наш скролер
    public class Myadapter extends FragmentStatePagerAdapter {

        public Myadapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Vse_radio() ;
                case 1:
                    return new Vip_radio();
                case 2:
                    return new Moy_plalist();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void fon_button(int button) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);

        switch (button) {
            case 0:
                vse_r.setBackgroundColor(COLOR_ITEM);
                vse_r.startAnimation(anim);
                imageSwitcher.setImageResource(mImageIds[0]);

                popul.setBackgroundColor(COLOR_FON);
                moy_pl.setBackgroundColor(COLOR_FON);
                break;
            case 1:
                popul.setBackgroundColor(COLOR_ITEM);
                popul.startAnimation(anim);
                imageSwitcher.setImageResource(mImageIds[1]);

                vse_r.setBackgroundColor(COLOR_FON);
                moy_pl.setBackgroundColor(COLOR_FON);
                break;
            case 2:
                moy_pl.setBackgroundColor(COLOR_ITEM);
                moy_pl.startAnimation(anim);
                imageSwitcher.setImageResource(mImageIds[2]);

                vse_r.setBackgroundColor(COLOR_FON);
                popul.setBackgroundColor(COLOR_FON);
                break;

        }
    }

    public static void save_value(String Key, String Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static String save_read(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        }
        return "";
    }

    public static void save_value_int(String Key, int Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }

    public static int save_read_int(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getInt(key_save, 0));
        }
        return 0;
    }

    public static void  Toast(String mesag){
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
    }
    public static void Toast_error(String mesag){
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
    }

    public static boolean install_app(String app){
        PackageManager pm = Main.context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(app, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(pi==null){
            return false;
        }else {
            return true;
        }
    }

    public static void setup_aimp(final String potok, final String file){

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_no_aimp, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button dw_aimp_market = (Button)content.findViewById(R.id.button_dialog_dowload_aimp_market);
        Button dw_aimp_link = (Button)content.findViewById(R.id.button_dialog_dowload_aimp_link);
        Button open_sys = (Button)content.findViewById(R.id.button_dialog_open_sistem);

        //если есть магазин покажем и установку через него
        if(install_app("com.google.android.gms")){
            dw_aimp_market.setVisibility(View.VISIBLE);
        }else {
            dw_aimp_market.setVisibility(View.GONE);
        }

        dw_aimp_market.setTypeface(Main.face);
        dw_aimp_market.setTextColor(Main.COLOR_TEXT);
        dw_aimp_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.aimp.player"));
                Main.context.startActivity(intent);
            }
        });


        dw_aimp_link.setTypeface(Main.face);
        dw_aimp_link.setTextColor(Main.COLOR_TEXT);
        dw_aimp_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent openlink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yadi.sk/d/QhgzmO7t3GnBb3"));
                context.startActivity(openlink);
            }
        });

        open_sys.setTypeface(Main.face);
        open_sys.setTextColor(Main.COLOR_TEXT);
        open_sys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                //передаётся один поток то создадим файл и откроем его иначе передаётся уже созданый файл
                if(potok.length()>0) {
                    String name = file.replace("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/", "");

                    //сохраним  временый файл сслку
                    File_function file_function = new File_function();
                    file_function.Save_temp_file(name, potok);
                }

                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(file),"audio/mpegurl");
                //проверим есть чем открыть или нет
                if (i.resolveActivity(Main.context.getPackageManager()) != null) {
                    Main.context.startActivity(i);
                }else {
                    Toast.makeText(context,"Системе не удалось ( ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        visi = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visi = true;
    }
}
