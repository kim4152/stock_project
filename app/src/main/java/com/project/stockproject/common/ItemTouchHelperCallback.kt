package com.project.stockproject.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.project.stockproject.favorite.editItem.ItemTouchHelperListener

class ItemTouchHelperCallback(
    private val listener: ItemTouchHelperListener
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = UP or DOWN // 위아래로 이동
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return listener.onItemMove(viewHolder.adapterPosition,target.adapterPosition)
    }

    //사용자에 의해 swipe될 때 호출
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        listener.updateItemOrder()
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }



}
