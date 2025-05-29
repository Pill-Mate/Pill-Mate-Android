package com.pill_mate.pill_mate_android.medicine_registration

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
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
    private val initialMonthsCount = 12
    private var initialSelectedDate: String? = null

    init {
        prepareInitialCalendarList()
    }

    fun setInitialSelectedDate(date: String) {
        initialSelectedDate = date
    }

    private fun prepareInitialCalendarList() {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.add(Calendar.MONTH, -6)
        repeat(initialMonthsCount) {
            calendarList.add(currentCalendar.clone() as Calendar)
            currentCalendar.add(Calendar.MONTH, 1)
        }
    }

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

    fun addFutureMonths(count: Int) {
        val lastCalendar = calendarList.last().clone() as Calendar

        for (i in 1..count) {
            lastCalendar.add(Calendar.MONTH, 1)
            calendarList.add(lastCalendar.clone() as Calendar)
        }

        notifyItemRangeInserted(calendarList.size - count, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_month, parent, false)
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
            addEmptyViews(startDayOfWeek - 1, itemView.context)
            for (day in 1..maxDay) {
                val dayView = createDayView(year, month, day, onDateSelected)
                gridLayout.addView(dayView)
            }
        }

        private fun addEmptyViews(count: Int, context: Context) {
            if (count <= 0) return

            repeat(count) {
                val emptyView = FrameLayout(context).apply {
                    layoutParams = createGridLayoutParams(context)
                }
                gridLayout.addView(emptyView)
            }
        }

        private fun createDayView(
            year: Int,
            month: Int,
            day: Int,
            onDateSelected: (String) -> Unit
        ): View {
            val context = itemView.context
            val layout = FrameLayout(context)
            layout.layoutParams = createGridLayoutParams(context)

            val cellSize = createGridCellSize(context) // width, height 계산하는 함수
            val circleSize = minOf(cellSize.first, cellSize.second) * 0.8  // 80% 크기 원

            val circle = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(circleSize.toInt(), circleSize.toInt(), Gravity.CENTER)
                setImageResource(R.drawable.bg_circle)
                visibility = View.GONE
            }

            val label = TextView(context).apply {
                text = day.toString()
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.black))
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }

            layout.addView(circle)
            layout.addView(label)

            val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(
                Calendar.getInstance().apply { set(year, month, day) }.time
            )

            if (date == initialSelectedDate) {
                circle.visibility = View.VISIBLE
                label.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            layout.setOnClickListener {
                onDateSelected(date)
                updateSelectedDayView(day)
            }

            return layout
        }

        private fun createGridLayoutParams(context: Context): GridLayout.LayoutParams {
            val displayMetrics = context.resources.displayMetrics
            val outerMargin = dpToPx(context, 25)
            val totalWidth = displayMetrics.widthPixels - (outerMargin * 2)

            val cellCount = 7
            val cellWidth = totalWidth / cellCount
            val cellHeight = (cellWidth * 0.85).toInt()

            return GridLayout.LayoutParams().apply {
                width = cellWidth
                height = cellHeight
            }
        }

        private fun createGridCellSize(context: Context): Pair<Int, Int> {
            val displayMetrics = context.resources.displayMetrics

            val outerMargin = dpToPx(context, 25) // ViewPager 좌우 margin (match your XML)
            val totalWidth = displayMetrics.widthPixels - (outerMargin * 2)

            val cellCount = 7
            val cellWidth = totalWidth / cellCount
            val cellHeight = (cellWidth * 0.85).toInt() // 높이를 더 작게

            return Pair(cellWidth, cellHeight)
        }

        private fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density).toInt()
        }

        private fun updateSelectedDayView(selectedDay: Int) {
            for (i in 0 until gridLayout.childCount) {
                val layout = gridLayout.getChildAt(i) as? FrameLayout ?: continue
                val label = layout.getChildAt(1) as? TextView ?: continue
                val circle = layout.getChildAt(0) as? ImageView ?: continue

                if (label.text.toString() == selectedDay.toString()) {
                    circle.visibility = View.VISIBLE
                    label.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else {
                    circle.visibility = View.GONE
                    label.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }
            }
        }
    }
}