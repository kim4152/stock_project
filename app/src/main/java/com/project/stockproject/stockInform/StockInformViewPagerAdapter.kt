package com.project.stockproject.stockInform

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.MajorIndexItemBinding
import com.project.stockproject.databinding.StockInformItemBinding
import com.project.stockproject.home.MajorIndexViewPagerAdapter
import java.text.DecimalFormat

class StockInformViewPagerAdapter(private var adapterList: MutableList<StockOutput>,
                                  private val favoriteClick:(stockName:String)->Unit
) :
    RecyclerView.Adapter<StockInformViewPagerAdapter.ViewHolder>() {

    fun addItem(item: List<StockOutput>) {
        item.forEach {
            adapterList.add(it)
        }
        notifyDataSetChanged()
    }


    fun getList():List<StockOutput>{
       return adapterList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StockInformItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {

        return adapterList.size
    }

    

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(adapterList[position])
    }

    inner class ViewHolder(private val viewBinding: StockInformItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        private val df = DecimalFormat("#,###") //세자리마다 콤마 찍기

        fun bind(item: StockOutput) {
            viewBinding.apply {
                stockCode.text=item.stck_shrn_iscd //종목코드
                marketName.text=item.rprs_mrkt_kor_name//마켓이름

                when(item.iscd_stat_cls_code){
                    "00"->{stockState.text=""}
                    "55"->{stockState.text="신용가능"}
                    "51"->{stockState.text="관리종목"}
                    "52"->{stockState.text="투자위험"}
                    "57"->{stockState.text="증거금100%"}
                    "53"->{stockState.text="투자경고"}
                    "58"->{stockState.text="거래정지"}
                    "54"->{stockState.text="투자주의"}
                    "59"->{stockState.text="단기과열"}
                    else->{stockState.text=""}
                }//종목상태구분

                if(item.bstp_kor_isnm!=null)bstpIsnm.text=item.bstp_kor_isnm//업종
                favorite.setColorFilter(ContextCompat.getColor
                    (MyApplication.getAppContext(), R.color.white))
                /////////////////////////////////////////////////////////////////

                stckPrpr.text=df.format(item.stck_prpr.toInt())//현재가
               if (item.prdy_vrss.substring(0,1)=="-"){ //음봉
                   arrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)//이미지 설정
                   arrow.setColorFilter(ContextCompat.getColor
                       (MyApplication.getAppContext(), R.color.down))//이미지 색
                   prdyVrss.text=item.prdy_vrss.substring(1)//가격 차이
                   prdyCtrt.text=" (${item.prdy_ctrt.substring(1)}%)" //비율
               }else if (item.prdy_vrss.toFloat()>0f){ //양봉
                   arrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)//이미지 설정
                   arrow.setColorFilter(ContextCompat.getColor
                       (MyApplication.getAppContext(), R.color.up))//이미지 색
                   prdyVrss.text=df.format(item.prdy_vrss.toInt())//가격 차이
                   prdyCtrt.text=" (${item.prdy_ctrt}%)" //비율
               }else{
                   prdyVrss.text=df.format(item.prdy_vrss.toInt())//가격 차이
                   prdyCtrt.text=" (${item.prdy_ctrt}%)" //비율
               }


                acmlVol.text=df.format(item.acml_vol.toInt())//거래량

                //지수 : 양봉/음봉 text색 설정
                if (item.prdy_vrss.substring(0, 1) == "-") {
                    consLayout.setBackgroundColor(ContextCompat.getColor(
                        MyApplication.getAppContext(),
                        R.color.down))//배경 색 설정

                    stckPrpr.setTextColor(
                        ContextCompat.getColor(
                            MyApplication.getAppContext(),
                            R.color.down))//현재가 색 설정

                    prdyVrss.setTextColor(ContextCompat.getColor
                        (MyApplication.getAppContext(), R.color.down)) //차이

                    prdyCtrt.setTextColor(
                        ContextCompat.getColor(
                            MyApplication.getAppContext(),
                            R.color.down))// 비율
                } else if (item.prdy_vrss.toFloat()>0f) {
                    consLayout.setBackgroundColor(ContextCompat.getColor(
                        MyApplication.getAppContext(),
                        R.color.up))//배경 색 설정

                    stckPrpr.setTextColor(
                        ContextCompat.getColor(
                            MyApplication.getAppContext(),
                            R.color.up))//현재가 색 설정

                    prdyVrss.setTextColor(ContextCompat.getColor
                        (MyApplication.getAppContext(), R.color.up)) //차이

                    prdyCtrt.setTextColor(
                        ContextCompat.getColor(
                            MyApplication.getAppContext(),
                            R.color.up))// 비율
                }else{
                    consLayout.setBackgroundColor(ContextCompat.getColor(
                        MyApplication.getAppContext(),
                        R.color.gray))//배경 색 설정
                }
            }
            viewBinding.favorite.setOnClickListener {favoriteClick(item.stck_shrn_iscd)  }

        }
    }


}



