package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.CardView
import android.util.AttributeSet

class Fon_item : CardView {

    constructor(context: Context) : super(context) {
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.setCardBackgroundColor(Main.COLOR_ITEM)

    }
}
