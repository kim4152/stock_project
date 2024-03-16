package com.project.stockproject.common

data class GetToken(
    val statusCode: Int,
    val headers: Headers,
    val body: ResponseBody
)

data class Headers(
    val contentType: String
)

data class ResponseBody(
    val accessToken: String
)

data class GetIPv4(
    val statusCode: Int,
    val body:String,
)
data class InfoCheck(
    val statusCode: Int,
    val body:String,
)
