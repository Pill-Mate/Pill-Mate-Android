package com.pill_mate.pill_mate_android.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PillIdntfcItem(
    val ITEM_SEQ: String,   // 식별 번호
    val ITEM_NAME: String,  // 약물 이름
    val ENTP_SEQ: String,
    val ENTP_NAME: String,
    val CHART: String,
    val ITEM_IMAGE: String?, // 약물 이미지 URL
    val PRINT_FRONT: String?,
    val PRINT_BACK: String?,
    val DRUG_SHAPE: String,
    val COLOR_CLASS1: String?,
    val COLOR_CLASS2: String?,
    val LINE_FRONT: String?,
    val LINE_BACK: String?,
    val LENG_LONG: String?,
    val LENG_SHORT: String?,
    val THICK: String?,
    val IMG_REGIST_TS: String?,
    val CLASS_NO: String?,
    val CLASS_NAME: String?, // 분류명
    val ETC_OTC_NAME: String?,
    val ITEM_PERMIT_DATE: String?,
    val FORM_CODE_NAME: String?,
    val MARK_CODE_FRONT_ANAL: String?,
    val MARK_CODE_BACK_ANAL: String?,
    val MARK_CODE_FRONT_IMG: String?,
    val MARK_CODE_BACK_IMG: String?,
    val ITEM_ENG_NAME: String?,
    val CHANGE_DATE: String?,
    val MARK_CODE_FRONT: String?,
    val MARK_CODE_BACK: String?,
    val EDI_CODE: String?,
    val BIZRNO: String?
) : Parcelable
