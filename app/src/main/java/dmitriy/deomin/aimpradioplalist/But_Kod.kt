package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.Button


class But_Kod : Button {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
}
