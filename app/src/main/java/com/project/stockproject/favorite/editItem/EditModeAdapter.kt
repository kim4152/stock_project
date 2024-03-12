package com.project.stockproject.favorite.editItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.databinding.EditModeItemBinding

class EditModeAdapter(
    var adapterList:ArrayList<EditModeItem>,
    val onClick: (EditModeItem) -> Unit,
    val viewModel: MyViewModel,
    val folderName: String
) : RecyclerView.Adapter<EditModeAdapter.ViewHolder>(),
    ItemTouchHelperListener {

    inner class ViewHolder(val binding: EditModeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EditModeItem) {
            binding.root.setOnClickListener { onClick(item) }
            binding.checkbox.isChecked = item.isChecked
            binding.textView.text = item.stockName
        }
    }
    override fun onItemMove(from: Int, to: Int): Boolean {
        val data = adapterList[from]
        //리스트 갱신
        adapterList.removeAt(from)
        adapterList.add(to,data)

        // from에서 to 위치로 아이템 위치 변경
        notifyItemMoved(from,to)
        return true
    }


    override fun updateItemOrder() {
         adapterList.forEachIndexed { index, editModeItem ->

             viewModel.itemMove(folderName,editModeItem.stockName,index)
         }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EditModeItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapterList[position])
    }

}

interface ItemTouchHelperListener {
    fun onItemMove(from: Int, to: Int): Boolean
    fun updateItemOrder()
}