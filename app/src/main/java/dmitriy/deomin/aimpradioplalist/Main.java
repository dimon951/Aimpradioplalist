package dmitriy.deomin.aimpradioplalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import android.widget.ViewSwitcher;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;



public class Main extends FragmentActivity {

    public static Context context;
    public static LinearLayout liner_boss;

    public static   ViewPager viewPager;
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

        time_show_reklamma = false;  //если бы черти isVisible mAdView сделали это херня бы не пригодилась

        //если нет интеренета скроем еЁ
        if (!isOnline(context)) {
            mAdView.setVisibility(View.GONE);
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
                        } else {
                            //иначе покажем
                            mAdView.setVisibility(View.VISIBLE);
                            time_show_reklamma = true; // это нужно чтоб знать что реклама показна
                            handler.postDelayed(this, 1000 * TIME_SHOW_REKLAMA); // через 10 секунд вырубим рекламу
                        }
                    }else {
                        handler.postDelayed(this, 1000 * 2); // если приложение свернуто пока в пустую погоняем поток
                    }
                }
            });
        }

    }

    //анимация логоттипа  0 - вык    от 1 до 62
    public static void Run_anim_view(View v,int num){
        switch (num){
            case 0:
                // нечего не делаем
               break;
            case 1:
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(v);
                break;
            case 2:
                YoYo.with(Techniques.TakingOff)
                        .duration(700)
                        .playOn(v);
                break;
            case 3:
                YoYo.with(Techniques.Flash)
                        .duration(700)
                        .playOn(v);
                break;
            case 4:
                YoYo.with(Techniques.Pulse)
                        .duration(700)
                        .playOn(v);
                break;
            case 5:
                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(v);
                break;
            case 6:
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(v);
                break;
            case 7:
                YoYo.with(Techniques.Swing)
                        .duration(700)
                        .playOn(v);
                break;
            case 8:
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .playOn(v);
                break;
            case 9:
                YoYo.with(Techniques.Landing)
                        .duration(700)
                        .playOn(v);
                break;
            case 10:
                YoYo.with(Techniques.Bounce)
                        .duration(700)
                        .playOn(v);
                break;
            case 11:
                YoYo.with(Techniques.StandUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 12:
                YoYo.with(Techniques.Wave)
                        .duration(700)
                        .playOn(v);
                break;
            case 13:
                YoYo.with(Techniques.Hinge)
                        .duration(700)
                        .playOn(v);
                break;
            case 14:
                YoYo.with(Techniques.RollIn)
                        .duration(700)
                        .playOn(v);
                break;
            case 15:
                YoYo.with(Techniques.RollOut)
                        .duration(700)
                        .playOn(v);
                break;
            case 16:
                YoYo.with(Techniques.BounceIn)
                        .duration(700)
                        .playOn(v);
                break;
            case 17:
                YoYo.with(Techniques.BounceInDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 18:
                YoYo.with(Techniques.BounceInLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 19:
                YoYo.with(Techniques.BounceInRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 20:
                YoYo.with(Techniques.BounceInUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 21:
                YoYo.with(Techniques.FadeIn)
                        .duration(700)
                        .playOn(v);
                break;
            case 22:
                YoYo.with(Techniques.FadeInUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 23:
                YoYo.with(Techniques.FadeInDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 24:
                YoYo.with(Techniques.FadeInLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 25:
                YoYo.with(Techniques.FadeInRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 26:
                YoYo.with(Techniques.FadeOut)
                        .duration(700)
                        .playOn(v);
                break;
            case 27:
                YoYo.with(Techniques.FadeOutDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 28:
                YoYo.with(Techniques.FadeOutLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 29:
                YoYo.with(Techniques.FadeOutRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 30:
                YoYo.with(Techniques.FadeOutUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 31:
                YoYo.with(Techniques.FlipInX)
                        .duration(700)
                        .playOn(v);
                break;
            case 32:
                YoYo.with(Techniques.FlipOutX)
                        .duration(700)
                        .playOn(v);
                break;
            case 33:
                YoYo.with(Techniques.FlipOutY)
                        .duration(700)
                        .playOn(v);
                break;
            case 34:
                YoYo.with(Techniques.RotateIn)
                        .duration(700)
                        .playOn(v);
                break;
            case 35:
                YoYo.with(Techniques.RotateInDownLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 36:
                YoYo.with(Techniques.RotateInDownRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 37:
                YoYo.with(Techniques.RotateInUpLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 38:
                YoYo.with(Techniques.RotateInUpRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 39:
                YoYo.with(Techniques.RotateOut)
                        .duration(700)
                        .playOn(v);
                break;
            case 40:
                YoYo.with(Techniques.RotateOutDownLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 41:
                YoYo.with(Techniques.RotateOutDownRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 42:
                YoYo.with(Techniques.RotateOutUpLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 43:
                YoYo.with(Techniques.RotateOutUpRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 44:
                YoYo.with(Techniques.SlideInLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 45:
                YoYo.with(Techniques.SlideInRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 46:
                YoYo.with(Techniques.SlideInUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 47:
                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 48:
                YoYo.with(Techniques.SlideOutLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 49:
                YoYo.with(Techniques.SlideOutRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 50:
                YoYo.with(Techniques.SlideOutUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 51:
                YoYo.with(Techniques.SlideOutDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 52:
                YoYo.with(Techniques.ZoomIn)
                        .duration(700)
                        .playOn(v);
                break;
            case 53:
                YoYo.with(Techniques.ZoomInDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 54:
                YoYo.with(Techniques.ZoomInLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 55:
                YoYo.with(Techniques.ZoomInRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 56:
                YoYo.with(Techniques.ZoomInUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 57:
                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(v);
                break;
            case 58:
                YoYo.with(Techniques.ZoomOutDown)
                        .duration(700)
                        .playOn(v);
                break;
            case 59:
                YoYo.with(Techniques.ZoomOutLeft)
                        .duration(700)
                        .playOn(v);
                break;
            case 60:
                YoYo.with(Techniques.ZoomOutRight)
                        .duration(700)
                        .playOn(v);
                break;
            case 61:
                YoYo.with(Techniques.ZoomOutUp)
                        .duration(700)
                        .playOn(v);
                break;
            case 62:
                YoYo.with(Techniques.DropOut)
                        .duration(700)
                        .playOn(v);
                break;

        }
    }


    public void Menu_progi(View view) {

        Run_anim_view(view,Main.save_read_int("anim_view_clik_logo"));

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null);
        builder.setView(content);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_menu)).setBackgroundColor(COLOR_FON);

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
        ((Button)content.findViewById(R.id.button_setting_anim)).setTextColor(COLOR_TEXT);
        ((Button)content.findViewById(R.id.button_setting_anim)).setTypeface(face);
        ((Button)content.findViewById(R.id.button_setting_anim)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent s = new Intent(Main.this, Animacia.class);
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
                Run_anim_view(imageSwitcher,Main.save_read_int("anim_view_svepe_logo"));

                popul.setBackgroundColor(COLOR_FON);
                moy_pl.setBackgroundColor(COLOR_FON);
                break;
            case 1:
                popul.setBackgroundColor(COLOR_ITEM);
                popul.startAnimation(anim);
                imageSwitcher.setImageResource(mImageIds[1]);
                Run_anim_view(imageSwitcher,Main.save_read_int("anim_view_svepe_logo"));

                vse_r.setBackgroundColor(COLOR_FON);
                moy_pl.setBackgroundColor(COLOR_FON);
                break;
            case 2:
                moy_pl.setBackgroundColor(COLOR_ITEM);
                moy_pl.startAnimation(anim);
                imageSwitcher.setImageResource(mImageIds[2]);
                Run_anim_view(imageSwitcher,Main.save_read_int("anim_view_svepe_logo"));

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
