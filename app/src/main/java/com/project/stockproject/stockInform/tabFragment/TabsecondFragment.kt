package com.project.stockproject.stockInform.tabFragment

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentTabsecondBinding
import com.project.stockproject.stockInform.StockInformFragment
import com.project.stockproject.stockInform.StockInformFragment.Companion.STOCKOUTPUT
import com.project.stockproject.stockInform.StockOutput
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TabsecondFragment : Fragment() {


    private var totalList: MutableList<CandleEntry> = mutableListOf()
    private var dateList: MutableList<String> = mutableListOf()


    private lateinit var binding: FragmentTabsecondBinding
    private lateinit var viewModel: TabViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("adfdsf","2:onCreate")
        binding = FragmentTabsecondBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProviders.of(this)[TabViewModel::class.java]
        return binding.root
    }

    override fun onStart() {
        Log.d("adfdsf","2:onStart")
        val item = STOCKOUTPUT
        super.onStart()
        item?.stck_shrn_iscd?.let {//item null 체크
            viewModel.getChart(it).observe(this, Observer { list ->
                div(list)
            })
        }
    }
    /////////////////////////////////////////////////////////////////////

    private fun div(list: MutableList<CandleDTO>) {
        list.reversed().forEachIndexed { id, aa ->
            val date = aa.date
            dateList.add(date)
            aa.al.forEach { bb ->
                totalList.add(CandleEntry(id.toFloat(), bb.high, bb.low, bb.open, bb.close))
            }
        }

        AA(binding.combinedChart,totalList,dateList).doit(totalList)
    }

    class AA(private val combinedChart: CombinedChart,
             private val totalList: MutableList<CandleEntry>,
             private val dateList: MutableList<String>
    ) {
        fun doit(candleEntryList: MutableList<CandleEntry>) {
//        val combinedChart = binding.combinedChart
            //combinedChart.onChartGestureListener=MyOnChartGestureListener()//차트 제스처 이벤트
            makeAxis(combinedChart) //축 설정
            setLegend(combinedChart.legend)// 범례 설정


            // 이동평균선 데이터 생성
            val maEntries = calculateMovingAverage(candleEntryList, 5) // 예: 5일 이동평균선
            val maDataSet = LineDataSet(maEntries, "5").apply {
                color = Color.RED
                setDrawCircles(false)
            }
            //캔들 모양
            val candleDataSet = CandleDataSet(candleEntryList, "Candle Data").apply {
                //color = Color.BLACK
                decreasingColor = Color.BLUE

                increasingColor = Color.RED
                shadowColor = Color.GRAY
                shadowWidth = 1.0f
                decreasingPaintStyle = android.graphics.Paint.Style.FILL
                increasingPaintStyle = android.graphics.Paint.Style.FILL
                neutralColor = Color.BLACK
                setDrawValues(false)

            }
            // CombinedData에 데이터셋 추가
            val combinedData = CombinedData().apply {
                setData(LineData(maDataSet))        //이동평균선
                setData(CandleData(candleDataSet))  //캔들
            }

            // CombinedChart에 데이터 설정
            combinedChart.data = combinedData
            combinedChart.invalidate() // 차트 업데이트


        }

        //축 설정
        private fun makeAxis(combinedChart: CombinedChart) {
            //차트 초기 설정
            combinedChart.apply {
                isHighlightPerDragEnabled = true
                //setBorderColor()
                description.text = ""
//            setVisibleXRange(1f,2f)
                // 확대/축소 동시에
                setPinchZoom(true)
                setScaleEnabled(true)
            }
            //왼쪽 축 설정
            combinedChart.axisLeft.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
            }
            //오른 쪽 축
            combinedChart.axisRight.apply {
                labelCount = 6
                setDrawGridLines(false)
                textColor = Color.BLACK
            }
            combinedChart.requestDisallowInterceptTouchEvent(true)
            //x축
            combinedChart.xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(true)
                labelCount = totalList.size
                textColor = Color.BLACK
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                setAvoidFirstLastClipping(true)
                //x축 최솟값,최댓값
                axisMinimum = -0.5f
                axisMaximum = (totalList.size + 1).toFloat()

                //x축을 날짜 형식으로
                valueFormatter = DateAxisValueFormatter()
            }

        }

        //setLegend
        private fun setLegend(legend: Legend) {
            legend.form = Legend.LegendForm.LINE // 범례 모양 설정
            legend.textColor = Color.BLACK // 범례 텍스트 색상 설정

            // 범례 항목 추가
            val legendEntries = arrayOf(
                LegendEntry("5", Legend.LegendForm.LINE, 10f, 2f, null, Color.RED),
            )
            legend.setCustom(legendEntries)
        }

        // 이동평균 계산 함수
        private fun calculateMovingAverage(data: List<CandleEntry>, period: Int): List<Entry> {
            val maEntries = mutableListOf<Entry>()

            for (i in data.indices) {
                if (i >= period - 1) {
                    var sum = 0f
                    for (j in 0 until period) {
                        sum += data[i - j].close
                    }
                    val average = sum / period
                    maEntries.add(Entry(data[i].x, average))
                } else {
                    //maEntries.add(Entry(data[i].x, data[i].y))
                }
            }
            return maEntries
        }


        //차트 확대/축소 이벤트
        /*inner class MyOnChartGestureListener : OnChartGestureListener {
            override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                // 확대/축소 제스처 시작 시 실행되는 코드

            }

            override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                // 확대/축소 제스처 종료 시 실행되는 코드
            }

            override fun onChartLongPressed(me: MotionEvent?) {

            }

            override fun onChartDoubleTapped(me: MotionEvent?) {

            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                //최대로 확대하면 차트 더 보여주기
                if (scaleY<=1.0f && scaleX<=1.0f) {
                    Thread.sleep(500)
                    getMore()
                }
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {

            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                // 차트를 단일 터치했을 때 실행되는 코드
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {

            }

            // 나머지 메서드들은 필요에 따라 구현할 수 있습니다.
            // onChartLongPressed, onChartDoubleTapped, onChartFling 등이 있습니다.
        }*/

        //x축 label 설정
        inner class DateAxisValueFormatter() : IndexAxisValueFormatter() {
            var tmp2: String = ""
            override fun getFormattedValue(value: Float): String {
                // 여기서 전달받는 value는 x축의 label을 나타내는 값

                val index = value.toInt()

                if (index >= dateList.size) { //index가 리스트 범위를 넘어가면 리턴
                    return ""
                } else {
                    //string타입의 날짜를 date타입으로 변경
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date1: Date = dateFormat.parse(dateList[index])!!
                    val calendar1 = Calendar.getInstance().apply { time = date1 }
                    var tmp = (calendar1.get(Calendar.MONTH) + 1).toString()
                    //달을 비교해서 같으면 "" 다르면 해당 달 return
                    return if (tmp == tmp2) {
                        ""
                    } else {
                        tmp2 = tmp
                        tmp
                    }
                }

            }
        }
    }

}