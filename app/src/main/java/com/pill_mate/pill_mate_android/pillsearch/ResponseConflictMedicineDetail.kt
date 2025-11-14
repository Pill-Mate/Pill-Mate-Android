package com.pill_mate.pill_mate_android.pillsearch

data class ResponseConflictMedicineDetail(
    val itemImage: String,
    val className: String,
    val itemName: String,
    val entpName: String,
    val allConflictResponse: ConflictMedicineAllResponse,
    val typeName: String,
    val useMethodQesitm: String,
    val efcyQesitm: String,
    val atpnQesitm: String,
    val depositMethod: String
)

data class ConflictMedicineAllResponse(
    val usjntTabooList: List<ConflictMedicineUsjntItem>,
    val efcyDplctList: List<ConflictMedicineEfcyItem>,
    val conflictWithUserMeds: ConflictMedicineUserConflict
)

data class ConflictMedicineUsjntItem(
    val mixItemName: String,
    val mixtureItemSeq: String,
    val prohbtContent: String,
    val className: String,
    val entpName: String,
    val image: String
)

data class ConflictMedicineEfcyItem(
    val itemName: String,
    val itemSeq: String,
    val className: String,
    val effectName: String,
    val entpName: String,
    val image: String
)

data class ConflictMedicineUserConflict(
    val item_name: String,
    val item_seq: String,
    val effect_name: String,
    val class_name: String,
    val entp_name: String,
    val item_image: String
)