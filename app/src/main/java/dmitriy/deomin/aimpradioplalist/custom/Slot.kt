package dmitriy.deomin.aimpradioplalist.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager


//Slot(this, "slot1").onRun {it->
//    val value = it.getStringExtra("param")
//}

class Slot(context: Context, name: String, life: Boolean = true) {

    private val con = context
    private val broadcastReceiver: BroadcastReceiver
    private var event: ((Intent) -> Unit)? = null

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(name)
        //приёмник  сигналов
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                //если получен наш сигнал
                if (intent.action == name) {
                    //выполняем халтуру на стороне
                    Run(intent)
                    //и если сигнал послан для однократного выполнения, удалим слот
                    if (!intent.getBooleanExtra("run", true) || !life) {
                        Delete_slot()
                    }
                    //иначе пусть дальше слушает
                }
            }
        }
        //регистрируем приёмник
        LocalBroadcastManager.getInstance(con).registerReceiver(broadcastReceiver, intentFilter)


    }

    //выполняется на стороне
    fun onRun(block: (Intent) -> Unit) {
        event = block
    }

    //срабатывает при приемке сигнала
    private fun Run(param: Intent) {
        event?.invoke(param)
    }

    //удалить приёмник
   fun Delete_slot() {
        LocalBroadcastManager.getInstance(con).unregisterReceiver(broadcastReceiver)
    }


}


//-----------------------------------------------------------------------------
//signal("slot1")
//.putExtra("param", "zaebis")
//.putExtra("run", false)
//.run(this)

//добавил интенту метод(как он работат там хз)
fun Intent.send(context: Context) {
    LocalBroadcastManager.getInstance(context).sendBroadcast(this)
}

//созданим signal тотже интент только имя поменяем
class signal(name: String) : Intent() {
    init {
        this.action = name
    }
}
//-----------------------------------------------------------