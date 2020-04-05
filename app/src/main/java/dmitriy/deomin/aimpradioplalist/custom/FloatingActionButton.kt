package dmitriy.deomin.aimpradioplalist.custom

import android.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout

class FloatingActionButton(context: Context) : View(context) {
    var mButtonPaint: Paint? = null
    var mDrawablePaint: Paint? = null
    var mBitmap: Bitmap? = null
    var isHidden = false


    fun setFloatingActionButtonColor(FloatingActionButtonColor: Int) {
        init(FloatingActionButtonColor)
    }

    fun setFloatingActionButtonDrawable(FloatingActionButtonDrawable: Drawable?) {
        mBitmap = (FloatingActionButtonDrawable as BitmapDrawable?)!!.bitmap
        invalidate()
    }

    fun init(FloatingActionButtonColor: Int) {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mButtonPaint!!.color = FloatingActionButtonColor
        mButtonPaint!!.style = Paint.Style.FILL
        mButtonPaint!!.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0))
        mDrawablePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        isClickable = true
        canvas.drawCircle(width / 2.toFloat(), height / 2.toFloat(), (width / 2.6).toFloat(), mButtonPaint!!)
        canvas.drawBitmap(mBitmap!!, (width - mBitmap!!.width) / 2.toFloat(),
                (height - mBitmap!!.height) / 2.toFloat(), mDrawablePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            alpha = 1.0f
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            alpha = 0.6f
        }
        return super.onTouchEvent(event)
    }

    fun hideFloatingActionButton() {
        if (!isHidden) {
            val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f)
            val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f)
            val animSetXY = AnimatorSet()
            animSetXY.playTogether(scaleX, scaleY)
            animSetXY.interpolator = accelerateInterpolator
            animSetXY.duration = 100
            animSetXY.start()
            isHidden = true
        }
    }

    fun showFloatingActionButton() {
        if (isHidden) {
            val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f)
            val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f)
            val animSetXY = AnimatorSet()
            animSetXY.playTogether(scaleX, scaleY)
            animSetXY.interpolator = overshootInterpolator
            animSetXY.duration = 200
            animSetXY.start()
            isHidden = false
        }
    }

    class Builder(context: Activity) {
        private var params: FrameLayout.LayoutParams
        private val activity: Activity
        var gravity = Gravity.BOTTOM or Gravity.RIGHT // default bottom right
        var drawable: Drawable? = null
        var color = Color.WHITE
        var size = 0
        var scale = 0f

        /**
         * Sets the gravity for the FAB
         */
        fun withGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        /**
         * Sets the margins for the FAB in dp
         */
        fun withMargins(left: Int, top: Int, right: Int, bottom: Int): Builder {
            params.setMargins(
                    convertToPixels(left, scale),
                    convertToPixels(top, scale),
                    convertToPixels(right, scale),
                    convertToPixels(bottom, scale))
            return this
        }

        /**
         * Sets the FAB drawable
         */
        fun withDrawable(drawable: Drawable?): Builder {
            this.drawable = drawable
            return this
        }

        /**
         * Sets the FAB color
         */
        fun withButtonColor(color: Int): Builder {
            this.color = color
            return this
        }

        /**
         * Sets the FAB size in dp
         */
        fun withButtonSize(size: Int): Builder {
            var size = size
            size = convertToPixels(size, scale)
            params = FrameLayout.LayoutParams(size, size)
            return this
        }

        fun create(): FloatingActionButton {
            val button = FloatingActionButton(activity)
            button.setFloatingActionButtonColor(color)
            button.setFloatingActionButtonDrawable(drawable)
            params.gravity = gravity
            val root = activity.findViewById<View>(R.id.content) as ViewGroup
            root.addView(button, params)
            return button
        }

        // The calculation (value * scale + 0.5f) is a widely used to convert to dps to pixel units
        // based on density scale
        // see developer.android.com (Supporting Multiple Screen Sizes)
        private fun convertToPixels(dp: Int, scale: Float): Int {
            return (dp * scale + 0.5f).toInt()
        }

        init {
            scale = context.resources.displayMetrics.density
            size = convertToPixels(72, scale) // default size is 72dp by 72dp
            params = FrameLayout.LayoutParams(size, size)
            params.gravity = gravity
            activity = context
        }
    }

    companion object {
        val overshootInterpolator = OvershootInterpolator()
        val accelerateInterpolator = AccelerateInterpolator()
    }

    init {
        init(Color.WHITE)
    }
}