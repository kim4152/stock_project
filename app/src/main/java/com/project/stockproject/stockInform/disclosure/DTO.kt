package com.project.stockproject.stockInform.disclosure

// corp code 받아올떄
data class CorpCode(
    val data: List<CorpCodeItem>
)
data class CorpCodeItem(
    val corp_code: String,
    val corp_name: String,
    val stock_code: String,
    val modify_date: String
)
/////////////////////////////////////

