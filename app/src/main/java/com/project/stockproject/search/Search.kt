package com.project.stockproject.search

import android.widget.ImageView
import com.google.gson.annotations.SerializedName

open class Search(
    @Transient open val image: ImageView?,
    open val stockName: String,
    @SerializedName("stockCode") // 명시적으로 선택
    open val stockCode: String,
    @Transient open val star: ImageView?
) {
    companion object {
        val TYPE_RESULT = 0
        val TYPE_HISTORY = 1
    }

    open fun getType() :Int {return -1}
}

// 자식 클래스: SearchResult
data class SearchResult(
    override val image: ImageView?,
    override val stockName: String,
    @SerializedName("stockCode") // 명시적으로 선택
    override val stockCode: String,
    override val star: ImageView?
) : Search(image, stockName, stockCode, star) {
    override fun getType(): Int {
        return TYPE_RESULT
    }
}

// 자식 클래스: SearchHistory
data class SearchHistory(
    override val image: ImageView?,
    override val stockName: String,
    override val stockCode: String,
    override val star: ImageView?
) : Search(image, stockName, stockCode, star) {
    override fun getType(): Int {
        return TYPE_HISTORY
    }
}
data class HistoryManager(
    val stockName:String,
    val stockCode:String,
)



data class AwsAPIStockInfo(
    val stock_code: String,
    val stock_name: String,
    val stock_market: String,
    val stock_industry: String,
    val stock_predic_price: String
)

