package com.project.stockproject.home.multiFactor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.stockproject.home.MFItem

class MultiFactorViewPager(private val fragmentActivity: FragmentActivity,
    val list: List<MFItem>
    )
    :FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        //0,1,2,3
        return MFSubFragment(list.subList(position*5,(position*5)+5),position)
    }
}