package com.pill_mate.pill_mate_android.ui.main.contract

import androidx.fragment.app.Fragment

interface MainContract {
    interface View {
        fun setWindowInsets()
        fun initBottomNavi()
        fun showFragment(fragment: Fragment)
    }

    interface Presenter {
        fun onCreate()
        fun onBottomNavigationItemSelected(itemId: Int)
    }
}