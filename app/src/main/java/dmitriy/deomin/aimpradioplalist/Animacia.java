package dmitriy.deomin.aimpradioplalist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class Animacia extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animacia);

       //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ((LinearLayout)findViewById(R.id.animacia)).setBackgroundColor(Main.COLOR_FON);


        Button edit_anim_clik,edit_anim_svipe;

        edit_anim_clik = (Button)findViewById(R.id.button_edit_anim_clik);
        edit_anim_svipe =(Button)findViewById(R.id.button_edit_anim_perechod);


        edit_anim_clik.setTypeface(Main.face);
        edit_anim_clik.setTextColor(Main.COLOR_TEXT);
        edit_anim_clik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Main.context,Vibor_anim_logo.class);
                i.putExtra("key","anim_view_clik_logo");
                startActivity(i);
            }
        });

        edit_anim_svipe.setTypeface(Main.face);
        edit_anim_svipe.setTextColor(Main.COLOR_TEXT);
        edit_anim_svipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Main.context,Vibor_anim_logo.class);
                i.putExtra("key","anim_view_svepe_logo");
                startActivity(i);
            }
        });

    }
}
