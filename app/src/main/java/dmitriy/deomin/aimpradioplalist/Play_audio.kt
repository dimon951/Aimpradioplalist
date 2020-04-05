package dmitriy.deomin.aimpradioplalist

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.LinearLayout
import dmitriy.deomin.aimpradioplalist.`fun`.file.is_existence_file
import dmitriy.deomin.aimpradioplalist.`fun`.formatTimeToEnd
import dmitriy.deomin.aimpradioplalist.`fun`.putText_сlipboard
import dmitriy.deomin.aimpradioplalist.`fun`.windows.download_file_window
import dmitriy.deomin.aimpradioplalist.`fun`.windows.menu_saved_file
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast



class Play_audio(name: String, url: String, context: Context = Main.context) {

    init {
        val plaer = DialogWindow(context, R.layout.plaer, true)

        //анимация при запуске
        val progressBar = plaer.view().findViewById<ProgressBar>(R.id.progressBar_load_audio)
        progressBar.visibility = View.GONE

        //прогресс бар воспроизведения
        val seekBar = plaer.view().findViewById<SeekBar>(R.id.seekBar)

        plaer.view().findViewById<TextView>(R.id.info).text = name

        val time = plaer.view().findViewById<TextView>(R.id.time)

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

        class UpdateSeekBarThread() : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                val currentPosition: Int = vv.currentPosition
                time.text = formatTimeToEnd(vv.duration.toLong()) + "/" + formatTimeToEnd(currentPosition.toLong())
                seekBar.max = vv.duration
                seekBar.progress = currentPosition
                threadHandler.postDelayed(this, 50)
            }

        }

        btnplay!!.setOnClickListener {
            if (!vv.isPlaying) {
                progressBar!!.visibility = View.VISIBLE

                vv.setVideoURI(uri)
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
                if (vv.duration > seekBar.progress) {
                    vv.seekTo(seekBar.progress)
                }
            }

        })


        //---------------------download--------------------------------------------------
        plaer.view().findViewById<Button>(R.id.download).onClick {
            //проврим существование файла
            val file_pach = Main.ROOT + name + "." + url.substringAfterLast('.')
            if (is_existence_file(file_pach)) {
                //если файл уже скачен , покажем меню для скаченого файла
                menu_saved_file(context, file_pach)
            } else {
                //иначе скачаем его
                download_file_window(context,name,url)
            }
            plaer.close()
        }
        //-------------------------------------------------------------------------------
    }




}
