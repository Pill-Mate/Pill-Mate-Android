package com.pill_mate.pill_mate_android.pillstats

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentPillStatsBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import java.util.Calendar

class PillStatsFragment : Fragment() {
    private var _binding: FragmentPillStatsBinding? = null
    private val binding get() = _binding!!

    private val calendarAdapter = PillStatsCalendarAdapter()
    private val displayedMonth: Calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }

    private var drugs: List<StatsDrug> = emptyList()
    private var selectedDrug: StatsDrug? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPillStatsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCalendar()
        setUpDrugSelection()
        showSampleData()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setDefaultStatusBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpCalendar() {
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), DAYS_IN_WEEK)
        binding.rvCalendar.adapter = calendarAdapter

        binding.btnPrevMonth.setOnClickListener { moveMonth(-1) }
        binding.btnNextMonth.setOnClickListener { moveMonth(1) }
    }

    private fun setUpDrugSelection() {
        binding.layoutDrugSelect.setOnClickListener {
            if (drugs.isEmpty()) return@setOnClickListener
            PillStatsDrugBottomSheetFragment.newInstance(drugs.map { it.name }, selectedDrug?.name)
                .show(childFragmentManager, PillStatsDrugBottomSheetFragment.TAG)
        }

        childFragmentManager.setFragmentResultListener(
            PillStatsDrugBottomSheetFragment.RESULT_KEY, viewLifecycleOwner
        ) { _, result ->
            val name = result.getString(PillStatsDrugBottomSheetFragment.RESULT_SELECTED_DRUG)
            drugs.firstOrNull { it.name == name }?.let(::selectDrug)
        }
    }

    private fun selectDrug(drug: StatsDrug) {
        selectedDrug = drug
        showTakingSummary(drug.name, drug.takingDays)
        showCalendar(drug.takenDays)
    }

    private fun moveMonth(offset: Int) {
        displayedMonth.add(Calendar.MONTH, offset)
        showCalendar(selectedDrug?.takenDays.orEmpty())
    }

    private fun showCalendar(takenDays: Set<Int>) {
        binding.tvMonth.text = "${displayedMonth.get(Calendar.YEAR)}년 ${displayedMonth.get(Calendar.MONTH) + 1}월"

        val leadingBlanks = displayedMonth.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val cells = List(leadingBlanks) { PillStatsCalendarAdapter.DayCell(0) } + (1..daysInMonth).map { day ->
            PillStatsCalendarAdapter.DayCell(day, isTaken = day in takenDays)
        }
        calendarAdapter.submitCells(cells)
    }

    private fun showTakingSummary(drugName: String, takingDays: Int) {
        binding.tvDrugName.text = drugName
        binding.tvDrugLabel.text = drugName
        binding.tvTakingDays.text = highlighted("${takingDays}일째", " 복용중")
    }

    private fun showCountTrend(points: List<MedicineCountChartView.Point>, diffFromLastMonth: Int) {
        binding.chartMedicineCount.setPoints(points)
        binding.tvCountDiff.text = when {
            diffFromLastMonth < 0 -> highlighted("${-diffFromLastMonth}종", " 줄었어요")
            diffFromLastMonth > 0 -> highlighted("${diffFromLastMonth}종", " 늘었어요")
            else -> highlighted("변화", " 없어요")
        }
    }

    private fun highlighted(highlight: String, rest: String): CharSequence = SpannableStringBuilder().apply {
        append(
            highlight,
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_blue_1)),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        append(rest)
    }

    // 서버 연동 전 UI 확인용 샘플 데이터. API 연결 시 drugs와 show* 함수에 실제 데이터를 넘기면 된다.
    private fun showSampleData() {
        drugs = SAMPLE_DRUGS
        selectDrug(SAMPLE_DRUGS.first())

        val month = Calendar.getInstance()
        val points = SAMPLE_COUNTS.mapIndexed { index, count ->
            val labelMonth = (month.clone() as Calendar).apply { add(Calendar.MONTH, index - SAMPLE_COUNTS.lastIndex) }
            MedicineCountChartView.Point("${labelMonth.get(Calendar.MONTH) + 1}월", count)
        }
        showCountTrend(points, diffFromLastMonth = SAMPLE_COUNTS.last() - SAMPLE_COUNTS[SAMPLE_COUNTS.lastIndex - 1])
    }

    private companion object {
        const val DAYS_IN_WEEK = 7
        val SAMPLE_COUNTS = listOf(6, 4, 7, 7, 8, 5, 7)
        val SAMPLE_DRUGS = listOf(
            StatsDrug("오그맥스정", 14, setOf(1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16)),
            StatsDrug("타이레놀정", 7, setOf(10, 11, 12, 13, 14, 15, 16)),
            StatsDrug("비타민C", 21, (1..16).toSet() - setOf(3, 9))
        )
    }
}
