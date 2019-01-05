package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView

class Btn : Button {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        super.onDraw(canvas)
    }
}

class Fon_item : CardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        this.setCardBackgroundColor(Main.COLOR_ITEM)
        super.onDraw(canvas)
    }
}

class Text : TextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    override fun onDraw(canvas: Canvas) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        super.onDraw(canvas)
    }
}