package com.example.pill_mate_android.pillSearch.presenter

import com.example.pill_mate_android.pillSearch.view.RegistrationData

class MedicineRegistrationPresenter {

    // 데이터를 저장할 리스트
    private val dataList = mutableListOf<RegistrationData>()

    fun addOrUpdateData(label: String, data: String) {
        val existingItem = dataList.find { it.label == label }
        if (existingItem != null) {
            existingItem.data = data
        } else {
            dataList.add(RegistrationData(label, data))
        }
    }

    fun getDataList(): List<RegistrationData> {
        return dataList
    }
}