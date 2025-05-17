package com.pill_mate.pill_mate_android.main.contract

import androidx.fragment.app.Fragment

interface MainContract {
    interface View {
        fun setWindowInsets()
        fun initBottomNavi()
        fun showFragment(fragment: Fragment)
        fun navigateToMedicineRegistration()
    }

    interface Presenter {
        fun onCreate()
        fun onBottomNavigationItemSelected(itemId: Int)
        fun onPlusButtonClicked()
    }
}