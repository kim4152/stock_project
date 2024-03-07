package com.project.stockproject.stockInform.chart

data class StockInfoRequest(
    val stock_code: String,
    val kospi_kosdaq: String
)
data class PredictionData(
    val next_day_pred: List<Double>,
    val pred_30_day: List<Double>
)