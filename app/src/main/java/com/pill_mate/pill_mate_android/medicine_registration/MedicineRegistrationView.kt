package com.pill_mate.pill_mate_android.medicine_registration

interface MedicineRegistrationView {
    fun updateRecyclerView(data: List<RegistrationData>) // RecyclerView 데이터를 갱신하는 메서드
    fun showConfirmationBottomSheet()
}