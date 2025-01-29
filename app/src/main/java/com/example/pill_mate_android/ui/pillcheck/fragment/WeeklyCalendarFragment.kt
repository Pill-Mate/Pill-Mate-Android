package com.example.pill_mate_android.ui.pillcheck.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentWeeklyCalendarBinding
import com.example.pill_mate_android.ui.pillcheck.IDateClickListener
import com.example.pill_mate_android.ui.pillcheck.WeeklyIconsData
import java.time.LocalDate

class WeeklyCalendarFragment : Fragment() {

    private var _binding: FragmentWeeklyCalendarBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")

    private lateinit var textViewList: List<TextView> // 일주일 날짜를 넣어줄 TextView의 리스트
    private lateinit var dates: List<LocalDate> // 일주일의 날짜를 받아올 dates

    private val todayPosition = Int.MAX_VALUE / 2 // 기준 포지션
    private var position: Int = 0 // 달력이 스크롤되었을 때 포지션 계산을 위한 변수
    private lateinit var onClickListener: IDateClickListener // 선택된 날짜를 넘겨받기 위한 인터페이스

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeeklyCalendarBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 달력 페이지를 넘겼을 때의 기준 날짜를 받아오기 위함
        val newDate = calculateNewDate()

        calculateDatesOfWeek(newDate)

        setOneWeekDateIntoTextView()
    }

    override fun onResume() {
        super.onResume()

        setPrevSelectedDate()
    }

    override fun onPause() {
        super.onPause()
        resetUi()
    }

    private fun initViews() {
        with(binding) {
            textViewList = listOf(
                tv1, tv2, tv3, tv4, tv5, tv6, tv7
            )
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun calculateNewDate(): LocalDate {
        val curDate = LocalDate.now()
        return if (position < todayPosition) { // 이전 페이지로 스크롤
            curDate.minusDays(((todayPosition - position) * 7).toLong())
        } else if (position > todayPosition) { // 다음 페이지로 스크롤
            curDate.plusDays(((position - todayPosition) * 7).toLong())
        } else {
            curDate
        }
    }

    private fun setPrevSelectedDate() {
        val sharedPreference = context?.getSharedPreferences("CALENDAR-APP", Context.MODE_PRIVATE)
        val selectedDate = sharedPreference?.getString("SELECTED-DATE", "")
        for (i in textViewList.indices) {
            if (selectedDate.toString() == dates[i].toString()) {
                setSelectedDate(textViewList[i])
            }
        }
    }

    private fun setOneWeekDateIntoTextView() { // 일주일의 날짜를 텍스트뷰에 넣어주기
        for (i in textViewList.indices) {
            setDate(textViewList[i], dates[i])
        }
    }

    private fun setDate(textView: TextView, date: LocalDate) { // ex. date: 2023-12-23
        val splits = date.toString().split('-')

        textView.text = splits[2].toInt().toString() // 날짜의 뒷 부분(일)만 가져와서 사용
        // 날짜 선택 시의 동작
        textView.setOnClickListener {
            resetUi() // 모든 날짜 선택 해제
            onClickListener.onClickDate(date) // 인터페이스를 통해 클릭한 날짜 전달
            setSelectedDate(textView)
        }
    }

    private fun resetUi() { // 모든 날짜 선택 해제
        for (i in textViewList.indices) {
            textViewList[i].setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.gray_2
                )
            )
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)
            textViewList[i].setTypeface(typeface, Typeface.NORMAL)

        }
    }

    private fun setSelectedDate(textView: TextView) { // 선택한 날짜 UI
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue_1)) // 글자색
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_semibold)
        textView.setTypeface(typeface, Typeface.NORMAL)
    }

    @RequiresApi(VERSION_CODES.O)
    fun updateWeeklyIcons(responseData: WeeklyIconsData) {
        val icons = listOf(
            binding.icSun, binding.icMon, binding.icTue, binding.icWed, binding.icThu, binding.icFri, binding.icSat
        )
        val days = listOf(
            responseData.sunday,
            responseData.monday,
            responseData.tuesday,
            responseData.wednesday,
            responseData.thursday,
            responseData.friday,
            responseData.saturday
        )

        try {
            icons.zip(days).forEach { (icon, isActive) ->
                val color = if (isActive) {
                    ContextCompat.getColor(requireContext(), R.color.main_blue_1) // 활성 상태 색상
                } else {
                    ContextCompat.getColor(requireContext(), R.color.gray_3) // 비활성 상태 색상
                }
                icon.setColorFilter(color) // 아이콘 색상 변경
            }
        } catch (e: Exception) {
            Log.e("WeeklyCalendarFragment", "updateDateIcons 예외 발생: ${e.message}")
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun calculateDatesOfWeek(today: LocalDate) { // 최초 주간 달력 날짜들을 표시하기 위함
        val dates = ArrayList<LocalDate>() // 일 ~ 토까지 한 주간의 날짜 리스트 추가하기
        val dayOfToday = today.dayOfWeek.value // 기준 날짜의 요일 구하기
        Log.e("Calendar", "dayOfToday: $dayOfToday")

        if (dayOfToday == SUNDAY) { // 일요일일 경우 다음 리스트를 받아오기 (일요일을 가장 먼저 표시하기 때문에)
            for (day in MONDAY..SUNDAY) { // 일(오늘) ~ 그 다음주 토
                dates.add(today.plusDays((day - 1).toLong()))
            }
        } else {
            for (day in (MONDAY - 1) until dayOfToday) { // 일 ~ 오늘
                dates.add(today.minusDays((dayOfToday - day).toLong()))
            }
            for (day in dayOfToday..<SUNDAY) { // 오늘 ~ 토
                dates.add(today.plusDays((day - dayOfToday).toLong()))
            }
        }
        this.dates = dates

    }

    companion object {
        fun newInstance(
            position: Int, onClickListener: IDateClickListener
        ): WeeklyCalendarFragment {
            val fragment = WeeklyCalendarFragment()
            fragment.position = position
            fragment.onClickListener = onClickListener
            return fragment
        }

        const val MONDAY = 1
        const val SUNDAY = 7
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun forceUpdate(date: LocalDate) {
        resetUi()
        for (i in textViewList.indices) {
            if (dates[i] == date) {
                setSelectedDate(textViewList[i]) // 선택된 날짜 스타일 적용
                break
            }
        }
    }

}