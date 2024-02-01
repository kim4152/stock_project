package com.project.stockproject.search

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.R
import com.project.stockproject.databinding.HistoryItemBinding
import com.project.stockproject.databinding.SearchItemBinding

class SearchAdapter(val onCLick: (Search) -> Unit, val onStarClick: (Search) -> Unit) :
    ListAdapter<Search, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Search.TYPE_RESULT -> {
                SearchViewHolder(
                    SearchItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            Search.TYPE_HISTORY -> {
                HistoryViewHolder(
                    HistoryItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchViewHolder -> {
                holder.bind(currentList[position])
            }
            is HistoryViewHolder -> {
                holder.bind(currentList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].getType()
    }


    //search viewHolder
    inner class SearchViewHolder(private val viewBinding: SearchItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: Search) {
            viewBinding.stockName.text = item.stockName
            viewBinding.stockCode.text = item.stockCode
            viewBinding.root.setOnClickListener { onCLick(item) }
            viewBinding.star.setOnClickListener { onStarClick(item) }
        }
    }

    //history viewHolder
    inner class HistoryViewHolder(private val viewBinding: HistoryItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: Search) {
            viewBinding.stockName.text = item.stockName
            viewBinding.stockCode.text = item.stockCode
            viewBinding.root.setOnClickListener { onCLick(item) }
            viewBinding.star.setOnClickListener { onStarClick(item) }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Search>() {
            override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
                return oldItem.stockCode == newItem.stockCode //User의 id는 고유값이기 때문에 ==
            }


            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
                return oldItem == newItem //User가 dataClass이기 때문에 ==
                //만약 일반class였으면 equals 오버라이딩
            }

        }
    }


}