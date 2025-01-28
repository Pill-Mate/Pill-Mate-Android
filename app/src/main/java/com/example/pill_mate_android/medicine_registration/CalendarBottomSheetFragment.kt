package com.example.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.pill_mate_android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var tvCalendarMonth: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var btnSelectDate: Button
    private lateinit var calendarAdapter: CalendarPagerAdapter

    private var selectedDate: String? = null
    private lateinit var onDateSelected: (String) -> Unit

    companion object {
        fun newInstance(onDateSelected: (String) -> Unit): CalendarBottomSheetFragment {
            val fragment = CalendarBottomSheetFragment()
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
            selectedDate = date
        }
        viewPager.adapter = calendarAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MONTH, position - 6)
                }
                updateMonthText(calendar)

                if (position <= 2) {
                    calendarAdapter.addPastMonths(3)
                    viewPager.setCurrentItem(position + 3, false)
                } else if (position >= calendarAdapter.itemCount - 3) {
                    calendarAdapter.addFutureMonths(3)
                }
            }
        })

        val initialPosition = 6
        viewPager.setCurrentItem(initialPosition, false)
        updateMonthText(Calendar.getInstance())
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
            selectedDate?.let { date ->
                onDateSelected(date)
                dismiss()
            }
        }
    }

    private fun updateMonthText(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        tvCalendarMonth.text = "${year}년 ${month}월"
    }
}