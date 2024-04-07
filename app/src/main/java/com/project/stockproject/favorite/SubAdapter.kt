package com.project.stockproject.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.SubadapterItemBinding
import java.text.DecimalFormat
import kotlin.math.abs


class SubAdapter(
    val adapter: List<SubFragment.SubItem>, val viewModel: MyViewModel,
    val context: SubFragment,val isChecked:String,
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
        val df2 = DecimalFormat("#.##")

        val colorDown = ContextCompat.getColor(MyApplication.getAppContext(), R.color.down)
        val colorUp = ContextCompat.getColor(MyApplication.getAppContext(), R.color.up)

        binding.apply {
            if (isChecked=="predic"){ //예측가 체크
                   viewModel.getPredicEach(stockCode).observe(context, Observer {
                       if (it!=null){
                           //가격차이
                           val prdy_vrss = makeDiffer(it.stock_predic_price,it.stock_predic_rate)
                           price.text = df.format(it.stock_predic_price.toInt()) //현재가
                           vol.text = "" //거래량

                            if (it.stock_predic_rate>0){ //전일대비 부호
                                arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)//이미지 설정
                                arrow.setColorFilter(colorUp)//이미지 색
                                differ.text = df.format(prdy_vrss.toInt())//가격 차이
                                rate.text = "${df2.format(it.stock_predic_rate)}%" //비율
                            }else if(it.stock_predic_rate<0){
                                arrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)//이미지 설정
                                arrow.setColorFilter(colorDown)//이미지 색
                                differ.text = df.format(prdy_vrss.toInt())//가격 차이
                                rate.text = "${df2.format(it.stock_predic_rate).toString().substring(1)}%" //비율
                            }else {
                                differ.text = df.format(prdy_vrss.toInt())//가격 차이
                                rate.text = "${df2.format(it.stock_predic_rate)}%" //비율
                            }

                           //지수 : 양봉/음봉 text색 설정
                           if (it.stock_predic_rate<0f) {
                               price.setTextColor(colorDown)//현재가 색 설정

                               differ.setTextColor(colorDown) //차이

                               rate.setTextColor(colorDown)// 비율
                           } else if (it.stock_predic_rate > 0f) {
                               price.setTextColor(colorUp) //현재가 색 설정

                               differ.setTextColor(colorUp) //차이

                               rate.setTextColor(colorUp) // 비율
                           }


                       }
                   })
            }else{ //현재가 체크
                viewModel.stockInform(stockCode).observe(context, Observer {
                    if (it!=null){

                        price.text = df.format(it.stck_prpr.toInt())//현재가
                        vol.text = df.format(it.acml_vol.toInt())//거래량

                        if (it.prdy_vrss.substring(0, 1) == "-") { //음봉
                            arrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)//이미지 설정
                            arrow.setColorFilter(colorDown)//이미지 색
                            differ.text = df.format(it.prdy_vrss.substring(1).toInt())//가격 차이
                            rate.text = "${it.prdy_ctrt.substring(1)}%" //비율
                        } else if (it.prdy_vrss.toFloat() > 0f) { //양봉
                            arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)//이미지 설정
                            arrow.setColorFilter(colorUp)//이미지 색
                            differ.text = df.format(it.prdy_vrss.toInt())//가격 차이
                            rate.text = "${it.prdy_ctrt}%" //비율
                        } else {
                            differ.text = df.format(it.prdy_vrss.toInt())//가격 차이
                            rate.text = "${it.prdy_ctrt}%" //비율
                        }

                        //지수 : 양봉/음봉 text색 설정
                        if (it.prdy_vrss.substring(0, 1) == "-") {
                            price.setTextColor(colorDown)//현재가 색 설정

                            differ.setTextColor(colorDown) //차이

                            rate.setTextColor(colorDown)// 비율
                        } else if (it.prdy_vrss.toFloat() > 0f) {
                            price.setTextColor(colorUp) //현재가 색 설정

                            differ.setTextColor(colorUp) //차이

                            rate.setTextColor(colorUp) // 비율
                        }
                    }
                })
            }
        }
    }
    private fun makeDiffer(stockPredicPrice: Double, stockPredicRate: Double):Int{
        val rate = stockPredicRate/100
        if (stockPredicRate.toFloat() ==0f){
            return 0
        }else{
            return abs((stockPredicPrice/(1+rate) - stockPredicPrice).toInt())
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