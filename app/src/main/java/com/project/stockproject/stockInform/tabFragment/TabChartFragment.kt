package com.project.stockproject.stockInform.tabFragment

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.CandleStickChartRenderer
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentTabsecondBinding
import com.project.stockproject.stockInform.StockInformFragment.Companion.STOCKOUTPUT
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TabChartFragment : Fragment() {

    private var totalList : MutableList<CurrentChart> = mutableListOf()
    private var candleEntryList: MutableList<CandleEntry> = mutableListOf()
    private var barEntryList: MutableList<BarEntry> = mutableListOf()
    private var dateList: MutableList<String> = mutableListOf()
    private var additionalList = mutableListOf<CurrentAdditional>()

    private lateinit var combinedChart: CombinedChart
    private lateinit var barChart: BarChart
    private lateinit var binding: FragmentTabsecondBinding
    private lateinit var viewModel: TabViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabsecondBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProviders.of(this)[TabViewModel::class.java]
        combinedChart = binding.combinedChart
        barChart = binding.barChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = STOCKOUTPUT?.stck_shrn_iscd
        if (item != null) { getCurrentChart(item,getCurrentDate()) }

    }

    //viewModel을 통해 현재 주가 가져오기
    private fun getCurrentChart(stockCode:String,endDate:String) {
        viewModel.getCurrentChart(stockCode = stockCode, startDate = getDateBeforeDays(endDate,50), endDate = endDate)
            .observe(this, Observer { list ->
                if (list.size != 0) {
                    chartListProcessing(list)
                }
            })
    }


    private fun chartListProcessing(list: MutableList<CurrentChart>) {
        list.reversed().forEachIndexed { index, it ->
            //종합
            totalList.add(it)
            //날짜리스트
            dateList.add(it.date)
            //캔들리스트
            it.priceDTO.apply {
                candleEntryList.add(
                    CandleEntry(
                        index.toFloat(),
                        this.stckHgpr,
                        this.stckLwpr,
                        this.stckOprc,
                        this.stckClpr
                    )
                )
            }
            it.additional.apply {
                barEntryList.add(
                    BarEntry(
                        index.toFloat(),
                        this.acmlVol.toFloat()
                    )
                )
            }

            //기타리스트(거래량)
            additionalList.add(it.additional)
        }
        setChart(candleEntryList)
    }


    private fun setChart(candleEntryList: MutableList<CandleEntry>) {
        //combinedChart.onChartGestureListener=MyOnChartGestureListener()//차트 제스처 이벤트

        // 이동평균선 데이터 생성
        val maEntries = calculateMovingAverage(candleEntryList, 5) // 예: 5일 이동평균선
        val maDataSet = LineDataSet(maEntries, "5").apply {
            color = Color.RED
            setDrawCircles(false)
            setDrawValues(false)
        }

        //캔들 모양
        val candleDataSet = CandleDataSet(candleEntryList, "Candle Data").apply {
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

        setBarchart()

        makeAxis() //축 설정
        setLegend(combinedChart.legend)// 범례 설정
        eventSetting()  //차트 이벤트 설정



        combinedChart.invalidate() // 차트 업데이트
    }
    //거래량 막대 그래프
    private fun setBarchart(){
        var previousYValue: Float = 0f
        val barDataSet = BarDataSet(barEntryList,"거래량").apply {
            setDrawValues(false)
            setDrawIcons(false)
            colors=barEntryList.map {entry->
                if (entry.y > previousYValue) {
                    Color.RED   // 현재 x축의 y값이 더 크면 빨강색
                } else {
                    Color.BLUE  // 현재 x축의 y값이 작거나 같으면 파랑색
                }.also {
                    previousYValue = entry.y  // 이전 값 업데이트
                }
            }
        }


        makeBarAxis(barChart)
        barEvent()

        //legend 안나오게 하기
        val i = barChart.legend
        i.isEnabled=false


        barChart.data =BarData(barDataSet)
        barChart.invalidate()
    }
    private fun barEvent(){
        barChart.setOnClickListener { barChart.highlightValue(null) }
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Log.d("asdfdf","selec")
            }

            override fun onNothingSelected() {
                barChart.highlightValue(null)
                Log.d("asdfdf","no")
            }

        })


        barChart.setVisibleXRangeMinimum(8f) //x축 확대 제한

        barChart.isScaleYEnabled=false
        barChart.isScaleXEnabled=false
        barChart.isDragEnabled=false

    }
    private fun makeBarAxis(barChart:BarChart){
        barChart.apply {
            isHighlightPerDragEnabled = true
            description.text = ""


            // X축 이동 및 확대/축소 활성화
            setPinchZoom(true)  //손가락으로 확대 축소 가능
            isDragYEnabled=false    //y축 드래그 X
            isScaleYEnabled=false   //y축 확대 축소x
            isAutoScaleMinMaxEnabled=true //y축 값 자동 조정

        }
        //왼쪽 축 설정
        barChart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        //오른 쪽 축
        barChart.axisRight.apply {
            setDrawGridLines(false)
            setDrawLabels(true)
            textColor=Color.TRANSPARENT
            setDrawAxisLine(false)
        }
        //부모뷰 가로채기x
        barChart.requestDisallowInterceptTouchEvent(true)
        //x축
        barChart.xAxis.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            labelCount = candleEntryList.size
            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            isGranularityEnabled = true
            setAvoidFirstLastClipping(true)
            //x축 최솟값,최댓값
            axisMinimum = -0.5f
            axisMaximum = (candleEntryList.size+1.5).toFloat()

            //x축을 날짜 형식으로
            valueFormatter = DateAxisValueFormatter()
        }
    }


    //축 설정
    private fun makeAxis() {
        //차트 초기 설정
        combinedChart.apply {
            isHighlightPerDragEnabled = true
            description.text = ""



            // X축 이동 및 확대/축소 활성화
            setPinchZoom(true)  //손가락으로 확대 축소 가능
            isDragYEnabled=false    //y축 드래그 X
            isScaleYEnabled=false   //y축 확대 축소x
            isAutoScaleMinMaxEnabled=true //y축 값 자동 조정

        }
        //왼쪽 축 설정
        combinedChart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        //오른 쪽 축
        combinedChart.axisRight.apply {
            labelCount = 6
            setDrawGridLines(false)
            textColor = Color.BLACK
        }
        //부모뷰 가로채기x
        combinedChart.requestDisallowInterceptTouchEvent(true)
        //x축
        combinedChart.xAxis.apply {
            setDrawGridLines(false)
            setDrawLabels(true)
            labelCount = candleEntryList.size
            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            isGranularityEnabled = true
            setAvoidFirstLastClipping(true)
            //x축 최솟값,최댓값
            axisMinimum = -0.5f
            axisMaximum = (candleEntryList.size+1.5).toFloat()

            //x축을 날짜 형식으로
            valueFormatter = DateAxisValueFormatter()


        }

    }

    //setLegend
    private fun setLegend(legend: Legend) {
        legend.form = Legend.LegendForm.LINE // 범례 모양 설정
        legend.textColor = Color.BLACK // 범례 텍스트 색상 설정

        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(true) // 차트 안에 범례를 그리지 않음
        legend.xOffset = 10f // 우측으로의 오프셋
        legend.yOffset = 10f // 상단으로의 오프셋

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

    private fun eventSetting(){
        combinedChart.apply {
            setVisibleXRangeMinimum(8f) //x축 확대 제한

            //클릭으로 highlight나오지 않게
            setOnClickListener { highlightValue(null) }


            setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val customMarker = CustomMarker(context, R.layout.marker_view,combinedChart)
                    val index = (e?.x ?: 0f).toInt()

                    customMarker.setData(totalList,index)

                    marker = customMarker
                }
                override fun onNothingSelected() {}
            })

            onChartGestureListener = object :OnChartGestureListener{
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                    combinedChart.highlightValues(null)
                }

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {

                }

                override fun onChartLongPressed(me: MotionEvent?) {
                    val highlight = combinedChart.getHighlightByTouchPoint(me?.x ?: 0f, me?.y ?: 0f)
                    combinedChart.highlightValue(highlight, true)
                    // 거래량차트도 highlight
                    barChart.highlightValues(arrayOf(highlight))
                    ////////////////////////////////////////////




                }

                override fun onChartDoubleTapped(me: MotionEvent?) {

                }

                override fun onChartSingleTapped(me: MotionEvent?) {

                }

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {

                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                    barChart.zoom(scaleX, 1f, 0f, 0f)
                    val sharedMatrix = Matrix()
                    // CombinedChart와 BarChart의 ViewPortHandler 비율 동기화
                    syncViewPortHandler(combinedChart, barChart, sharedMatrix)
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                    // 이동 적용할 Matrix 객체 생성
                    val matrix = Matrix()

                    // 이동 적용
                    matrix.postTranslate(dX, 0f)

                    // CombinedChart와 BarChart의 ViewPortHandler 비율 동기화
                    syncViewPortHandler(combinedChart, barChart, matrix)
/*
                    // Matrix를 CombinedChart에 적용
                    combinedChart.viewPortHandler.refresh(matrix, combinedChart, true)

                    // Matrix를 BarChart에 적용
                    barChart.viewPortHandler.refresh(matrix, barChart, true)

                    // CombinedChart와 BarChart 갱신
                    combinedChart.notifyDataSetChanged()
                    barChart.notifyDataSetChanged()

                    // 화면 갱신
                    combinedChart.invalidate()
                    barChart.invalidate()*/
                }

            }

        }
    }
    //주가차트&거래량차트 확대/축소/이동 동기화
    private fun syncViewPortHandler(sourceChart: BarLineChartBase<*>, targetChart: BarLineChartBase<*>, matrix: Matrix) {
        // sourceChart의 변환 정보를 가져와서 matrix에 설정
        matrix.set(sourceChart.viewPortHandler.matrixTouch)

        // targetChart에 matrix를 적용하여 ViewPortHandler를 설정
        targetChart.viewPortHandler.refresh(matrix, targetChart, true)
    }


    //x축 label 설정
    inner class DateAxisValueFormatter() : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in dateList.indices) {
                val interestingIndices = setOf(0, dateList.size / 2, dateList.size-1)
                if (index in interestingIndices) {
                    formatDateString(dateList[index])
                } else {
                    ""
                }
            } else {
                ""
            }
        }

        //날짜형식변환
        private fun formatDateString(originalDate: String): String {
            val originalFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

            try {
                val date = originalFormat.parse(originalDate)
                return targetFormat.format(date)
            } catch (e: Exception) {
                // 날짜 형식 변환 실패 시 예외 처리
                return ""
            }
        }
    }



    //현재 날짜 구하기
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
        return dateFormat.format(Date())
    }
    //input날짜에서 -50일
    private fun getDateBeforeDays(inputDate: String, days: Int): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
        val calendar = Calendar.getInstance()

        try {
            calendar.time = dateFormat.parse(inputDate)!!
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            return dateFormat.format(calendar.time)
        } catch (e: Exception) {
            // 날짜 형식 변환 실패 시 예외 처리
            return ""
        }
    }
}