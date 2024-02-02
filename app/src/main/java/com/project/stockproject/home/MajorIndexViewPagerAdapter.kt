package com.project.stockproject.home

import com.project.stockproject.R
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.common.MyApplication.getAppContext
import com.project.stockproject.databinding.MajorIndexItemBinding


class MajorIndexViewPagerAdapter(private val adapter: List<MajorIndexViewPagerDTO>) :
    RecyclerView.Adapter<MajorIndexViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MajorIndexItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapter[position])
    }

    inner class ViewHolder(private val viewBinding: MajorIndexItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MajorIndexViewPagerDTO) {
            viewBinding.apply {

                val title = when (item.stockName) {
                    "KOSPI" -> {
                        "코스피"
                    }

                    "KOSDAQ" -> {
                        "코스닥"
                    }

                    "KPI200" -> {
                        "코스피200"
                    }

                    else -> {
                        ""
                    }
                }
                stockName.text = title
                presentPrice.text = item.presentPrice
                when(item.fluctuationRate.substring(0,1)){
                    "+"->{arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)}
                    "-"->{arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)}
                }
                rateDown.text = item.rateDown
                fluctuationRate.text = when(item.fluctuationRate.substring(0,1)){
                    "+"->{" (${item.fluctuationRate.substring(1)})"}
                    "-"->{" (${item.fluctuationRate.substring(1)})"}
                    else->{" (${item.fluctuationRate})"}
                }



                //지수 : 양봉/음봉 text색 설정
                if (item.fluctuationRate.substring(0, 1) == "+") {

                    arrow.setColorFilter(ContextCompat.getColor(getAppContext(),R.color.up))
                    presentPrice.setTextColor(ContextCompat.getColor(getAppContext(),R.color.up))
                    rateDown.setTextColor(ContextCompat.getColor(getAppContext(),R.color.up))
                    fluctuationRate.setTextColor(ContextCompat.getColor(getAppContext(),R.color.up))
                } else if (item.fluctuationRate.substring(0, 1) == "-") {
                    arrow.setColorFilter(ContextCompat.getColor(getAppContext(),R.color.down))
                    presentPrice.setTextColor(ContextCompat.getColor(getAppContext(),R.color.down))
                    rateDown.setTextColor(ContextCompat.getColor(getAppContext(),R.color.down))
                    fluctuationRate.setTextColor(ContextCompat.getColor(getAppContext(),R.color.down))
                }
            }
        }
    }


}

data class MajorIndexViewPagerDTO(
    val stockName: String,
    val date: String,
    val presentPrice: String,
    val rateDown: String,
    val fluctuationRate: String,
)

