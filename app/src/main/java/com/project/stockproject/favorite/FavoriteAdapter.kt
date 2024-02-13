package com.project.stockproject.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.databinding.FavoriteViewpagerBinding
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.ItemTable

class FavoriteAdapter(private val adapter: List<FolderTable>,val myViewModel: MyViewModel,val context: FavoriteFragment) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private lateinit var subAdapter:SubAdapter
    data class SubItem(
        val stockName: String,
        val stockCode: String,
    )
    fun List<ItemTable>.transform() : List<SubItem>{
        return this.map {
            SubItem(
                stockName = it.itemName,
                stockCode = it.itemCode,
            )
        }
    }

    inner class ViewHolder(private val viewBinding: FavoriteViewpagerBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: FolderTable) {
            setAdapter(viewBinding.recyclerView,item,context)
        }
    }

    private fun setAdapter(recyclerView: RecyclerView, item: FolderTable, af: FavoriteFragment) {
        subAdapter = SubAdapter(
            item.folderName,myViewModel,context
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subAdapter
        }
        myViewModel.getAllItems(item.folderName)
        myViewModel.getAllItemsResult.observe(context, Observer { it->
            subAdapter.submitList(it.transform())
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavoriteViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapter[position])
    }
}