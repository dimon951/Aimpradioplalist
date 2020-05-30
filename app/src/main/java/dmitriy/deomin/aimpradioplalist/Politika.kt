package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.politika.*
import org.jetbrains.anko.backgroundColor


class Politika : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.politika)
        if(Main.FULLSCRIN >0){
            //во весь экран
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if(Main.NAVBUTTON >0){
            //скрывем кнопки навигации
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
            //будем слушать  если покажется опять - закроем
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) decorView.systemUiVisibility =
                        uiOptions
            }
            //-----------------------------------------------------------------------------
        }
        fon_politiki.backgroundColor = Main.COLOR_FON
        textpolitiki.movementMethod = ScrollingMovementMethod()
    }
}
