package dmitriy.deomin.aimpradioplalist.`fun`.windows

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import dmitriy.deomin.aimpradioplalist.Main
import dmitriy.deomin.aimpradioplalist.R
import dmitriy.deomin.aimpradioplalist.`fun`.file.deleteAllFilesFolder
import dmitriy.deomin.aimpradioplalist.`fun`.file.getDirSize
import dmitriy.deomin.aimpradioplalist.`fun`.file.long_size_to_good_vid
import dmitriy.deomin.aimpradioplalist.`fun`.file.read_all_files_to_list
import dmitriy.deomin.aimpradioplalist.`fun`.selekt_CATEGORIA_ONLINE_PLALIST
import dmitriy.deomin.aimpradioplalist.adapters.Adapter_history_online_plalist
import dmitriy.deomin.aimpradioplalist.custom.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun history_online_plalist(context: Context, list_history: ArrayList<HistoryNav>, v: View) {
    val hop = DialogWindow(context, R.layout.history_online_plalist)

    //слот будет закрывать это окно
    Slot(context, "History_online_plalist").onRun { hop.close() }

    //настройка вида----------------------------------------------------------------------------
    val fon = hop.view().findViewById<LinearLayout>(R.id.fon)
    fon.setBackgroundColor(Main.COLOR_FON)

    val recikl = hop.view().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recikl)
    recikl.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

    //полоса быстрой прокрутки
    val fS = hop.view().findViewById<VerticalRecyclerViewFastScroller>(R.id.fast_scroller)
    //получим текущие пораметры
    val paramL = fS.layoutParams
    //меняем ширину
    paramL.width = Main.SIZE_WIDCH_SCROLL
    //устанавливаем
    fS.layoutParams = paramL
    fS.setRecyclerView(recikl)
    recikl.setOnScrollListener(fS.onScrollListener)
    //-------------------------------------------------------------------------------------------

    val d = ArrayList<History>()
    val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss", Locale.getDefault())

    val kesh = read_all_files_to_list()

    for (l in kesh.iterator()) {
        val f = File(l)
        if (f.isFile) {
            d.add(History(f.name, f.absolutePath, sdf.format(f.lastModified()), long_size_to_good_vid(f.length().toDouble())))
        }
    }


    val a = Adapter_history_online_plalist(d)
    recikl.adapter = a


    //покажем полный размер кеша
    //кнопка очистить кеш
    //при открытии меню будем показыват размер этого кеша
    val b_c = hop.view().findViewById<Button>(R.id.button_clear_kesh)

    //получим размер
    val file = File(Main.ROOT)
    if (file.exists()) {
        val s = getDirSize(file)
        if (s > Main.SIZEFILETHEME) {
            b_c.text = "Очистить Кэш (" + long_size_to_good_vid(s.toDouble()) + ")"
        } else {
            b_c.text = "Кэш очищен"

        }
    } else
        b_c.text = "Кэш очищен"

    b_c.onClick {

        if (b_c.text == "Кэш очищен") {

            signal("History_online_plalist").send(context)
            signal("Online_plalist")
                    .putExtra("update", "zaebis")
                    .send(context)
            Main.context.toast("Пусто")

        } else {

            //покажем предупреждающее окошко
            val v_d = DialogWindow(Main.context, R.layout.dialog_delete_plalist)

            v_d.view().findViewById<TextView>(R.id.text_voprosa_del_stncii).text = "Удалить Кеш?"

            v_d.view().findViewById<Button>(R.id.button_dialog_delete).onClick {
                v_d.close()
                //удаляем все
                if (file.exists()) {
                    //файлы
                    deleteAllFilesFolder(Main.ROOT)
                    //список этих файлов
                    hop.close()
                    //очистим историю навигации
                    //------------------------------------------
                    list_history.clear()
                    signal("history_save").send(context)
                    //сбросим на кнопках
                    selekt_CATEGORIA_ONLINE_PLALIST("del", v)
                    //--------------------------------------------
                    //обновим список
                    //иначе пустую страницу покажем
                    signal("Online_plalist")
                            .putExtra("update", "zaebis")
                            .send(context)
                    signal("History_online_plalist").send(context)
                }
                if (file.exists()) {
                    val s = getDirSize(file)
                    if (s > Main.SIZEFILETHEME) {
                        b_c.text = "Очистить Кэш (" + long_size_to_good_vid(s.toDouble()) + ")"
                    } else {
                        b_c.text = "Кэш очищен"
                    }

                } else {
                    b_c.text = "Кэш очищен"
                }

            }
            v_d.view().findViewById<Button>(R.id.button_dialog_no).onClick {
                v_d.close()
            }

        }

    }
    //---------------------------------------
}