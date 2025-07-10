package com.pill_mate.pill_mate_android.medicine_conflict.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ConflictCheckResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ConflictCheckResult
)

data class ConflictCheckResult(
    val usjntTabooList: List<UsjntTabooResponse>,
    val efcyDplctList: List<EfcyDplctResponse>,
    val conflictWithUserMeds: ConflictWithUserMeds?
)

@Parcelize
data class UsjntTabooResponse(
    val mixtureItemSeq: String,
    val className: String,
    val mixItemName: String,
    val entpName: String,
    val prohbtContent: String,
    @SerializedName("image") val item_image: String?
) : Parcelable

@Parcelize
data class EfcyDplctResponse(
    val itemSeq: String,
    val className: String,
    val itemName: String,
    val entpName: String,
    val effectName: String,
    @SerializedName("image") val item_image: String?
) : Parcelable

@Parcelize
data class ConflictWithUserMeds(
    val item_name: String,
    val item_seq: String,
    val effect_name: String,
    val class_name: String,
    val entp_name: String,
    val item_image: String?
) : Parcelable