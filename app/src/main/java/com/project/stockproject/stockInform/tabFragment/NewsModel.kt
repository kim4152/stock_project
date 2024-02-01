package com.project.stockproject.stockInform.tabFragment

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

data class NewsModel(
    val title:String,
    val link:String,
    var imageUrl:String?=null,
    val date:String,
)

@Xml(name = "rss")
data class NewsRSS(
    @Element(name = "channel")
    val channel: RssChannel
)

@Xml(name = "channel")
data class RssChannel(
    @PropertyElement(name = "title")
    val title: String,

    @Element(name = "item")
    val items: List<NewsItem>? = null
)

@Xml(name = "item")
data class NewsItem(
    @PropertyElement(name="title")
    val title:String ?=null,

    @PropertyElement(name = "link")
    val link :String?=null,

    @PropertyElement(name="pubDate")
    val date :String?=null,

    )

fun List<NewsItem>.transform() : List<NewsModel>{
    return this.map {
        NewsModel(
            title = it.title ?: "",
            link = it.link ?: "",
            imageUrl = null,
            date=it.date ?: "",
        )
    }
}