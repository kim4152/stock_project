package com.project.stockproject.home.tabLayout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.ItemSubTabBinding
import com.project.stockproject.home.Predic
import com.project.stockproject.stockInform.StockOutput
import java.text.DecimalFormat

class RankingAdapter(val onClick:(Predic)->Unit) :
    ListAdapter<Predic, RankingAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: ItemSubTabBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Predic, position: Int) {
            binding.root.setOnClickListener { onClick(item) }
            if (item.stock_predic_rate > 0) {
                val color = ContextCompat.getColor(MyApplication.getAppContext(), R.color.up)
                binding.arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)//이미지 설정
                setItem(binding, color, item)
            } else if (item.stock_predic_rate < 0) {
                val color = ContextCompat.getColor(MyApplication.getAppContext(), R.color.down)
                binding.arrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)//이미지 설정
                setItem(binding, color, item)
            } else {
                val color = ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)
                setItem(binding, color, item)
            }

            binding.stockName.text = item.stock_name

            binding.number.text = position.toString()
        }
    }

    private fun setItem(binding: ItemSubTabBinding, color: Int, item: Predic) {
        val a = DecimalFormat("#,###")
        val b = DecimalFormat("#.##")

        binding.arrow.setColorFilter(color)

        binding.price.text = a.format(item.stock_predic_price.toInt()).toString()
        binding.price.setTextColor(color)

        binding.rate.text = b.format(item.stock_predic_rate).toString()+"%"
        binding.rate.setTextColor(color)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Predic>() {
            override fun areItemsTheSame(oldItem: Predic, newItem: Predic): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Predic, newItem: Predic): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], position + 1)
    }
}