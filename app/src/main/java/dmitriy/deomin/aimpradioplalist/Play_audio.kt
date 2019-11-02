package dmitriy.deomin.aimpradioplalist

import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import java.io.IOException




class Play_audio(name:String,url:String){
    var playStatus = false //статус проигрывания
    var mediaPlayer: MediaPlayer? = null

    init {
        // предварительная настройка
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(Main.context, Uri.parse(url))
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val plaer = DialogWindow(Main.context,R.layout.plaer,true)

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.stop()
            playStatus = false
            plaer.view().findViewById<Button>(R.id.go).text="play"
        }

        plaer.view().findViewById<Button>(R.id.close).onClick {
            if (playStatus) {
                mediaPlayer?.stop()
                playStatus = false
            }
            plaer.close()
        }

        plaer.view().findViewById<TextView>(R.id.text_logo).text=url


        plaer.view().findViewById<Button>(R.id.go).onClick {
            // стоп
            if (playStatus) {
                mediaPlayer?.stop()
                playStatus = false
                plaer.view().findViewById<Button>(R.id.go).text="play"
                // старт
            } else {
                try {
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    plaer.view().findViewById<Button>(R.id.go).text="stop"
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                playStatus = true
            }
        }


        plaer.view().findViewById<Button>(R.id.download).onClick {

            val dw = DialogWindow(Main.context, R.layout.dialog_delete_stancii, true)
            val dw_start = dw.view().findViewById<Button>(R.id.button_dialog_delete)
            val dw_no = dw.view().findViewById<Button>(R.id.button_dialog_no)
            val dw_logo = dw.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
            val dw_progres = dw.view().findViewById<ProgressBar>(R.id.progressBar)
            dw_progres.visibility = View.VISIBLE


            Slot(Main.context, "dw_progres").onRun {
                val totalBytes = it.getStringExtra("totalBytes")
                val readBytes = it.getStringExtra("readBytes")
                dw_progres.max = totalBytes.toInt()
                dw_progres.progress = readBytes.toInt()

                if (totalBytes == readBytes) {
                    if (totalBytes == "0") {
                        dw_logo.text = "Отменено,попробовать еще раз?"
                        dw_no.text = "Нет"
                        dw_start.visibility = View.VISIBLE
                    } else {
                        dw_logo.text = "Готово,сохранено в папке программы"
                        dw_start.visibility = View.VISIBLE
                        dw_start.text = "Открыть файл"
                        dw_no.text = "Нет"
                    }
                }
            }


            dw_logo.text = "Попробовать скачать?"

            dw_start.onClick {
                if (dw_logo.text == "Готово,сохранено в папке программы") {
                    //попробуем его открыть
                    Main.play_aimp_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
                } else {
                    dw_start.visibility = View.GONE
                    Main.download_file(url, name + "." + url.substringAfterLast('.'), "anim_online_plalist")
                    dw_logo.text = "Идёт загрузка..."
                    dw_no.text = "Отмена"
                }
            }
            dw_start.onLongClick {
                if (dw_logo.text == "Готово,сохранено в папке программы") {
                    Main.play_system_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
                }
            }
            dw_no.onClick {
                if (dw_no.text == "Отмена") {
                    signal("dw_cansel").send(Main.context)
                } else {
                    dw.close()
                }
            }
        }

    }

}