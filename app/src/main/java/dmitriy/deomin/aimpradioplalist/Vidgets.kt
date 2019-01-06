package dmitriy.deomin.aimpradioplalist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView

class Btn : Button {
    constructor(context: Context) : super(context){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
}

class Fon_item : CardView {
    constructor(context: Context) : super(context){
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        this.setCardBackgroundColor(Main.COLOR_ITEM)
    }
}

class Text : TextView {
    constructor(context: Context) : super(context){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
}