package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R

//даёт рандомную положительную фразу
fun rnd_ok(): String {
    val mas = Main.context.resources.getStringArray(R.array.list_ok)
    val i = rnd_int(0, mas.size - 1)
    return if (i < mas.size) {
        mas[i]
    } else {
        mas[0]
    }

}

fun rnd_int(min: Int, max: Int): Int {
    var Max = max
    Max -= min
    return (Math.random() * ++Max).toInt() + min
}
//