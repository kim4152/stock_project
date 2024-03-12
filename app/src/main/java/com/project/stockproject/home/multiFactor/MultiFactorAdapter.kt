package com.project.stockproject.home.multiFactor

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.common.MyApplication.context
import com.project.stockproject.databinding.ItemMultiFactorBinding
import com.project.stockproject.home.MFItem
import java.text.DecimalFormat

class MultiFactorAdapter(
    val onClick: (MFItem)->Unit,val index:Int,
    val viewModel:MyViewModel,val lifecycleOwner: LifecycleOwner
) : ListAdapter<MFItem, MultiFactorAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: ItemMultiFactorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MFItem,position: Int) {
            binding.root.setOnClickListener { onClick(item) }//종목 클릭시
            binding.number.text=position.toString()
            binding.stockName.text = item.stock_name
            Log.d("dafdfdf","${position}: ${item.stock_name}")
            showNowPrice(binding, item)
        }
    }

    private fun showNowPrice(binding: ItemMultiFactorBinding, item:MFItem) {
        val stockCode = item.stock_code
        val df = DecimalFormat("#,###") //세자리마다 콤마 찍기
        binding.apply {

            viewModel.stockInform(stockCode).observe(lifecycleOwner, Observer {
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
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MFItem>() {
            override fun areItemsTheSame(oldItem: MFItem, newItem: MFItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MFItem, newItem: MFItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMultiFactorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position],5*index+position+1)
    }
}