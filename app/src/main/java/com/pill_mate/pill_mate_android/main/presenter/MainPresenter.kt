package com.pill_mate.pill_mate_android.main.presenter

import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.main.contract.MainContract
import com.pill_mate.pill_mate_android.pillcheck.view.PillCheckFragment
import com.pill_mate.pill_mate_android.pilledit.view.PillEditFragment
import com.pill_mate.pill_mate_android.pillsearch.PillSearchFragment
import com.pill_mate.pill_mate_android.pillstats.PillStatsFragment

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {

    override fun onCreate() {
        view.setWindowInsets()
        view.initBottomNavi()
        view.showFragment(PillCheckFragment()) // 초기 프레그먼트 설정
    }

    override fun onBottomNavigationItemSelected(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.menu_home -> PillCheckFragment()
            R.id.menu_stats -> PillStatsFragment()
            R.id.menu_search -> PillSearchFragment()
            else -> PillEditFragment()
        }
        view.showFragment(fragment)
    }

    override fun onPlusButtonClicked() {
        view.navigateToMedicineRegistration()
    }
}