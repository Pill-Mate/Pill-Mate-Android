package com.pill_mate.pill_mate_android.medicine_registration

import android.content.Context
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
        initialSelectedDate = date // 초기 선택 날짜 저장
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
        val newCalendars = mutableListOf<Calendar>()
        val firstCalendar = calendarList.first().clone() as Calendar

        for (i in 1..count) {
            firstCalendar.add(Calendar.MONTH, -1)
            newCalendars.add(0, firstCalendar.clone() as Calendar)
        }

        calendarList.addAll(0, newCalendars)
        notifyItemRangeInserted(0, count)
    }

    // 미래 3개월씩 추가 (스크롤 시)
    fun addFutureMonths(count: Int) {
        val lastCalendar = calendarList.last().clone() as Calendar

        for (i in 1..count) {
            lastCalendar.add(Calendar.MONTH, 1)
            calendarList.add(lastCalendar.clone() as Calendar)
        }

        notifyItemRangeInserted(calendarList.size - count, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_month, parent, false)
        return CalendarViewHolder(view, initialSelectedDate)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendarList[position], onDateSelected)
    }

    override fun getItemCount(): Int = calendarList.size

    fun getCalendarAt(position: Int): Calendar {
        return calendarList[position]
    }

    class CalendarViewHolder(view: View, private val initialSelectedDate: String?) :
        RecyclerView.ViewHolder(view) {

        private val gridLayout: GridLayout = view.findViewById(R.id.grid_dates)

        fun bind(calendar: Calendar, onDateSelected: (String) -> Unit) {
            gridLayout.removeAllViews()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            // 달의 첫 날의 요일 (1=일요일, 7=토요일)
            val startDayOfWeek = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)

            populateGrid(year, month, maxDay, startDayOfWeek, onDateSelected)
        }

        private fun populateGrid(
            year: Int,
            month: Int,
            maxDay: Int,
            startDayOfWeek: Int,
            onDateSelected: (String) -> Unit
        ) {
            // 빈 칸 추가 (달력의 첫 번째 줄에서 시작 요일 이전의 빈 칸)
            addEmptyViews(startDayOfWeek - 1, itemView.context)

            // 실제 날짜 추가
            for (day in 1..maxDay) {
                val dayView = createDayView(year, month, day, onDateSelected)
                gridLayout.addView(dayView)
            }
        }

        private fun addEmptyViews(count: Int, context: Context) {
            if (count <= 0) return

            repeat(count) {
                val emptyView = TextView(context).apply {
                    text = ""  // 빈칸
                    gravity = Gravity.CENTER
                    layoutParams = createGridLayoutParams(context) // 크기 동일하게 적용
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
                layoutParams = createGridLayoutParams(itemView.context) // context 전달

                post {
                    val size = minOf(measuredWidth, measuredHeight)
                    layoutParams.width = size
                    layoutParams.height = size
                    setPadding(0, 0, 0, 0)
                    requestLayout()
                }

                val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(
                    Calendar.getInstance().apply { set(year, month, day) }.time
                )

                if (date == initialSelectedDate) {
                    background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.bg_date_selected)
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }

                setOnClickListener {
                    onDateSelected(date)
                    updateSelectedDayView(day)
                }
            }
        }

        private fun createGridLayoutParams(context: Context): GridLayout.LayoutParams {
            return GridLayout.LayoutParams().apply {
                width = dpToPx(context, 38)  // 빈칸 포함 크기 고정
                height = dpToPx(context, 38)

                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 7열 균등 배분
                setMargins(8, 8, 8, 8) // 여백 추가
            }
        }

        // dp → px 변환 함수
        private fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }

        private fun updateSelectedDayView(selectedDay: Int) {
            for (i in 0 until gridLayout.childCount) {
                val dayView = gridLayout.getChildAt(i) as? TextView ?: continue
                if (dayView.text.toString() == selectedDay.toString()) {
                    dayView.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.bg_date_selected)
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else {
                    dayView.background = null
                    dayView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }
            }
        }
    }
}