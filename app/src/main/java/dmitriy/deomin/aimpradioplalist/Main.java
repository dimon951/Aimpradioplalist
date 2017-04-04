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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;



public class Main extends FragmentActivity {

    public static Context context;
    public static LinearLayout liner_boss;

    TextView time_reklama;

    public static   ViewPager viewPager;
    public static Myadapter myadapter;
    public static int number_page;

    ImageSwitcher imageSwitcher;
    int[] mImageIds;

    Button vse_r;
    Button popul;
    Button moy_pl;

    //размеры экрана
    //--------------------
    public static int wd;
    public static int hd;
    //--------------------

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

    static public int TIME_SHOW_REKLAMA = 10; // сколько показывать рекламу сек
    boolean visi;//true при активном приложении
    boolean time_show_reklamma; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        face = Typeface.createFromAsset(getAssets(), ((save_read("fonts").equals("")) ? "fonts/Tweed.ttf" : save_read("fonts")));

        //размеры экрана
        //----------------------
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            wd = display.getWidth();
            hd = display.getHeight();
        //----------------------


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
       // viewPager.setPageTransformer(true, new CubeOutTransformer());
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

        //реклама
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        time_reklama = (TextView)findViewById(R.id.time_reklama_text);

        time_show_reklamma = false;  //если бы черти isVisible mAdView сделали это херня бы не пригодилась

        //если нет интеренета скроем еЁ
        if (!isOnline(context)) {
            mAdView.setVisibility(View.GONE);
            time_reklama.setVisibility(View.GONE);
        } else {
            //через 10 секунд скроем её(пока так потом можно регулировать от количества постов)
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v("TTT","ebasit");
                    if (visi) {
                        if (time_show_reklamma) {
                            mAdView.setVisibility(View.GONE); // скроем рекламу и поток больше не запустится
                            time_reklama.setVisibility(View.GONE);
                        } else {
                            //иначе покажем рекламу
                            mAdView.setVisibility(View.VISIBLE);
                            time_show_reklamma = true; // это нужно чтоб знать что реклама показна
                            //и текст сколько осталось времени
                            time_reklama.setVisibility(View.VISIBLE);
                            pokaz_smeny_time();

                            handler.postDelayed(this, 1000 * TIME_SHOW_REKLAMA); // через 10 секунд вырубим рекламу
                        }
                    }else {
                        handler.postDelayed(this, 1000 * 2); // если приложение свернуто пока в пустую погоняем поток
                    }
                }
            });
        }

    }



    public void pokaz_smeny_time(){

        final int[] time_visible = {TIME_SHOW_REKLAMA};

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.v("TTT","ebasit");
                if (visi) {
                    time_visible[0]--;
                    time_reklama.setText("Реклама скроется через  " + String.valueOf(time_visible[0]));

                    if(time_visible[0]>0){
                        handler.postDelayed(this, 1000); // через 10 секунд вырубим рекламу
                    }else{
                        time_reklama.setVisibility(View.GONE);
                    }

                }else {
                    handler.postDelayed(this, 1000 * 2); // если приложение свернуто пока в пустую погоняем поток
                }
            }
        });
    }

    public void Menu_progi(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
        view.startAnimation(anim);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null);
        builder.setView(content);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((Button)content.findViewById(R.id.button_abaut)).setTextColor(COLOR_TEXT);
        ((Button)content.findViewById(R.id.button_abaut)).setTypeface(face);
        ((Button)content.findViewById(R.id.button_abaut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i  = new Intent(getApplicationContext(),Abaut.class);
                startActivity(i);
                alertDialog.cancel();
            }
        });
        ((Button)content.findViewById(R.id.button_setting)).setTextColor(COLOR_TEXT);
        ((Button)content.findViewById(R.id.button_setting)).setTypeface(face);
        ((Button)content.findViewById(R.id.button_setting)).setOnClickListener(new View.OnClickListener() {
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


    //*******************************************************
    public static void save_value_bool(String Key, boolean Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public static boolean save_read_bool(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getBoolean(key_save, false));
        } else {
            return false;
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
                Intent openlink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yadi.sk/d/oo0XXTPZ3E3SYt"));
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




    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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
