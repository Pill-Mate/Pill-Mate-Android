package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.pill_mate.pill_mate_android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import java.util.*

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var tvCalendarMonth: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var btnSelectDate: Button
    private lateinit var calendarAdapter: CalendarPagerAdapter

    private var selectedDate: String = DateConversionUtil.getCurrentDate()
    private lateinit var onDateSelected: (String) -> Unit

    companion object {
        fun newInstance(initialSelectedDate: String?, onDateSelected: (String) -> Unit): CalendarBottomSheetFragment {
            val fragment = CalendarBottomSheetFragment()
            fragment.selectedDate = initialSelectedDate ?: DateConversionUtil.getCurrentDate()
            fragment.onDateSelected = onDateSelected
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_bottom_sheet_calendar, container, false)

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCalendarMonth = view.findViewById(R.id.tv_calendar_month)
        viewPager = view.findViewById(R.id.vp_calendar)
        btnSelectDate = view.findViewById(R.id.btn_select_date)

        setupCalendarAdapter()
        setupNavigation(view)
        setupSelectButton()
    }

    private fun setupCalendarAdapter() {
        calendarAdapter = CalendarPagerAdapter { date ->
            selectedDate = date // 선택된 날짜 업데이트
        }

        calendarAdapter.setInitialSelectedDate(selectedDate) // 초기 선택 날짜 설정

        viewPager.adapter = calendarAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val calendar = calendarAdapter.getCalendarAt(position)
                updateMonthText(calendar)

                // 이전 3개월 추가
                if (position <= 2) {
                    val oldPosition = position + 3 // 새로 추가된 페이지를 고려한 위치 조정
                    calendarAdapter.addPastMonths(3)
                    viewPager.setCurrentItem(oldPosition, false)
                }

                // 다음 3개월 추가
                if (position >= calendarAdapter.itemCount - 3) {
                    calendarAdapter.addFutureMonths(3)
                }
            }
        })

        val initialPosition = calculateInitialPosition(selectedDate)
        viewPager.setCurrentItem(initialPosition, false)
        updateMonthText(getCalendarFromDate(selectedDate))
    }

    private fun setupNavigation(view: View) {
        view.findViewById<ImageView>(R.id.iv_calendar_previous).setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        view.findViewById<ImageView>(R.id.iv_calendar_next).setOnClickListener {
            if (viewPager.currentItem < calendarAdapter.itemCount - 1) {
                viewPager.currentItem += 1
            }
        }
    }

    private fun setupSelectButton() {
        btnSelectDate.setOnClickListener {
            onDateSelected(selectedDate)
            dismiss()
        }
    }

    private fun updateMonthText(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        tvCalendarMonth.text = getString(R.string.calendar_month_year_format, year, month)
    }

    private fun calculateInitialPosition(date: String): Int {
        val selectedCalendar = getCalendarFromDate(date)
        val firstCalendar = calendarAdapter.getCalendarAt(0) // 첫 번째 달력의 Calendar 객체
        val monthsDiff =
            (selectedCalendar.get(Calendar.YEAR) - firstCalendar.get(Calendar.YEAR)) * 12 +
                    (selectedCalendar.get(Calendar.MONTH) - firstCalendar.get(Calendar.MONTH))
        return monthsDiff
    }

    private fun getCalendarFromDate(date: String): Calendar {
        val parts = date.split(".")
        return Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }
    }
}