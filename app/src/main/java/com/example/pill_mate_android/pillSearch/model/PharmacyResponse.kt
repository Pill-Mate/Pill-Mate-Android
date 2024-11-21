package com.example.pill_mate_android.pillSearch.model

import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement

@Xml(name = "response")
data class PharmacyResponse(
    @Element(name = "header") val header: PharmacyHeader?,
    @Element(name = "body") val body: PharmacyBody?
)

@Xml(name = "header")
data class PharmacyHeader(
    @PropertyElement(name = "resultCode") val resultCode: String?,
    @PropertyElement(name = "resultMsg") val resultMsg: String?
)

@Xml(name = "body")
data class PharmacyBody(
    @Element(name = "items") val items: PharmacyItems?,
    @PropertyElement(name = "numOfRows") val numOfRows: Int?,
    @PropertyElement(name = "pageNo") val pageNo: Int?,
    @PropertyElement(name = "totalCount") val totalCount: Int?
)

@Xml(name = "items")
data class PharmacyItems(
    @Element(name = "item") val itemList: List<PharmacyItem>?
)

@Xml(name = "item")
data class PharmacyItem(
    @PropertyElement(name = "dutyAddr") val dutyAddr: String?,      // 약국 주소
    @PropertyElement(name = "dutyName") val dutyName: String,      // 약국 이름
    @PropertyElement(name = "dutyTel1") val dutyTel1: String?,      // 약국 전화번호
    @PropertyElement(name = "dutyTime1c") val dutyTime1c: String?,  // 월요일 종료 시간
    @PropertyElement(name = "dutyTime1s") val dutyTime1s: String?,  // 월요일 시작 시간
    @PropertyElement(name = "dutyTime2c") val dutyTime2c: String?,  // 화요일 종료 시간
    @PropertyElement(name = "dutyTime2s") val dutyTime2s: String?,  // 화요일 시작 시간
    @PropertyElement(name = "dutyTime3c") val dutyTime3c: String?,  // 수요일 종료 시간
    @PropertyElement(name = "dutyTime3s") val dutyTime3s: String?,  // 수요일 시작 시간
    @PropertyElement(name = "dutyTime4c") val dutyTime4c: String?,  // 목요일 종료 시간
    @PropertyElement(name = "dutyTime4s") val dutyTime4s: String?,  // 목요일 시작 시간
    @PropertyElement(name = "dutyTime5c") val dutyTime5c: String?,  // 금요일 종료 시간
    @PropertyElement(name = "dutyTime5s") val dutyTime5s: String?,  // 금요일 시작 시간
    @PropertyElement(name = "dutyTime6c") val dutyTime6c: String?,  // 토요일 종료 시간
    @PropertyElement(name = "dutyTime6s") val dutyTime6s: String?,  // 토요일 시작 시간
    @PropertyElement(name = "hpid") val hpid: String?,              // 약국 ID
    @PropertyElement(name = "postCdn1") val postCdn1: String?,      // 우편번호 앞자리
    @PropertyElement(name = "postCdn2") val postCdn2: String?,      // 우편번호 뒷자리
    @PropertyElement(name = "rnum") val rnum: Int?,                 // 리스트 순번
    @PropertyElement(name = "wgs84Lat") val wgs84Lat: Double?,      // 위도
    @PropertyElement(name = "wgs84Lon") val wgs84Lon: Double?       // 경도
)