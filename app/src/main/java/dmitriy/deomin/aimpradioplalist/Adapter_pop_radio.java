package dmitriy.deomin.aimpradioplalist;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Map;

public class Adapter_pop_radio extends SimpleAdapter {
    private ArrayList<Map<String, Object>> results;
    private Context context;
    Transformation transformation;


        public Adapter_pop_radio(Context context,ArrayList<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.results = data;
            transformation = new RoundedTransformationBuilder()
                    .borderColor(Main.Companion.getCOLOR_TEXT())
                    .borderWidthDp(2)
                    .cornerRadiusDp(10)
                    .oval(false)
                    .build();
    }

    static class ViewHolder {
        String link;
        TextView text;
        ImageView ava;
        Button add;
        Button play;
        Button share;

        Button kbps1;
        Button kbps2;
        Button kbps3;
        Button kbps4;
        Button kbps5;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View v = view;
        final ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.delegat_pop, parent, false);
            viewHolder = new ViewHolder();

            //получаем все наши виджеты
            viewHolder.text = (TextView) v.findViewById(R.id.Text_name_pop);
            viewHolder.ava = (ImageView)v.findViewById(R.id.ava_pop);
            viewHolder.add = (Button)v.findViewById(R.id.button_add);
            viewHolder.play = (Button)v.findViewById(R.id.button_open);
            viewHolder.share = (Button)v.findViewById(R.id.button_cshre);


            viewHolder.kbps1=(Button)v.findViewById(R.id.pop_vibr_kbts_1);
            viewHolder.kbps2=(Button)v.findViewById(R.id.pop_vibr_kbts_2);
            viewHolder.kbps3=(Button)v.findViewById(R.id.pop_vibr_kbts_3);
            viewHolder.kbps4=(Button)v.findViewById(R.id.pop_vibr_kbts_4);
            viewHolder.kbps5=(Button)v.findViewById(R.id.pop_vibr_kbts_5);

            v.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.text.setTypeface(Main.Companion.getFace());
        viewHolder.text.setText(results.get(position).get("stancia").toString());

        Picasso.with(context)
                .load("file:///android_asset/ava_pop/"+results.get(position).get("avapop").toString())
                .transform(transformation)
                .into(viewHolder.ava);

        //viewHolder.ava.setMaxWidth(viewHolder.ava.getHeight());


        //смотрим че там передалось в параметрах какаое качество и покажем нужные кнопки
        final String[] mass_link_parametr = results.get(position).get("link").toString().split("~kbps~");
        Log.e("rrr",String.valueOf(mass_link_parametr.length));

        switch (mass_link_parametr.length){
            case 2:
                viewHolder.kbps1.setVisibility(View.VISIBLE);
                viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps2.setVisibility(View.GONE);
                viewHolder.kbps3.setVisibility(View.GONE);
                viewHolder.kbps4.setVisibility(View.GONE);
                viewHolder.kbps5.setVisibility(View.GONE);
                break;
            case 4:
                viewHolder.kbps1.setVisibility(View.VISIBLE);
                viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps2.setVisibility(View.VISIBLE);
                viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps3.setVisibility(View.GONE);
                viewHolder.kbps4.setVisibility(View.GONE);
                viewHolder.kbps5.setVisibility(View.GONE);
                break;
            case 6:
                viewHolder.kbps1.setVisibility(View.VISIBLE);
                viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps2.setVisibility(View.VISIBLE);
                viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps3.setVisibility(View.VISIBLE);
                viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps4.setVisibility(View.GONE);
                viewHolder.kbps5.setVisibility(View.GONE);
                break;
            case 8:
                viewHolder.kbps1.setVisibility(View.VISIBLE);
                viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps2.setVisibility(View.VISIBLE);
                viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps3.setVisibility(View.VISIBLE);
                viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps4.setVisibility(View.VISIBLE);
                viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps5.setVisibility(View.GONE);
                break;
            case 10:
                viewHolder.kbps1.setVisibility(View.VISIBLE);
                viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps2.setVisibility(View.VISIBLE);
                viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps3.setVisibility(View.VISIBLE);
                viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps4.setVisibility(View.VISIBLE);
                viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                viewHolder.kbps5.setVisibility(View.VISIBLE);
                viewHolder.kbps5.setText(mass_link_parametr[8].toString());
                viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                break;
        }


        //по умолчанию ставим первую ссылку
        viewHolder.link = mass_link_parametr[1].toString();

        Main.Companion.setText(new SpannableString(viewHolder.kbps1.getText().toString()));
        Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps1.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps1.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.kbps1.setText(Main.Companion.getText());

        //будем слушать кнопки и менять ссылку

        viewHolder.kbps1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                Main.Companion.setText(new SpannableString(viewHolder.kbps1.getText().toString()));
                Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps1.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps1.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //сбросим другие кнопки
                switch (mass_link_parametr.length){
                    case 2:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.GONE);
                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 4:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 6:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 8:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 10:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.VISIBLE);
                        viewHolder.kbps5.setText(mass_link_parametr[8].toString());
                        viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                        break;
                }


                viewHolder.kbps1.setText(Main.Companion.getText());

                viewHolder.link = mass_link_parametr[1].toString();

            }
        });

        viewHolder.kbps2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                Main.Companion.setText(new SpannableString(viewHolder.kbps2.getText().toString()));
                Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps2.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps2.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //сбросим другие кнопки
                switch (mass_link_parametr.length){
                    case 2:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.GONE);
                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 4:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 6:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 8:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 10:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.VISIBLE);
                        viewHolder.kbps5.setText(mass_link_parametr[8].toString());
                        viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                        break;
                }

                viewHolder.kbps2.setText(Main.Companion.getText());

                viewHolder.link = mass_link_parametr[3].toString();
            }
        });

        viewHolder.kbps3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                Main.Companion.setText(new SpannableString(viewHolder.kbps3.getText().toString()));
                Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps3.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps3.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //сбросим другие кнопки
                switch (mass_link_parametr.length){
                    case 2:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.GONE);
                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 4:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 6:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 8:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 10:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.VISIBLE);
                        viewHolder.kbps5.setText(mass_link_parametr[8].toString());
                        viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                        break;
                }

                viewHolder.kbps3.setText(Main.Companion.getText());

                viewHolder.link = mass_link_parametr[5].toString();
            }
        });


        viewHolder.kbps4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                Main.Companion.setText(new SpannableString(viewHolder.kbps4.getText().toString()));
                Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps4.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps4.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //сбросим другие кнопки
                switch (mass_link_parametr.length){
                    case 2:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.GONE);
                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 4:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 6:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 8:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 10:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.VISIBLE);
                        viewHolder.kbps5.setText(mass_link_parametr[8].toString());
                        viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                        break;
                }

                viewHolder.kbps4.setText(Main.Companion.getText());

                viewHolder.link = mass_link_parametr[7].toString();
            }
        });

        viewHolder.kbps5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                Main.Companion.setText(new SpannableString(viewHolder.kbps5.getText().toString()));
                Main.Companion.getText().setSpan(new UnderlineSpan(), 0, viewHolder.kbps5.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Main.Companion.getText().setSpan(new StyleSpan(Typeface.BOLD), 0, viewHolder.kbps5.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //сбросим другие кнопки
                switch (mass_link_parametr.length){
                    case 2:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.GONE);
                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 4:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.GONE);
                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 6:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.GONE);
                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 8:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.GONE);
                        break;
                    case 10:
                        viewHolder.kbps1.setVisibility(View.VISIBLE);
                        viewHolder.kbps1.setText(mass_link_parametr[0].toString());
                        viewHolder.kbps1.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps2.setVisibility(View.VISIBLE);
                        viewHolder.kbps2.setText(mass_link_parametr[2].toString());
                        viewHolder.kbps2.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps3.setVisibility(View.VISIBLE);
                        viewHolder.kbps3.setText(mass_link_parametr[4].toString());
                        viewHolder.kbps3.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps4.setVisibility(View.VISIBLE);
                        viewHolder.kbps4.setText(mass_link_parametr[6].toString());
                        viewHolder.kbps4.setTextColor(Main.Companion.getCOLOR_TEXT());

                        viewHolder.kbps5.setVisibility(View.VISIBLE);
                        viewHolder.kbps5.setTextColor(Main.Companion.getCOLOR_TEXT());
                        break;
                }

                viewHolder.kbps5.setText(Main.Companion.getText());

                viewHolder.link = mass_link_parametr[9].toString();
            }
        });





        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Main.Companion.setNumber_page(1);
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

                                //обновим старницу

                                Main.Companion.getMyadapter().notifyDataSetChanged();
                                Main.Companion.getViewPager().setAdapter(Main.Companion.getMyadapter());
                                Main.Companion.getViewPager().setCurrentItem(Main.Companion.getNumber_page());
                            }else {
                                Toast.makeText(context,"Ошибочка вышла тыкниете еще раз",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                };

                //регистрируем приёмник
                Main.Companion.getContext().registerReceiver(broadcastReceiver,intentFilter);


                File_function file_function= new File_function();

                //запишем в файл выбранную станцию
                file_function.Add_may_plalist_stansiy(viewHolder.link,results.get(position).get("stancia").toString());
            }
        });


        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                //Изменим текущию вкладку при обновлении что тутж остаться
                Main.Companion.setNumber_page(1);

                //сохраним  временый файл сслку
                File_function file_function = new File_function();
                // file_function.Save_temp_file(results.get(position).get("stancia").toString()+".m3u",results.get(position).get("link").toString());
                file_function.Save_temp_file(results.get(position).get("stancia").toString() + ".m3u", viewHolder.link);


                if (Main.Companion.install_app("com.aimp.player")) {
                    //откроем файл с сылкой в плеере
                    ComponentName cm = new ComponentName(
                            "com.aimp.player",
                            "com.aimp.player.views.MainActivity.MainActivity");

                    Intent intent = new Intent();
                    intent.setComponent(cm);

                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + results.get(position).get("stancia").toString() + ".m3u"), "audio/mpegurl");
                    intent.setFlags(0x3000000);

                    Main.Companion.getContext().startActivity(intent);

                } else {
                    //иначе предложим системе открыть или установить аимп
                    Main.Companion.setup_aimp(viewHolder.link,
                            "file://" + Environment.getExternalStorageDirectory().toString() + "/aimp_radio/" + results.get(position).get("stancia").toString() + ".m3u");
                }
            }

            });


        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                //intent.putExtra(Intent.EXTRA_TEXT,results.get(position).get("link").toString());
                intent.putExtra(Intent.EXTRA_TEXT,viewHolder.link);
                try
                {
                    Main.Companion.getContext().startActivity(Intent.createChooser(intent, "Поделиться через"));
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(context, "Some error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

}
