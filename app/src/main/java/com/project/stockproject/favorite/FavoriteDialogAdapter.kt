package com.project.stockproject.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.R
import com.project.stockproject.databinding.FavoriteDialogItemBinding

class FavoriteDialogAdapter(private val onClick:(CustomDialogItem)->Unit, val context: Context) : ListAdapter<CustomDialogItem, FavoriteDialogAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: FavoriteDialogItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CustomDialogItem){
            binding.textView.text=item.folderName
            if (item.isChecked){
                binding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.secondaryContainer))
            }
            else{
                binding.root.setBackgroundColor(ContextCompat.getColor(context,R.color.surface))
            }
            binding.root.setOnClickListener {
                onClick(item)
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
        val diffUtil = object : DiffUtil.ItemCallback<CustomDialogItem>(){
            override fun areItemsTheSame(oldItem: CustomDialogItem, newItem: CustomDialogItem): Boolean {
                return oldItem==newItem //hash cod 비교
            }

            override fun areContentsTheSame(oldItem: CustomDialogItem, newItem: CustomDialogItem): Boolean {
                return oldItem==newItem
            }

        }
    }

}