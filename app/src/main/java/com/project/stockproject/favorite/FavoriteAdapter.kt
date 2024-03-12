package com.project.stockproject.favorite

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.stockproject.room.FolderTable

class FavoriteAdapter(
    private val adapter: List<FolderTable>,fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun createFragment(position: Int): Fragment {
        if (adapter.isNotEmpty()){
            Log.d("dsfsadfasdf",position.toString())
            return SubFragment(adapter[position%adapter.size])
        }else{
            return SubFragment(adapter[0])
        }
    }
}