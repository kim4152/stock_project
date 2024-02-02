package com.project.stockproject.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.databinding.FavoriteViewpagerBinding
import com.project.stockproject.room.FolderTable

class FavoriteAdapter(private val adapter:List<FolderTable> ) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    inner class ViewHolder(private val viewBinding: FavoriteViewpagerBinding)
        :RecyclerView.ViewHolder(viewBinding.root){
        fun bind(item:FolderTable){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavoriteViewpagerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapter[position])
    }
}