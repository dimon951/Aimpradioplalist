package dmitriy.deomin.aimpradioplalist.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class Slot(context: Context, name: String) {

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
                    run(intent)
                    //и если сигнал послан для однократного выполнения, удалим слот
                    if (!intent.getBooleanExtra("run",true)) {
                        delete_slot()
                    }
                    //иначе пусть дальше слушает
                }
            }
        }
        //регистрируем приёмник
        con.registerReceiver(broadcastReceiver, intentFilter)
    }

    //выполняется на стороне
    fun onRun(block: (Intent) -> Unit) {
        event = block
    }

    //срабатывает при приемке сигнала
    private fun run(param: Intent) {
        event?.invoke(param)
    }

    //удалить приёмник
    private fun delete_slot() {
        con.unregisterReceiver(broadcastReceiver)
    }
}


//добавил интенту метод(как он работат там хз)
fun Intent.send(context: Context) {
    context.sendBroadcast(this)
}

//созданим signal тотже интент только имя поменяем
class signal(name: String) : Intent() {
    init {
        this.action = name
    }
}
