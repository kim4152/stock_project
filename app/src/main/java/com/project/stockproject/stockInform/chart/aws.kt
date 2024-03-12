package com.project.stockproject.stockInform.chart


data class PredictionData(
    val next_day_pred: List<Double>,
    val pred_30_day: List<Double>
)