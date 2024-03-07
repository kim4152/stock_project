package com.project.stockproject.stockInform.chart

import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.stockInform.StockInformFragment
import com.project.stockproject.stockInform.TabViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MakeChart(val stockCode:String, val combinedChart: CombinedChart,
                val viewModel: TabViewModel, val context: StockInformFragment,val marketName:String) {

    private var totalList: MutableList<CurrentChart> = mutableListOf()
    private var candleEntryList: MutableList<CandleEntry> = mutableListOf()
    private var dateList: MutableList<String> = mutableListOf()

    private var markerLineList: MutableList<Float> = mutableListOf()

    fun init(){
        getCurrentChart()
    }

    private var startDate="0"
    private var endDate="0"

    //viewModel을 통해 현재 주가 가져오기
    private fun getCurrentChart() {
        if (startDate=="0"&&endDate=="0") {
            //초기 차트 불러오기
            endDate=getCurrentDate()
            startDate=getDateBeforeDays(endDate, 60)

            viewModel.getCurrentChart(stockCode,startDate , endDate)
                .observe(context, Observer { list ->
                    if (list.size != 0) {
                        chartListProcessing(list)
                    }
                })
        }else{
           //차트 더 불러오기
            endDate=getDateBeforeDays(endDate,1) //기존 start날짜에서 하루빼기
            startDate = getDateBeforeDays(endDate,60)

            viewModel.getCurrentChart(stockCode,startDate , endDate)
                .observe(context, Observer { list ->
                    if (list.size != 0) {
                        chartListProcessing(list)
                    }
                })
        }
    }

    private fun chartListProcessing(list: MutableList<CurrentChart>) {
        list.reversed().forEachIndexed { index, it ->
            //종합
            totalList.add(index, it)
            //날짜리스트
            dateList.add(index, it.date)
            //캔들리스트
            it.priceDTO.apply {
                candleEntryList.add(
                    index,
                    CandleEntry(
                        index.toFloat(),
                        this.stckHgpr,
                        this.stckLwpr,
                        this.stckOprc,
                        this.stckClpr
                    )
                )
            }
        }
        setChart()
        makePredicList()
    }

    //예상 가격 만들기
    private fun makePredicList(){
        val totalSize =totalList.size
        var code = stockCode
        val stockMarket= when(marketName.replace(Regex("\\d+"), "")){
                //정규표현식으로 숫자 제거
                "KOSPI"-> "2"
                "KOSDAQ"->"3"
                "KSQ" ->"3"
                else->{
                    code=""
                    "2"
                }
            }
        try {
            viewModel.getPredicChart(StockInfoRequest(code,stockMarket)).observe(context, Observer {
                if (it==null){
                    MyApplication.makeToast("예측하기 힘든 종목입니다")
                }else if(it.pred_30_day.size-totalSize<0){
                    MyApplication.makeToast("아직 공부중입니다")
                }else{
                    makePredicChart(it)
                }
            })
        }catch (e:Exception){ }
    }

    //예상가격차트만들기
    private fun makePredicChart(it:PredictionData){
        val lineList = mutableListOf<Entry>()
        var realIndex =0f

        it.pred_30_day.forEachIndexed { index, d ->
            val cutLine=it.pred_30_day.size-totalList.size
            if (index>=cutLine){
                markerLineList.add(d.toFloat()) //customMarker로 보낼 데이터 생성
                lineList.add(Entry(realIndex,d.toFloat()))
                realIndex++
            }
        }
        markerLineList.add(it.next_day_pred[0].toFloat()) //customMarker로 보낼 데이터 생성
        lineList.add(Entry(realIndex,it.next_day_pred[0].toFloat())) //내일 가격

        eventSetting()
        candleDataSet.isHighlightEnabled=false //캔들 하이라이트 X

        val predicDataset = LineDataSet(lineList, "예측선").apply {
            color = ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)
            setDrawCircles(true)
            setCircleColor(ContextCompat.getColor(MyApplication.getAppContext(),R.color.basic))
            circleHoleColor=ContextCompat.getColor(MyApplication.getAppContext(), R.color.surface)
            setDrawValues(false)
        }
        val combinedData: CombinedData = CombinedData().apply {
            setData(LineData(predicDataset))
            setData(CandleData(candleDataSet))
        }
        combinedChart.data = combinedData
        combinedChart.invalidate()
    }


    private lateinit var candleDataSet: CandleDataSet

    private fun setChart() {
        // 이동평균선 데이터 생성
        /*val maEntries = calculateMovingAverage(candleEntryList, 5)
        val maDataSet = LineDataSet(maEntries, "5일 이동평균").apply {
            color = Color.RED
            setDrawCircles(false)
            setDrawValues(false)
        }*/
        //캔들 모양
        candleDataSet = CandleDataSet(candleEntryList, "Candle Data").apply {
            decreasingColor = Color.BLUE
            increasingColor = Color.RED
            shadowColor = Color.GRAY
            shadowWidth = 1.0f
            decreasingPaintStyle = android.graphics.Paint.Style.FILL
            increasingPaintStyle = android.graphics.Paint.Style.FILL
            neutralColor = Color.BLACK
            setDrawValues(false)
            isHighlightEnabled=true
        }


        // CombinedData에 데이터셋 추가
        val combinedData = CombinedData().apply {
            setData(CandleData(candleDataSet))
        }
        // CombinedChart에 데이터 설정
        combinedChart.data = combinedData


        makeAxis() //축 설정
        setLegend(combinedChart.legend)// 범례 설정

        eventSetting()  //차트 이벤트 설정


        combinedChart.invalidate() // 차트 업데이트
    }

    //축 설정
    private fun makeAxis() {
        //차트 초기 설정
        combinedChart.apply {
            isHighlightPerDragEnabled = true
            description.text = ""


            // X축 이동 및 확대/축소 활성화
            setPinchZoom(true)  //손가락으로 확대 축소 가능
            isDragYEnabled = false    //y축 드래그 X
            isScaleYEnabled = false   //y축 확대 축소x
            isScaleXEnabled = false   //X축 확대 축소x
            isAutoScaleMinMaxEnabled = true //y축 값 자동 조정

        }
        //왼쪽 축 설정
        combinedChart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        //오른 쪽 축
        combinedChart.axisRight.apply {
            labelCount = 5
            setDrawGridLines(false)
            textColor = Color.BLACK
            setDrawAxisLine(false)
            textColor=ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)
        }

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

            textColor=ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)
        }

    }

    //setLegend
    private fun setLegend(legend: Legend) {
        legend.form = Legend.LegendForm.LINE // 범례 모양 설정
        legend.textColor = Color.BLACK // 범례 텍스트 색상 설정

        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(true) // 차트 안에 범례 그리기


        // 범례 항목 추가
        val legendEntries = arrayOf(
            LegendEntry("AI예측", Legend.LegendForm.LINE, 10f, 2f, null,
                ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)),
        )
        legend.setCustom(legendEntries)
        legend.textColor=ContextCompat.getColor(MyApplication.getAppContext(), R.color.basic)
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

    private fun eventSetting() {
        combinedChart.apply {
            setVisibleXRangeMinimum(8f) //x축 확대 제한

            //클릭으로 highlight나오지 않게
            setOnClickListener {
                highlightValue(null)
                false
            }



            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val index = (e?.x ?: 0f).toInt()

                    if (index!=totalList.size){
                        val customMarker = CustomMarker(context, R.layout.marker_view)
                        customMarker.setData(totalList,markerLineList ,index)
                        marker = customMarker
                    }else{
                        val customMarker = CustomMarker2(context,R.layout.marker2_view)
                        customMarker.setData(totalList[totalList.size-1].priceDTO.stckClpr
                            ,markerLineList[markerLineList.size-1])
                        marker=customMarker
                    }

                }

                override fun onNothingSelected() {}
            })

            onChartGestureListener = object : OnChartGestureListener {
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

                    combinedChart.requestDisallowInterceptTouchEvent(true)
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
                    if (scaleX<=1.0f) {
                        //getCurrentChart()
                    }
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                    // 이동 적용할 Matrix 객체 생성

                }

            }

        }
    }


    //x축 label 설정
    inner class DateAxisValueFormatter() : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in dateList.indices) {
                if (index%10==0 || index == dateList.size-2) {
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

    //input날짜에서 -x일
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