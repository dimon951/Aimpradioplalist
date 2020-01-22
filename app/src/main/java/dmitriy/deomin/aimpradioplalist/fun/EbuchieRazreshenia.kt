package dmitriy.deomin.aimpradioplalist.`fun`

import android.Manifest
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.kotlinpermissions.KotlinPermissions
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal

fun EbuchieRazreshenia() {

    //посмотрим есть ли разрешения
    // 0 есть
    // -1 нет
    //для интернета вроде всегда есть , спрашивает только для записи , автоматом и на чтение ставится
    //поставлю на все накройняк
    val permissionCheck = ContextCompat.checkSelfPermission(Main.context, Manifest.permission.INTERNET)
    val permissionFileR = ContextCompat.checkSelfPermission(Main.context, Manifest.permission.READ_EXTERNAL_STORAGE)
    val permissionFileW = ContextCompat.checkSelfPermission(Main.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    if (permissionCheck == 0 && permissionFileR == 0 && permissionFileW == 0) {
        //пошлём сигнал пусть мой плейлист обновится
        signal("Main_update")
                .putExtra("signal", "1")
                .send(Main.context)
    } else {
        //----------------------
        KotlinPermissions.with(Main.context as FragmentActivity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET)
                .onAccepted {
                    //пошлём сигнал пусть обновится
                    signal("Main_update")
                            .putExtra("signal", "update")
                            .send(Main.context)
                }
                .ask()
        //------------------------
    }
}