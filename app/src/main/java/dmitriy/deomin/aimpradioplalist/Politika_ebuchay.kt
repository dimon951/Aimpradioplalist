package dmitriy.deomin.aimpradioplalist

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_politika_ebuchay.*
import org.jetbrains.anko.backgroundColor


class Politika_ebuchay : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politika_ebuchay)
        fon_politiki.backgroundColor = Main.COLOR_FON
        textpolitiki.movementMethod = ScrollingMovementMethod()
    }
}
