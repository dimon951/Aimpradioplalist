package dmitriy.deomin.aimpradioplalist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Vibor_anim_logo extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vibor_anim_logo);
        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ((LinearLayout)findViewById(R.id.fon_vibora_anim)).setBackgroundColor(Main.COLOR_FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(Main.context, R.layout.delegat_list,getResources().getStringArray(R.array.anim_logo_list));
        ((ListView)findViewById(R.id.listView_dialog_anim)).setAdapter(stringArrayAdapter);

        ((ListView)findViewById(R.id.listView_dialog_anim)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Main.save_value_int(getIntent().getStringExtra("key"),position);
                Main.Run_anim_view(findViewById(R.id.vibor_anim_logo_test),Main.save_read_int(getIntent().getStringExtra("key")));
            }
        });
    }
}
