package dmitriy.deomin.aimpradioplalist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Vip_radio extends Fragment {
    Context context;
    ListView listView;
    final String STANCIA="stancia";
    final String AVAPOP="avapop";
    final String LINK="link";


    File_function file_function;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.vip_radio, null);

        context = container.getContext();

        listView =(ListView)v.findViewById(R.id.listview_pop);

        file_function = new File_function();


        final String [] mas_radio=getResources().getStringArray(R.array.pop_radio);

        final ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>(mas_radio.length);

        Map<String,Object> m ;

        for(int i = 0;i<mas_radio.length;i++){
            m= new HashMap<String,Object>();
            m.put(STANCIA,(mas_radio[i].split("\\n")[0]));
            m.put(AVAPOP,(mas_radio[i].split("\\n")[1]));
            m.put(LINK,(mas_radio[i].split("\\n")[2])); //ссылка тоже список ссылок разбываться буде в адаптере
            data.add(m);
        }



        final Adapter_pop_radio adapter_pop_radio = new Adapter_pop_radio(context,data,R.layout.delegat_vse_radio_list,null,null);
        listView.setAdapter(adapter_pop_radio);



        return v;
    }
}
