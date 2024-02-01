package com.project.stockproject.stockInform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.stockproject.stockInform.tabFragment.TabfifthFragment
import com.project.stockproject.stockInform.tabFragment.TabfirstFragment
import com.project.stockproject.stockInform.tabFragment.TabfourthFragment
import com.project.stockproject.stockInform.tabFragment.TabsecondFragment
import com.project.stockproject.stockInform.tabFragment.TabthirdFragment

class TabLayoutAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

        

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabfirstFragment()
            1 -> TabsecondFragment()
            2 -> TabthirdFragment()
            3 -> TabfourthFragment()
            4 -> TabfifthFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}