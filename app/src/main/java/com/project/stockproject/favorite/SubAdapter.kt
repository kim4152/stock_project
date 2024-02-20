package com.project.stockproject.favorite

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.SubadapterItemBinding
import com.project.stockproject.search.SearchHistoryManager
import java.text.DecimalFormat
import java.util.Collections


class SubAdapter(
    val adapter: List<SubFragment.SubItem>, val viewModel: MyViewModel,
    val context: SubFragment,
    val editMode:()->Unit,
    val subOnClick: (SubFragment.SubItem) -> Unit
) : RecyclerView.Adapter<SubAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SubadapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubFragment.SubItem) {

            binding.apply {
                binding.root.setOnClickListener {
                        subOnClick(item)
                }

                textView.text = item.stockName

                //현재가 표현
                showNowPrice(binding, item)

                binding.root.setOnLongClickListener {
                    editMode()
                    true
                }
            }
        }
    }

    //관심종목에서 현재가 보여주기 일때 표현
    private fun showNowPrice(binding: SubadapterItemBinding, item: SubFragment.SubItem) {
        val stockCode = item.stockCode
        val df = DecimalFormat("#,###") //세자리마다 콤마 찍기
        binding.apply {
            viewModel.stockInform(stockCode).observe(context, Observer {
                if (it!=null){
                    price.text = df.format(it.stck_prpr.toInt())//현재가
                    if (it.prdy_vrss.substring(0, 1) == "-") { //음봉
                        arrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)//이미지 설정
                        arrow.setColorFilter(
                            ContextCompat.getColor
                                (MyApplication.getAppContext(), R.color.down)
                        )//이미지 색
                        differ.text = df.format(it.prdy_vrss.substring(1).toInt())//가격 차이
                        rate.text = "${it.prdy_ctrt.substring(1)}%" //비율
                    } else if (it.prdy_vrss.toFloat() > 0f) { //양봉
                        arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)//이미지 설정
                        arrow.setColorFilter(
                            ContextCompat.getColor
                                (MyApplication.getAppContext(), R.color.up)
                        )//이미지 색
                        differ.text = df.format(it.prdy_vrss.toInt())//가격 차이
                        rate.text = "${it.prdy_ctrt}%" //비율
                    } else {
                        differ.text = df.format(it.prdy_vrss.toInt())//가격 차이
                        rate.text = "${it.prdy_ctrt}%" //비율
                    }

                    vol.text = df.format(it.acml_vol.toInt())//거래량

                    //지수 : 양봉/음봉 text색 설정
                    if (it.prdy_vrss.substring(0, 1) == "-") {
                        price.setTextColor(
                            ContextCompat.getColor(
                                MyApplication.getAppContext(),
                                R.color.down
                            )
                        )//현재가 색 설정

                        differ.setTextColor(
                            ContextCompat.getColor
                                (MyApplication.getAppContext(), R.color.down)
                        ) //차이

                        rate.setTextColor(
                            ContextCompat.getColor(
                                MyApplication.getAppContext(),
                                R.color.down
                            )
                        )// 비율
                    } else if (it.prdy_vrss.toFloat() > 0f) {
                        price.setTextColor(
                            ContextCompat.getColor(
                                MyApplication.getAppContext(),
                                R.color.up
                            )
                        )//현재가 색 설정

                        differ.setTextColor(
                            ContextCompat.getColor
                                (MyApplication.getAppContext(), R.color.up)
                        ) //차이

                        rate.setTextColor(
                            ContextCompat.getColor(
                                MyApplication.getAppContext(),
                                R.color.up
                            )
                        )// 비율
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAdapter.ViewHolder {
        return ViewHolder(
            SubadapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SubAdapter.ViewHolder, position: Int) {
        holder.bind(adapter[position])
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

}