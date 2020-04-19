package dmitriy.deomin.aimpradioplalist.`fun`

import android.graphics.Typeface
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.`fun`.file.isAssetExists

fun load_font() {

    when (val s = save_read("fonts")) {
        "system" -> Main.face = Typeface.DEFAULT
        "" -> {
            //если первый раз запустили установим Tweed.ttf , сначала ток проверим что он есть
            if (isAssetExists("fonts/Tweed.ttf")) {
                Main.face = Typeface.createFromAsset(Main.context.assets, "fonts/Tweed.ttf")
            } else {
                Main.face = Typeface.DEFAULT
            }
        }
        else->{
            //если есть выбранный другой шрифт, тоже проверим , и если всё путём установим
            if (isAssetExists(s)) {
                Main.face = Typeface.createFromAsset(Main.context.assets, s)
            } else {
                Main.face = Typeface.DEFAULT
            }
        }
    }
}