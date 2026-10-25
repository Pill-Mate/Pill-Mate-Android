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
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentPillStatsBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.pillcheck.util.fetch
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
        showCalendar(emptySet())

        loadDrugs()
        loadCountTrend()
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
            val current = selectedDrug ?: return@setOnClickListener
            val labels = drugLabels()
            PillStatsDrugBottomSheetFragment.newInstance(labels, labels[drugs.indexOf(current)])
                .show(childFragmentManager, PillStatsDrugBottomSheetFragment.TAG)
        }

        childFragmentManager.setFragmentResultListener(
            PillStatsDrugBottomSheetFragment.RESULT_KEY, viewLifecycleOwner
        ) { _, result ->
            val label = result.getString(PillStatsDrugBottomSheetFragment.RESULT_SELECTED_DRUG)
            drugs.getOrNull(drugLabels().indexOf(label))?.let(::selectDrug)
        }
    }

    // 같은 이름의 약이 여러 개면 라디오 선택이 섞이지 않도록 "이름 (2)"처럼 구분한다.
    private fun drugLabels(): List<String> {
        val seen = mutableMapOf<String, Int>()
        return drugs.map { drug ->
            val order = seen.merge(drug.name, 1, Int::plus) ?: 1
            if (order == 1) drug.name else "${drug.name} ($order)"
        }
    }

    private fun loadDrugs() {
        ServiceCreator.statisticsService.getMedicines().fetch { medicines ->
            if (_binding == null) return@fetch

            drugs = medicines.map { StatsDrug(it.medicineId, it.medicineName, it.totalIntakeDays.toInt()) }
            drugs.firstOrNull()?.let(::selectDrug)
        }
    }

    private fun selectDrug(drug: StatsDrug) {
        selectedDrug = drug
        showTakingSummary(drug.name, drug.takingDays)
        showCalendar(emptySet())
        loadCalendar()
    }

    private fun moveMonth(offset: Int) {
        displayedMonth.add(Calendar.MONTH, offset)
        showCalendar(emptySet())
        loadCalendar()
    }

    private fun loadCalendar() {
        val drug = selectedDrug ?: return
        val year = displayedMonth.get(Calendar.YEAR)
        val month = displayedMonth.get(Calendar.MONTH) + 1

        ServiceCreator.statisticsService.getCalendar(drug.medicineId, year, month).fetch { calendar ->
            // 응답을 기다리는 사이 화면이 닫히거나 약물/월이 바뀌었으면 무시
            val isStale = _binding == null || selectedDrug != drug ||
                displayedMonth.get(Calendar.YEAR) != year || displayedMonth.get(Calendar.MONTH) + 1 != month
            if (isStale) return@fetch

            showCalendar(calendar.days.filter { it.isTaken }.map { it.dayOfMonth }.toSet())
        }
    }

    private fun loadCountTrend() {
        ServiceCreator.statisticsService.getMedicineCountTrend().fetch { result ->
            if (_binding == null) return@fetch

            val points = result.trend.map { MedicineCountChartView.Point(it.monthLabel, it.count) }
            showCountTrend(points, result.diffFromLastMonth)
            binding.tvRecommendedMax.text = "권장 복약갯수: ${result.recommendedMax}개 이하"
        }
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
        binding.tvDrugName.text = if (drugName.length > DRUG_CHIP_NAME_MAX_LENGTH) {
            drugName.take(DRUG_CHIP_NAME_MAX_LENGTH) + "..."
        } else {
            drugName
        }
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

    private companion object {
        const val DAYS_IN_WEEK = 7
        const val DRUG_CHIP_NAME_MAX_LENGTH = 5
    }
}
