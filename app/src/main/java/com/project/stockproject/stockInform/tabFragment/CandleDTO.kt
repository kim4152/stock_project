package com.project.stockproject.stockInform.tabFragment

data class CandleDTO(
    val date: String,
    val al: List<PriceDTO>
)
data class PriceDTO(
    val high: Float,
    val low: Float,
    val open: Float,
    val close: Float,
    val differ: Float,
    val volume: Float,
)
