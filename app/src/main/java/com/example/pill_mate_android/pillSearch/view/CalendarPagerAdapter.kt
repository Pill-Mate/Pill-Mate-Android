package com.example.pill_mate_android.pillSearch.view

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarPagerAdapter(
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.CalendarViewHolder>() {

    private val calendarList = mutableListOf<Calendar>()

    init {
        prepareCalendarList()
    }

    private fun prepareCalendarList() {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.add(Calendar.MONTH, -6) // 6개월 전부터 시작
        repeat(12) { // 12개월 데이터 생성
            calendarList.add(currentCalendar.clone() as Calendar)
            currentCalendar.add(Calendar.MONTH, 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_month, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendarList[position], onDateSelected)
    }

    override fun getItemCount(): Int = calendarList.size

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val gridLayout: GridLayout = view.findViewById(R.id.grid_dates)

        fun bind(calendar: Calendar, onDateSelected: (String) -> Unit) {
            gridLayout.removeAllViews()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val startDayOfWeek = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }.get(Calendar.DAY_OF_WEEK)

            populateGrid(year, month, maxDay, startDayOfWeek, onDateSelected)
        }

        private fun populateGrid(
            year: Int,
            month: Int,
            maxDay: Int,
            startDayOfWeek: Int,
            onDateSelected: (String) -> Unit
        ) {
            // 빈 칸 채우기 (달의 시작 요일 전)
            addEmptyViews(startDayOfWeek - 1)

            // 날짜 채우기
            for (day in 1..maxDay) {
                val dayView = createDayView(year, month, day, onDateSelected)
                gridLayout.addView(dayView)
            }
        }

        private fun addEmptyViews(count: Int) {
            repeat(count) {
                val emptyView = TextView(itemView.context).apply {
                    text = ""
                    layoutParams = createGridLayoutParams()
                }
                gridLayout.addView(emptyView)
            }
        }

        private fun createDayView(
            year: Int,
            month: Int,
            day: Int,
            onDateSelected: (String) -> Unit
        ): TextView {
            return TextView(itemView.context).apply {
                text = day.toString()
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                setBackgroundResource(R.drawable.bg_date_normal)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0 // GridLayout 내에서 균등 분배
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(6, 6, 6, 6) // 이미지 비율에 맞는 간격 설정
                }
                setOnClickListener {
                    val selectedDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(
                        Calendar.getInstance().apply { set(year, month, day) }.time
                    )
                    onDateSelected(selectedDate)
                    updateSelectedDayView(day)
                }
            }
        }

        private fun createGridLayoutParams(): GridLayout.LayoutParams {
            return GridLayout.LayoutParams().apply {
                width = 0 // GridLayout 내에서 균등 분배
                height = 0 // 행 크기 균등 분배
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(4, 4, 4, 4) // 날짜 간격을 균일하게 설정
            }
        }

        private fun updateSelectedDayView(selectedDay: Int) {
            for (i in 0 until gridLayout.childCount) {
                val dayView = gridLayout.getChildAt(i)
                if (dayView is TextView && dayView.text == selectedDay.toString()) {
                    dayView.setBackgroundResource(R.drawable.bg_date_selected)
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else if (dayView is TextView && dayView.text.isNotEmpty()) {
                    dayView.setBackgroundResource(R.drawable.bg_date_normal)
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }
            }
        }
    }
}