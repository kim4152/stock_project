package com.project.stockproject.stockInform.chart


data class Chart(
    val CurrentChart: CurrentChart,
    //val PredredictionChart: PredictionChart,
)
data class CurrentChart(
    val date: String,
    val priceDTO: PriceDTO,
    val additional: CurrentAdditional,
)
data class CurrentAdditional(
    val acmlVol: String,        //주식거러ㅐ량
    val prdyVrssSign: String,   //전일 대비 부호
    val prdyVrss: String,       //전일 대비
)

data class GetCurrentChart(
    val output1: Output1,
    val output2: List<Output2>,
    val rt_cd: String,
)

data class Output1(
    val prdy_vrss: String,      //전일대비
    val prdy_vrss_sign: String, //전일대비부호
    val prdy_ctrt: String,      //전일 대비율

    val stck_prdy_clpr: String, //주식 전일 종가
    val acml_vol: String,       //거래량


    val stck_prpr: String,      //주식현재가

    val prdy_vol: String,       //전일거래량

    val stck_oprc: String,      //시가
    val stck_hgpr: String,      //최고가
    val stck_lwpr: String,      //최저가
    val stck_prdy_oprc: String, //주식 전일 시가
    val stck_prdy_hgpr: String, //주식 전일 최고가
    val stck_prdy_lwpr: String, //주식 전일 최저가
    val prdy_vrss_vol: String,  //전일대비거래량
)

data class Output2(
    val stck_bsop_date: String,   //영업일
    val stck_clpr: String,       //주식종가
    val stck_oprc: String,       //주식시가
    val stck_hgpr: String,       //주식최고가
    val stck_lwpr: String,       //주식최저가
    val acml_vol: String,        //주식거러ㅐ량
    val prdy_vrss_sign: String,   //전일 대비 부호
    val prdy_vrss: String,       //전일 대비
)

data class GerPredictionChart(
    val test: String,
)

data class PriceDTO(
    val stckClpr: Float,       //주식종가
    val stckOprc: Float,       //주식시가
    val stckHgpr: Float,       //주식최고가
    val stckLwpr: Float,       //주식최저가
    val acmlVol: String,        //주식거래량
    val prdyVrssSign: String,   //전일 대비 부호
    val prdyVrss: String,       //전일 대비
)
