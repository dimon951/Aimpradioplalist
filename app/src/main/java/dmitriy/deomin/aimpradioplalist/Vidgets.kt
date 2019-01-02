package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.CardView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView

class But_Kod : Button {
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
        //форматирование текста если есть перенос
        if (this.text.toString().contains("\n")) {
            //new UnderlineSpan() - подчеркнутый текст
            //new StyleSpan(Typeface.BOLD) - полужирный тектс
            //new StyleSpan(Typeface.ITALIC) - курсив
            //new ForegroundColorSpan(Color.GREEN) - цвет
            //new RelativeSizeSpan(1.5f) - размер текста
            Main.text = SpannableString(this.text.toString())
            Main.text.setSpan(UnderlineSpan(), 0, this.text.toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(RelativeSizeSpan(1.1f), 0, this.text.toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Main.text.setSpan(RelativeSizeSpan(0.8f), this.text.toString().indexOf("\n"), this.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.text = Main.text
        }
        super.onDraw(canvas)
    }
}