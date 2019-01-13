package dmitriy.deomin.aimpradioplalist.custom

import android.R
import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main


class Btn : Button {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val wrapper = OnClickListener {
            startAnimation(AnimationUtils.loadAnimation(this.context, dmitriy.deomin.aimpradioplalist.R.anim.myalpha))
            l?.onClick(it)
        }
        super.setOnClickListener(wrapper)
    }

//    override fun setOnLongClickListener(l: OnLongClickListener?) {
//        val wrapper = OnLongClickListener {
//            startAnimation(AnimationUtils.loadAnimation(this.context, dmitriy.deomin.aimpradioplalist.R.anim.myalpha))
//            l?.onLongClick(it)!!
//        }
//
//        super.setOnLongClickListener(wrapper)
//    }
}

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
}

class Text : TextView {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
    }
}

class DialogWindow(context: Context, loaut: Int) {

    private val alertDialog: AlertDialog
    private val content: View

    init {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.Theme_Holo))
        content = LayoutInflater.from(context).inflate(loaut, null)
        builder.setView(content)

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun view(): View {
        return content
    }

    fun close() {
        alertDialog.cancel()
    }
}