package com.pill_mate.pill_mate_android.medicine_conflict.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsjntTabooResponse0( //병용금기
    val ITEM_SEQ: String,
    val CLASS_NAME: String,
    val MIXTURE_ITEM_NAME: String,
    val ENTP_NAME: String,
    val PROHBT_CONTENT: String,
    val ITEM_IMAGE: String? = null
) : Parcelable

@Parcelize
data class EfcyDplctResponse0( //효능군중복
    val ITEM_SEQ: String,
    val CLASS_NAME: String,
    val ITEM_NAME: String,
    val ENTP_NAME: String,
    val EFFECT_NAME: String,
    val ITEM_IMAGE: String? = null
) : Parcelable
