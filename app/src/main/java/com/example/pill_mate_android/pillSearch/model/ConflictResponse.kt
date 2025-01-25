package com.example.pill_mate_android.pillSearch.model

data class ConflictResponse(
    val medicine: ConflictMedicine,
    val conflicts: List<Conflict>
)

data class ConflictMedicine(
    val name: String,
    val ingredient: String,
    val image: String,
    val manufacturer: String
)

data class Conflict(
    val crashName: String,
    val prohibitContent: String,
    val conflictingMedicine: ConflictMedicine,
    val pharmacy: Pharmacy,
    val hospital: Hospital?  // 병원 정보 (nullable)
)
