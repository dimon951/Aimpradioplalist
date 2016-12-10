package dmitriy.deomin.aimpradioplalist;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Admin on 07.11.2016.
 */

public class But_Kod extends Button {
    public But_Kod(Context context) {
        super(context);
    }

    public But_Kod(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public But_Kod(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
    }
}
