package com.project.stockproject.home

data class MFItem(
    val stock_code:String,
    val stock_name:String
)
data class Predic(
    val stock_code: String,
    val stock_name: String,
    val stock_predic_price: Double,
    val stock_predic_rate:Double,
)