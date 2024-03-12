package com.project.stockproject.favorite

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.project.stockproject.MyViewModel
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FavoriteViewpagerBinding
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.ItemTable

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