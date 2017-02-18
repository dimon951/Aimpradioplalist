package dmitriy.deomin.aimpradioplalist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Vse_radio extends Fragment {
    ListView listView;
    Context context;
    public static EditText find;

    final String STANCIA="stancia";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.vse_radio, null);
        context = container.getContext();

        find =(EditText)v.findViewById(R.id.editText_find);
        find.setTypeface(Main.face);

        listView =(ListView)v.findViewById(R.id.listviw_vse_radio);

        final String [] mas_radio=getResources().getStringArray(R.array.vse_radio);

        final ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>(mas_radio.length);

        Map<String,Object> m ;

        for(int i = 0;i<mas_radio.length;i++){
            m= new HashMap<String,Object>();
            m.put(STANCIA,(mas_radio[i]));
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { STANCIA };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.textView};

        final Adapter_vse_radio adapter_vse_radio = new Adapter_vse_radio(context,data,R.layout.delegat_vse_radio_list,from,to);
        listView.setAdapter(adapter_vse_radio);
        listView.setTextFilterEnabled(true);


        //пролистываем до нужного элемента
        if(Main.save_read("nomer_stroki")!=null){
            if(!Main.save_read("nomer_stroki").equals("")) {
                if (Integer.valueOf(Main.save_read("nomer_stroki")) > 0) {
                    listView.setSelection(Integer.valueOf(Main.save_read("nomer_stroki")));
                }
            }
        }



        find.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                adapter_vse_radio.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });

        final Button disk_filtr = ((Button)v.findViewById(R.id.kod_diskografii));
        final Button med_int_filtr = ((Button)v.findViewById(R.id.kod_32bit));
        final Button med_int_filtr64 = ((Button)v.findViewById(R.id.kod_64bit));
        final Button med_int_filtr96 = ((Button)v.findViewById(R.id.kod_96bit));
        final Button med_int_filtr128 = ((Button)v.findViewById(R.id.kod_128bit));
        final Button med_int_filtr256 = ((Button)v.findViewById(R.id.kod_256bit));

        disk_filtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(disk_filtr.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(disk_filtr.getText().toString());
                }

            }
        });

        med_int_filtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(med_int_filtr.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(med_int_filtr.getText().toString());
                }
            }
        });

        med_int_filtr96.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(med_int_filtr96.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(med_int_filtr96.getText().toString());
                }
            }
        });

        med_int_filtr64.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(med_int_filtr64.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(med_int_filtr64.getText().toString());
                }
            }
        });
        med_int_filtr128.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(med_int_filtr128.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(med_int_filtr128.getText().toString());
                }
            }
        });
        med_int_filtr256.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if(find.getText().toString().equals(med_int_filtr256.getText().toString())){
                    find.setText("");
                }else {
                    find.setText(med_int_filtr256.getText().toString());
                }
            }
        });





        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //реализация

                //сохраняем позицию
                Main.save_value("nomer_stroki",String.valueOf(position));

                //обратываем
                String k =a.getAdapter().getItem(position).toString();
                k=k.substring(9,k.length()-1);
                String mas[] = k.toString().split("\\n");
                final String name = mas[0];
                final String url_link = mas[1];

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(context).inflate(R.layout.menu_vse_radio, null);
                builder.setView(content);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button add_pls = (Button) content.findViewById(R.id.button_add_plalist);
                Button open_aimp = (Button) content.findViewById(R.id.button_open_aimp);
                Button instal_aimp  =(Button) content.findViewById(R.id.button_instal_aimp);
                Button instal_aimp2  =(Button) content.findViewById(R.id.button_download_yandex_aimp);
                Button open_sistem = (Button) content.findViewById(R.id.button_open_sistem);


                //если aimp установлен скроем кнопку установить аимп
                if(Main.install_app("com.aimp.player")){
                    instal_aimp.setVisibility(View.GONE);
                    instal_aimp2.setVisibility(View.GONE);
                    open_aimp.setVisibility(View.VISIBLE);
                    open_aimp.setTypeface(Main.face);
                    open_aimp.setTextColor(Main.COLOR_TEXT);
                }else {
                    //если есть магазин покажем и установку через него
                    if(Main.install_app("com.google.android.gms")){
                        instal_aimp.setVisibility(View.VISIBLE);
                        instal_aimp.setTypeface(Main.face);
                        instal_aimp.setTextColor(Main.COLOR_TEXT);
                    }else {
                        instal_aimp.setVisibility(View.GONE);
                    }

                    //скачать по ссылке будем показывать всегда
                    instal_aimp2.setVisibility(View.VISIBLE);
                    instal_aimp2.setTypeface(Main.face);
                    instal_aimp2.setTextColor(Main.COLOR_TEXT);

                    open_aimp.setVisibility(View.GONE);

                }




                add_pls.setTypeface(Main.face);
                add_pls.setTextColor(Main.COLOR_TEXT);
                open_sistem.setTypeface(Main.face);
                open_sistem.setTextColor(Main.COLOR_TEXT);

                TextView text_name_i_url = (TextView)content.findViewById(R.id.textView_vse_radio);
                text_name_i_url.setText(name+"\n"+url_link);
                text_name_i_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        view.startAnimation(anim);
                        putText(url_link,context);
                        Toast.makeText(context,"url скопирован в буфер",Toast.LENGTH_SHORT).show();
                    }
                });



                open_sistem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        v.startAnimation(anim);

                        //сохраним  временый файл сслку
                        File_function file_function= new File_function();
                        file_function.Save_temp_file(name+".m3u",url_link);

                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.parse("file://"+Environment.getExternalStorageDirectory().toString()+"/aimp_radio/"+name+".m3u"),"audio/mpegurl");
                        //проверим есть чем открыть или нет
                        if (i.resolveActivity(Main.context.getPackageManager()) != null) {
                            Main.context.startActivity(i);
                        }else {
                            Toast.makeText(context,"Системе не удалось ( ",Toast.LENGTH_LONG).show();
                        }
                    }
                });


                instal_aimp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        v.startAnimation(anim);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.aimp.player"));
                        Main.context.startActivity(intent);
                    }
                });

                instal_aimp2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        v.startAnimation(anim);
                        Intent openlink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yadi.sk/d/oo0XXTPZ3E3SYt"));
                        startActivity(openlink);
                    }
                });


                add_pls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        v.startAnimation(anim);
                        Main.number_page =0;
                                //фильтр для нашего сигнала
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("File_created");

                        //приёмник  сигналов
                        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                if(intent.getAction().equals("File_created")){
                                    //получим данные
                                    String s = intent.getStringExtra("update");
                                    if(s.equals("zaebis")){
                                      //  Toast.makeText(context,"Готово",Toast.LENGTH_SHORT).show();
                                        alertDialog.cancel();
                                        //обновим старницу
                                        Main.myadapter.notifyDataSetChanged();
                                        Main.viewPager.setAdapter(Main.myadapter);
                                        Main.viewPager.setCurrentItem(Main.number_page);
                                    }else {
                                        Toast.makeText(context,"Ошибочка вышла тыкниете еще раз",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        };

                        //регистрируем приёмник
                        Main.context.registerReceiver(broadcastReceiver,intentFilter);


                        File_function file_function= new File_function();
                        file_function.Add_may_plalist_stansiy(url_link);




                    }
                });

                open_aimp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        v.startAnimation(anim);
                        //сохраним  временый файл сслку
                        File_function file_function= new File_function();
                        file_function.Save_temp_file(name+".m3u",url_link);

                            //откроем файл с сылкой в плеере
                            ComponentName cm = new ComponentName(
                                    "com.aimp.player",
                                    "com.aimp.player.views.MainActivity.MainActivity");

                            Intent intent = new Intent();
                            intent.setComponent(cm);

                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://"+Environment.getExternalStorageDirectory().toString()+"/aimp_radio/"+name+".m3u"),"audio/mpegurl");
                            intent.setFlags(0x3000000);

                            startActivity(intent);
                    }
                });

            }
            });


        return v;
    }


    //запись
    @SuppressWarnings("deprecation")
    public void putText(String text,Context context){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }
    }

}
