package com.example.pill_mate_android.ui.pillcheck

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pill_mate_android.ui.pillcheck.fragment.WeeklyCalendarFragment
import java.time.LocalDate

class CalendarVPAdapter(
    fragmentActivity: FragmentActivity, private val onClickListener: IDateClickListener
) : FragmentStateAdapter(fragmentActivity) {

    val fragments = mutableMapOf<Int, WeeklyCalendarFragment>()

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val fragment = WeeklyCalendarFragment.newInstance(position, onClickListener)
        fragments[position] = fragment
        return fragment
    }

    // 강제로 데이터 갱신
    fun updateFragment(position: Int, date: LocalDate) {
        fragments[position]?.forceUpdate(date)
    }

    // Adapter가 해당 Fragment를 유지하고 있는지 확인
    override fun containsItem(itemId: Long): Boolean {
        return fragments.containsKey(itemId.toInt())
    }
}
