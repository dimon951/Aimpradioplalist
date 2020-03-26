package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.LinearLayout
import dmitriy.deomin.aimpradioplalist.`fun`.download_file
import dmitriy.deomin.aimpradioplalist.`fun`.formatTimeToEnd
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.`fun`.putText_сlipboard
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.sdk27.coroutines.onSeekBarChangeListener
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit


class Play_audio(name: String, url: String,context: Context = Main.context) {

    init {
        val plaer = DialogWindow(context, R.layout.plaer, true)

        //анимация при запуске
        val progressBar = plaer.view().findViewById<ProgressBar>(R.id.progressBar_load_audio)
        progressBar.visibility = View.GONE

        //прогресс бар воспроизведения
        val seekBar = plaer.view().findViewById<SeekBar>(R.id.seekBar)

        plaer.view().findViewById<TextView>(R.id.info).text = name

        val time =plaer.view().findViewById<TextView>(R.id.time)

        val text_name_i_url = plaer.view().findViewById<TextView>(R.id.text_logo)
        text_name_i_url.text = url
        text_name_i_url.onClick {
            text_name_i_url.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myalpha))
            putText_сlipboard(url, context)
            context.toast("url скопирован в буфер")
        }



        val btnplay = plaer.view().findViewById<Button>(R.id.go)
        val vv = plaer.view().findViewById<VideoView>(R.id.videoView)

        val param_full = LinearLayout.LayoutParams( /*width*/
                ViewGroup.LayoutParams.MATCH_PARENT,  /*height*/
                ViewGroup.LayoutParams.MATCH_PARENT,  /*weight*/
                1.0f
        )
        val param_smol = vv.layoutParams

        plaer.view().findViewById<Button>(R.id.full_scren).onClick {
            vv.layoutParams = param_full
        }

        vv.onClick {
            vv.layoutParams = param_smol
        }

        val mediacontroller = MediaController(context)
        val uri = Uri.parse(url)

        vv!!.setOnCompletionListener {
            vv.pause()
           btnplay.setBackgroundDrawable(context.resources.getDrawable(R.drawable.iconka_play))
        }

        val threadHandler = Handler()
        class UpdateSeekBarThread(): Runnable{
            @SuppressLint("SetTextI18n")
            override fun run() {
                val currentPosition: Int = vv.currentPosition
                time.text= formatTimeToEnd(vv.duration.toLong())+"/"+formatTimeToEnd(currentPosition.toLong())
                seekBar.max = vv.duration
                seekBar.progress = currentPosition
                threadHandler.postDelayed(this, 50)
            }

        }

        btnplay!!.setOnClickListener {
            if (!vv.isPlaying) {
                progressBar!!.visibility = View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    vv.setVideoURI(uri, mapOf("user-agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36"))
                } else {
                    vv.setVideoURI(uri)
                }
                vv.setMediaController(mediacontroller)
                vv.requestFocus(0)
                vv.start()
                // Create a thread to update position of SeekBar.
                val updateSeekBarThread = UpdateSeekBarThread()
                threadHandler.postDelayed(updateSeekBarThread, 500)
                btnplay.setBackgroundDrawable(context.resources.getDrawable(R.drawable.iconka_pausa))
            } else {
                vv.pause()
                btnplay.setBackgroundDrawable(context.resources.getDrawable(R.drawable.iconka_play))
            }

        }

        vv.setOnPreparedListener { progressBar!!.visibility = View.GONE }

        plaer.view().findViewById<Button>(R.id.close).onClick {
            if (vv.isPlaying) {
                vv.stopPlayback()
            }
            plaer.close()
        }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                if(vv.duration>seekBar.progress){
                    vv.seekTo(seekBar.progress)
                }
            }
        })




        //---------------------download--------------------------------------------------
        plaer.view().findViewById<Button>(R.id.download).onClick {

            val dw = DialogWindow(context, R.layout.dialog_delete_stancii, true)
            val dw_start = dw.view().findViewById<Button>(R.id.button_dialog_delete)
            val dw_no = dw.view().findViewById<Button>(R.id.button_dialog_no)
            val dw_logo = dw.view().findViewById<TextView>(R.id.text_voprosa_del_stncii)
            val dw_progres = dw.view().findViewById<ProgressBar>(R.id.progressBar)
            dw_progres.visibility = View.VISIBLE


            Slot(context, "dw_progres").onRun {
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


            dw_logo.text = "Попробовать скачать?\n(не работает для потока)"

            dw_start.onClick {
                if (dw_logo.text == "Готово,сохранено в папке программы") {
                    //попробуем его открыть
                    play_aimp_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
                } else {
                    dw_start.visibility = View.GONE
                    download_file(url, name + "." + url.substringAfterLast('.'), "anim_online_plalist")
                    dw_logo.text = "Идёт загрузка..."
                    dw_no.text = "Отмена"
                }
            }
            dw_start.onLongClick {
                if (dw_logo.text == "Готово,сохранено в папке программы") {
                    play_system_file(Main.ROOT + name + "." + url.substringAfterLast('.'))
                }
            }
            dw_no.onClick {
                if (dw_no.text == "Отмена") {
                    signal("dw_cansel").send(context)
                } else {
                    dw.close()
                }
            }
        }
        //-------------------------------------------------------------------------------
    }
}
