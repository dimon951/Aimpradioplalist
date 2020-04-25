package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
        fon_politiki.backgroundColor = Main.COLOR_FON
        textpolitiki.movementMethod = ScrollingMovementMethod()
    }
}
