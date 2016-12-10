package dmitriy.deomin.aimpradioplalist;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class Adapter_vse_radio extends SimpleAdapter{
    public Adapter_vse_radio(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
}
