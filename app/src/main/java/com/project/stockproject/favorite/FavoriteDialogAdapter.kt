package com.project.stockproject.favorite

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.databinding.FavoriteDialogItemBinding
import com.project.stockproject.databinding.NewsItemBinding
import com.project.stockproject.room.FolderTable
import com.project.stockproject.stockInform.tabFragment.NewsAdapter
import com.project.stockproject.stockInform.tabFragment.NewsModel
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteDialogAdapter(private val onClick:(String)->Unit) : ListAdapter<FolderTable, FavoriteDialogAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: FavoriteDialogItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: FolderTable){
            binding.textView.text=item.folderName
            binding.root.setOnClickListener {

            }


        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavoriteDialogItemBinding.inflate(
                LayoutInflater.from(parent.context)
                ,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<FolderTable>(){
            override fun areItemsTheSame(oldItem: FolderTable, newItem: FolderTable): Boolean {
                return oldItem==newItem //hash cod 비교
            }

            override fun areContentsTheSame(oldItem: FolderTable, newItem: FolderTable): Boolean {
                return oldItem==newItem
            }

        }
    }

}