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
