package com.project.stockproject.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.databinding.EditItemBinding
import com.project.stockproject.databinding.FavoriteDialogItemBinding
import com.project.stockproject.room.FolderTable

class EditAdapter(private val onClick:(EditItem)->Unit) : ListAdapter<EditItem, EditAdapter.ViewHolder>(diffUtil) {


    inner class ViewHolder(val binding: EditItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: EditItem){
            binding.textView.text=item.folderName
            binding.checkBox.isChecked = item.isSelected

            binding.root.setOnClickListener {
                onClick(item)
            }


        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EditItemBinding.inflate(
                LayoutInflater.from(parent.context)
                ,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<EditItem>(){
            override fun areItemsTheSame(oldItem: EditItem, newItem: EditItem): Boolean {
                return oldItem==newItem //hash cod 비교
            }

            override fun areContentsTheSame(oldItem: EditItem, newItem: EditItem): Boolean {
                return oldItem==newItem
            }

        }
    }

}