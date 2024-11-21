package com.example.pill_mate_android.pillSearch.model

import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement

@Xml(name = "response")
data class HospitalResponse(
    @Element(name = "header") val header: HospitalHeader?,
    @Element(name = "body") val body: HospitalBody?
)

@Xml(name = "header")
data class HospitalHeader(
    @PropertyElement(name = "resultCode") val resultCode: String?,
    @PropertyElement(name = "resultMsg") val resultMsg: String?
)

@Xml(name = "body")
data class HospitalBody(
    @Element(name = "items") val items: HospitalItems?,
    @PropertyElement(name = "numOfRows") val numOfRows: Int?,
    @PropertyElement(name = "pageNo") val pageNo: Int?,
    @PropertyElement(name = "totalCount") val totalCount: Int?
)

@Xml(name = "items")
data class HospitalItems(
    @Element(name = "item") val itemList: List<HospitalItem>?
)

@Xml(name = "item")
data class HospitalItem(
    @PropertyElement(name = "dutyAddr") val dutyAddr: String?,            // 병원 주소
    @PropertyElement(name = "dutyDiv") val dutyDiv: String?,              // 병원 구분 코드
    @PropertyElement(name = "dutyDivNam") val dutyDivNam: String?,        // 병원 구분 이름
    @PropertyElement(name = "dutyEmcls") val dutyEmcls: String?,          // 응급실 코드
    @PropertyElement(name = "dutyEmclsName") val dutyEmclsName: String?,  // 응급실 이름
    @PropertyElement(name = "dutyEryn") val dutyEryn: String?,            // 응급실 운영 여부 (1: 운영, 0: 미운영)
    @PropertyElement(name = "dutyMapimg") val dutyMapimg: String?,        // 지도로 설명된 위치
    @PropertyElement(name = "dutyName") val dutyName: String?,            // 병원 이름
    @PropertyElement(name = "dutyTel1") val dutyTel1: String?,            // 대표 전화번호
    @PropertyElement(name = "dutyTel3") val dutyTel3: String?,            // 응급실 전화번호
    @PropertyElement(name = "dutyTime1c") val dutyTime1c: String?,        // 월요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime1s") val dutyTime1s: String?,        // 월요일 시작 시간 (HHmm)
    @PropertyElement(name = "dutyTime2c") val dutyTime2c: String?,        // 화요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime2s") val dutyTime2s: String?,        // 화요일 시작 시간 (HHmm)
    @PropertyElement(name = "dutyTime3c") val dutyTime3c: String?,        // 수요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime3s") val dutyTime3s: String?,        // 수요일 시작 시간 (HHmm)
    @PropertyElement(name = "dutyTime4c") val dutyTime4c: String?,        // 목요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime4s") val dutyTime4s: String?,        // 목요일 시작 시간 (HHmm)
    @PropertyElement(name = "dutyTime5c") val dutyTime5c: String?,        // 금요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime5s") val dutyTime5s: String?,        // 금요일 시작 시간 (HHmm)
    @PropertyElement(name = "dutyTime6c") val dutyTime6c: String?,        // 토요일 종료 시간 (HHmm)
    @PropertyElement(name = "dutyTime6s") val dutyTime6s: String?,        // 토요일 시작 시간 (HHmm)
    @PropertyElement(name = "hpid") val hpid: String?,                    // 병원 ID
    @PropertyElement(name = "postCdn1") val postCdn1: String?,            // 우편번호 앞자리
    @PropertyElement(name = "postCdn2") val postCdn2: String?,            // 우편번호 뒷자리
    @PropertyElement(name = "rnum") val rnum: Int?,                       // 순번
    @PropertyElement(name = "wgs84Lat") val wgs84Lat: Double?,            // 위도
    @PropertyElement(name = "wgs84Lon") val wgs84Lon: Double?             // 경도
)