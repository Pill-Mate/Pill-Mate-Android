package com.example.pill_mate_android.pillSearch.model

import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement

@Xml(name = "response")
data class PharmacyResponse(
    @Element(name = "header") val header: Header?,
    @Element(name = "body") val body: Body?
)

@Xml(name = "header")
data class Header(
    @PropertyElement(name = "resultCode") val resultCode: String?,
    @PropertyElement(name = "resultMsg") val resultMsg: String?
)

@Xml(name = "body")
data class Body(
    @Element(name = "items") val items: Items?,
    @PropertyElement(name = "numOfRows") val numOfRows: Int?,
    @PropertyElement(name = "pageNo") val pageNo: Int?,
    @PropertyElement(name = "totalCount") val totalCount: Int?
)

@Xml(name = "items")
data class Items(
    @Element(name = "item") val itemList: List<Item>?
)

@Xml(name = "item")
data class Item(
    @PropertyElement(name = "dutyAddr") val dutyAddr: String?,
    @PropertyElement(name = "dutyName") val dutyName: String?,
    @PropertyElement(name = "dutyTel1") val dutyTel1: String?,
    @PropertyElement(name = "dutyTime1c") val dutyTime1c: String?,
    @PropertyElement(name = "dutyTime1s") val dutyTime1s: String?,
    @PropertyElement(name = "dutyTime2c") val dutyTime2c: String?,
    @PropertyElement(name = "dutyTime2s") val dutyTime2s: String?,
    @PropertyElement(name = "dutyTime3c") val dutyTime3c: String?,
    @PropertyElement(name = "dutyTime3s") val dutyTime3s: String?,
    @PropertyElement(name = "dutyTime4c") val dutyTime4c: String?,
    @PropertyElement(name = "dutyTime4s") val dutyTime4s: String?,
    @PropertyElement(name = "dutyTime5c") val dutyTime5c: String?,
    @PropertyElement(name = "dutyTime5s") val dutyTime5s: String?,
    @PropertyElement(name = "dutyTime6c") val dutyTime6c: String?,
    @PropertyElement(name = "dutyTime6s") val dutyTime6s: String?,
    @PropertyElement(name = "hpid") val hpid: String?,
    @PropertyElement(name = "postCdn1") val postCdn1: String?,
    @PropertyElement(name = "postCdn2") val postCdn2: String?,
    @PropertyElement(name = "rnum") val rnum: Int?,
    @PropertyElement(name = "wgs84Lat") val wgs84Lat: Double?,
    @PropertyElement(name = "wgs84Lon") val wgs84Lon: Double?
)