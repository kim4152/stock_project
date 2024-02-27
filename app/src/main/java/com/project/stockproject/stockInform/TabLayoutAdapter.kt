package com.project.stockproject.stockInform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.stockproject.stockInform.tabFragment.TabYoutubeFragment
import com.project.stockproject.stockInform.tabFragment.TabChartFragment
import com.project.stockproject.stockInform.tabFragment.TabOrderFragment
import com.project.stockproject.stockInform.tabFragment.TabDiscussionFragment

class TabLayoutAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

        

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabChartFragment()
            1 -> TabOrderFragment()
            2 -> TabDiscussionFragment()
            3 -> TabYoutubeFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}