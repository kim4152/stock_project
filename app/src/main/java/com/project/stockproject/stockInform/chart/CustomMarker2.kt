package com.project.stockproject.stockInform.chart

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.Marker2ViewBinding
import java.text.DecimalFormat
import kotlin.math.round

class CustomMarker2(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val binding = Marker2ViewBinding.inflate(LayoutInflater.from(context), this, true)
    fun setData(current: Float, predic: Float) {
        binding.predic2.text = setPredic(current.toInt(), predic.toInt())
    }


    private fun setPredic(current: Int, predic: Int): String {
        val percentage = calculatePercentageDifference(predic, current)
        setColor(percentage)
        return "${df.format(predic)}(${percentage}%)"
    }

    private val df = DecimalFormat("#,###")

    //두 가격차이 백분율
    private fun calculatePercentageDifference(a: Int, b: Int): Double {
        if (a == 0 && b == 0) {
            return 0.0 // A와 B가 모두 0이면 차이가 없음
        }
        val difference = a - b
        val percentageDifference = ((difference.toDouble() / b) * 100)
        return round(percentageDifference * 100) / 100 // 소숫점 둘째 자리까지 반올림
    }

    //색 입히기
    private fun setColor(percentage: Double) {

        if (percentage > 0.0) {
            binding.predic2.setTextColor(
                ContextCompat.getColor
                    (MyApplication.getAppContext(), R.color.up)
            )
        } else if (percentage < 0.0) {
            binding.predic2.setTextColor(
                ContextCompat.getColor
                    (MyApplication.getAppContext(), R.color.down)
            )
        }
    }


    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        super.refreshContent(entry, highlight)
    }

    // You can override this method to customize the offset of the MarkerView
    override fun getOffset(): MPPointF {
        return super.getOffset()
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        if (posX > width) {
            // 왼쪽에 표시
            return MPPointF(-width.toFloat() - 20, -posY + 20)
        } else {
            // "오른쪽에표시"
            return MPPointF(20f, -posY + 20)
        }


    }
}