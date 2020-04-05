package dmitriy.deomin.aimpradioplalist.custom

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.AnimationUtils
import dmitriy.deomin.aimpradioplalist.Main

class Btn : androidx.appcompat.widget.AppCompatButton {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.gravity = Gravity.CENTER
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.gravity = Gravity.CENTER
        this.setTextColor(Main.COLOR_TEXT)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.gravity = Gravity.CENTER
        this.setTextColor(Main.COLOR_TEXT)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val wrapper = OnClickListener {
            startAnimation(AnimationUtils.loadAnimation(this.context, dmitriy.deomin.aimpradioplalist.R.anim.myalpha))
            l?.onClick(it)
        }
        super.setOnClickListener(wrapper)
    }
}

class Fon_item : androidx.cardview.widget.CardView {
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

class Text : androidx.appcompat.widget.AppCompatTextView {
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

class Textcoontext : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXTcontext)
        this.textSize = Main.SIZE_TEXT_CONTEXT_text
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXTcontext)
        this.textSize = Main.SIZE_TEXT_CONTEXT_text
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXTcontext)
        this.textSize = Main.SIZE_TEXT_CONTEXT_text
    }
}

class TextNameItem : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_NAME
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_NAME
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_NAME
    }
}

class TextInfo : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_CONTEXT
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_CONTEXT
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
        this.setTextColor(Main.COLOR_TEXT)
        this.textSize = Main.SIZE_TEXT_CONTEXT
    }
}

class DialogWindow(context: Context, loaut: Int,onTop: Boolean = false) {

    var full_skren = false

    private val alertDialog: AlertDialog
    private val content: View

    init {
        val builder = AlertDialog.Builder(context)
        content = LayoutInflater.from(context).inflate(loaut, null)
        builder.setView(content)

        this.alertDialog = builder.create()

        if(onTop){
            alertDialog.setCanceledOnTouchOutside(false)
        }else{
            alertDialog.setCanceledOnTouchOutside(true)
        }

        //сместим немногов низ окно
        val params = this.alertDialog.window?.attributes

        //https://it-giki.com/post/355.html
        params!!.y = 150

        //применяем правки
        this.alertDialog.window!!.attributes = params

        //показываем окно
        this.alertDialog.show()
    }

    fun view(): View {
        return content
    }

    fun close() {
        alertDialog.cancel()
    }

    fun full_screen(){
        full_skren = if(full_skren){
            alertDialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            false
        }else{
            alertDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
            true
        }
    }


}
