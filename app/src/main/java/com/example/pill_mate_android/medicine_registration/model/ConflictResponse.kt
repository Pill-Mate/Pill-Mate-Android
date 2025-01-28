package com.example.pill_mate_android.medicine_registration.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsjntTabooResponse(
    val CLASS_NAME: String,
    val MIXTURE_ITEM_NAME: String,
    val ENTP_NAME: String,
    val PROHBT_CONTENT: String
) : Parcelable

@Parcelize
data class EfcyDplctResponse(
    val CLASS_NAME: String,
    val ITEM_NAME: String,
    val ENTP_NAME: String,
    val EFFECT_NAME: String
) : Parcelable
