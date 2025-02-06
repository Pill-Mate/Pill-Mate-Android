package com.pill_mate.pill_mate_android.medicine_registration

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarPagerAdapter(
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.CalendarViewHolder>() {

    private val calendarList = mutableListOf<Calendar>()
    private val initialMonthsCount = 12 // 당월 기준 6개월 전후
    private var initialSelectedDate: String? = null

    init {
        prepareInitialCalendarList()
    }

    fun setInitialSelectedDate(date: String) {
        initialSelectedDate = date // ✅ 초기 선택 날짜 저장
    }

    private fun prepareInitialCalendarList() {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.add(Calendar.MONTH, -6) // 6개월 전부터 시작
        repeat(initialMonthsCount) {
            calendarList.add(currentCalendar.clone() as Calendar)
            currentCalendar.add(Calendar.MONTH, 1)
        }
    }

    // 과거 3개월씩 추가 (스크롤 시)
    fun addPastMonths(count: Int) {
        val firstCalendar = calendarList.first().clone() as Calendar
        for (i in 1..count) {
            firstCalendar.add(Calendar.MONTH, -1)
            calendarList.add(0, firstCalendar.clone() as Calendar)
        }
        notifyItemRangeInserted(0, count)
    }

    //미래 3개월씩 추가 (스크롤 시)
    fun addFutureMonths(count: Int) {
        val lastCalendar = calendarList.last().clone() as Calendar
        val startPosition = calendarList.size
        for (i in 1..count) {
            lastCalendar.add(Calendar.MONTH, 1)
            calendarList.add(lastCalendar.clone() as Calendar)
        }
        notifyItemRangeInserted(startPosition, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_month, parent, false)
        return CalendarViewHolder(view, initialSelectedDate)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendarList[position], onDateSelected)
    }

    override fun getItemCount(): Int = calendarList.size

    class CalendarViewHolder(view: View, private val initialSelectedDate: String?) : RecyclerView.ViewHolder(view) {
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
            addEmptyViews(startDayOfWeek - 1)

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
                layoutParams = createGridLayoutParams()

                addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                    val size = minOf(v.width, v.height)
                    v.layoutParams.width = size
                    v.layoutParams.height = size
                    v.requestLayout()
                }

                val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(
                    Calendar.getInstance().apply { set(year, month, day) }.time
                )

                // ✅ 기존 선택된 날짜 강조 표시
                if (date == initialSelectedDate) {
                    background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_date_selected)
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }

                setOnClickListener {
                    onDateSelected(date)
                    updateSelectedDayView(day)
                }
            }
        }

        private fun createGridLayoutParams(): GridLayout.LayoutParams {
            return GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
        }

        private fun updateSelectedDayView(selectedDay: Int) {
            for (i in 0 until gridLayout.childCount) {
                val dayView = gridLayout.getChildAt(i)
                if (dayView is TextView && dayView.text == selectedDay.toString()) {
                    dayView.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_date_selected)
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else if (dayView is TextView && dayView.text.isNotEmpty()) {
                    dayView.background = null
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }
            }
        }
    }
}