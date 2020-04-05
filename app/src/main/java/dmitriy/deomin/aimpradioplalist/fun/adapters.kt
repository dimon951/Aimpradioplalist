package dmitriy.deomin.aimpradioplalist.`fun`

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dmitriy.deomin.aimpradioplalist.Moy_plalist
import dmitriy.deomin.aimpradioplalist.Online_plalist
import dmitriy.deomin.aimpradioplalist.Pop_radio
import dmitriy.deomin.aimpradioplalist.Vse_radio

//адаптер главного вьюпейджера
class Adapter_main_viewpager(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return Vse_radio()
            1 -> return Pop_radio()
            2 -> return Moy_plalist()
            3 -> return Online_plalist()
        }
        return Pop_radio()
    }

    override fun getCount(): Int {
        return 4
    }
}