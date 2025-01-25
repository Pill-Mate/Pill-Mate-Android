package com.example.pill_mate_android.ui.main.presenter

import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ui.main.contract.MainContract
import com.example.pill_mate_android.ui.mypage.MyPageFragment
import com.example.pill_mate_android.ui.pillcheck.PillCheckFragment
import com.example.pill_mate_android.ui.pilledit.PillEditFragment
import com.example.pill_mate_android.ui.pillsearch.PillSearchFragment
import com.example.pill_mate_android.ui.pillstats.PillStatsFragment

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {

    override fun onCreate() {
        view.setWindowInsets()
        view.initBottomNavi()
        view.showFragment(PillCheckFragment()) // 초기 프레그먼트 설정
    }

    override fun onBottomNavigationItemSelected(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.menu_check -> PillCheckFragment()
            R.id.menu_stats -> PillStatsFragment()
            R.id.menu_edit -> PillEditFragment()
            R.id.menu_search -> PillSearchFragment()
            else -> MyPageFragment()
        }
        view.showFragment(fragment)
    }
}