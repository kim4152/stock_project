package com.project.stockproject.stockInform.chart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.MarkerViewBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.round

class CustomMarker(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val binding: MarkerViewBinding =
        MarkerViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var totalList: CurrentChart
    private lateinit var beforeList: CurrentChart

    fun setData(list: List<CurrentChart>, markerList:List<Float> ,pos:Int) {
        try{
            totalList = list[pos]
            if (pos>1){beforeList=list[pos-1]}else{beforeList=list[0]}
            binding.date.text=setDate()
            binding.endPrice2.text=setEndPrice()
            binding.openPrice2.text=setOpenPrice()
            binding.highPrice2.text=setHighPrice()
            binding.lowPrice2.text=setLowPrice()
            binding.vol2.text=setVol()
            binding.prediction2.text=setPredic(markerList,pos)
        }catch (e:Exception){
        }

    }



    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        super.refreshContent(entry, highlight)
    }

    // You can override this method to customize the offset of the MarkerView
    override fun getOffset(): MPPointF {
        // Adjust the offset if needed
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        //val xOffset = if (posX < width) -(getWidth() / 2f) else getWidth() / 2f
        if (posX>width){
            // 왼쪽에 표시
            return MPPointF(-width.toFloat()-20, -posY+20)
        } else{
           // "오른쪽에표시"
            return MPPointF(20f, -posY+20)
        }


    }
    private fun setDate():String{
        return formatDateString(totalList.date)
    }
    private fun setEndPrice():String{
        val a =totalList.priceDTO.stckClpr.toInt()
        val b =beforeList.priceDTO.stckClpr.toInt()
        val percentage=calculatePercentageDifference(a,b)
        setColor(percentage,"endPrice")
       return "${df.format(a)}(${percentage}%)"
    }
    private fun setOpenPrice():String{
        val a =totalList.priceDTO.stckOprc.toInt()
        val b =beforeList.priceDTO.stckClpr.toInt()
        val percentage=calculatePercentageDifference(a,b)
        setColor(percentage,"openPrice")
        return "${df.format(a)}(${percentage}%)"
    }
    private fun setHighPrice():String{
        val a =totalList.priceDTO.stckHgpr.toInt()
        val b =totalList.priceDTO.stckOprc.toInt()
        val percentage=calculatePercentageDifference(a,b)
        setColor(percentage,"highPrice")
        return "${df.format(a)}(${percentage}%)"
    }
    private fun setLowPrice():String{
        val a =totalList.priceDTO.stckLwpr.toInt()
        val b =totalList.priceDTO.stckOprc.toInt()
        val percentage=calculatePercentageDifference(a,b)
        setColor(percentage,"lowPrice")
        return "${df.format(a)}(${percentage}%)"
    }
    private fun setVol():String{
        val a =totalList.priceDTO.acmlVol.toInt()
        val b =beforeList.priceDTO.acmlVol.toInt()
        val percentage=calculatePercentageDifference(a,b)
        setColor(percentage,"vol")
        return "${df.format(a)}(${percentage}%)"
    }
    //천자리 , 찍기
    private val df = DecimalFormat("#,###")
    //날짜 형변환
    private fun formatDateString(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd")
        val outputFormat = SimpleDateFormat("yyyy/MM/dd")
        val date: Date = inputFormat.parse(inputDate) ?: Date()
        return outputFormat.format(date)
    }

    private fun setPredic(list:List<Float>,pos:Int): String{
        val predicCurrent= list[pos].toInt()
        val b =totalList.priceDTO.stckClpr.toInt()
        if (list.isNullOrEmpty()){
            return ""
        }else{
            val percentage=calculatePercentageDifference(predicCurrent,b)
            setColor(percentage,"predic")
            return "${df.format(predicCurrent)}(${percentage}%)"
        }
    }

    //두 가격차이 백분율
    private fun calculatePercentageDifference(a: Int, b: Int): String {
        if (a == 0 && b == 0) {
            return "0.0" // A와 B가 모두 0이면 차이가 없음
        }
        val difference = a-b
        val percentageDifference = ((difference.toDouble() / b) * 100)
        val re = round(percentageDifference * 100) / 100 // 소숫점 둘째 자리까지 반올림
        return if (re>0) "+${re}" else "$re"
    }
    //색 입히기
    private fun setColor(percen:String,string:String){
        val percentage = percen.toDouble()
        when(string){
            "endPrice"->{
                if (percentage>0.0){
                    binding.endPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.endPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            "openPrice"->{
                if (percentage>0.0){
                    binding.openPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.openPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            "highPrice"->{
                if (percentage>0.0){
                    binding.highPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.highPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            "lowPrice"->{
                if (percentage>0.0){
                    binding.lowPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.lowPrice2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            "vol"->{
                if (percentage>0.0){
                    binding.vol2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.vol2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            "predic"->{
                if (percentage>0.0){
                    binding.prediction2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.up)
                    )
                }else if(percentage<0.0){
                    binding.prediction2.setTextColor(
                        ContextCompat.getColor
                            (MyApplication.getAppContext(), R.color.down)
                    )
                }
            }
            else->{}
        }
    }
}