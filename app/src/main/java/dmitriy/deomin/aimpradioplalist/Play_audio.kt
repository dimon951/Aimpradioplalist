package dmitriy.deomin.aimpradioplalist
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import dmitriy.deomin.aimpradioplalist.`fun`.download_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_aimp_file
import dmitriy.deomin.aimpradioplalist.`fun`.play.play_system_file
import dmitriy.deomin.aimpradioplalist.`fun`.putText_сlipboard
import dmitriy.deomin.aimpradioplalist.custom.DialogWindow
import dmitriy.deomin.aimpradioplalist.custom.Slot
import dmitriy.deomin.aimpradioplalist.custom.send
import dmitriy.deomin.aimpradioplalist.custom.signal
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.toast


class Play_audio(name: String, url: String) {
    init {
        val plaer = DialogWindow(Main.context, R.layout.plaer, true)

        val progressBar = plaer.view().findViewById<ProgressBar>(R.id.progressBar_load_audio)
        progressBar.visibility = View.GONE

        plaer.view().findViewById<TextView>(R.id.text_logo).text = url
        plaer.view().findViewById<TextView>(R.id.text_logo).onClick {
            putText_сlipboard(url, Main.context)
            Main.context.toast("url скопирован в буфер")
        }



        plaer.view().findViewById<TextView>(R.id.info).text = name

        val btnplay = plaer.view().findViewById<Button>(R.id.go)
        val vv = plaer.view().findViewById<VideoView>(R.id.videoView)

        val param_full = LinearLayout.LayoutParams( /*width*/
                ViewGroup.LayoutParams.MATCH_PARENT,  /*height*/
                ViewGroup.LayoutParams.MATCH_PARENT,  /*weight*/
                1.0f
        )
        val param_smol =vv.layoutParams

        plaer.view().findViewById<Button>(R.id.full_scren).onClick {
            vv.layoutParams = param_full
        }

        vv.onClick {
            vv.layoutParams = param_smol
        }

        val mediacontroller = MediaController(Main.context)
        mediacontroller.setAnchorView(vv)
        val uri = Uri.parse(url)

        vv!!.setOnCompletionListener {
            vv.pause()
            btnplay.setBackgroundResource(android.R.drawable.ic_media_play)
        }

        btnplay!!.setOnClickListener {
            if (!vv.isPlaying) {
                progressBar!!.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    vv.setVideoURI(uri, mapOf("user-agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36"))
                }else{
                    vv.setVideoURI(uri)
                }
                vv.setMediaController(mediacontroller)
                vv.requestFocus()
                vv.start()
                btnplay.setBackgroundResource(android.R.drawable.ic_media_pause)
            } else {
                vv.pause()
                btnplay.setBackgroundResource(android.R.drawable.ic_media_play)
            }

        }

        vv.setOnPreparedListener { progressBar!!.visibility = View.GONE }

        plaer.view().findViewById<Button>(R.id.close).onClick {
            if (vv.isPlaying) {
                vv.stopPlayback()
            }
            plaer.close()
        }

        //---------------------download--------------------------------------------------
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
                    signal("dw_cansel").send(Main.context)
                } else {
                    dw.close()
                }
            }
        }
        //-------------------------------------------------------------------------------
    }

}