package com.pill_mate.pill_mate_android.pillcheck.view

import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.math.MathUtils.clamp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentPillCheckBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationActivity
import com.pill_mate.pill_mate_android.notice.NotificationActivity
import com.pill_mate.pill_mate_android.pillcheck.model.GroupedMedicine
import com.pill_mate.pill_mate_android.pillcheck.model.HomeData
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineCheckData
import com.pill_mate.pill_mate_android.pillcheck.model.ResponseHome
import com.pill_mate.pill_mate_android.pillcheck.model.TimeGroup
import com.pill_mate.pill_mate_android.pillcheck.util.fetch
import com.pill_mate.pill_mate_android.pillcheck.view.adapter.CalendarVPAdapter
import com.pill_mate.pill_mate_android.pillcheck.view.adapter.IntakeCountAdapter
import com.pill_mate.pill_mate_android.setting.view.SettingActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max

class PillCheckFragment : Fragment(), IDateClickListener {

    private var _binding: FragmentPillCheckBinding? = null
    private val binding get() = _binding!!
    private var isFirstLoad = true
    private val expandedStates = mutableSetOf<Int>()
    private var isStatusBarLight = false // 현재 상태바 상태 추적
    private var progressBarAnimator: ValueAnimator? = null

    @RequiresApi(VERSION_CODES.O)
    var today: LocalDate = LocalDate.now()
    private lateinit var selectedDate: LocalDate
    private lateinit var dayTextViewList: List<TextView> //일주일 요일을 넣어줄 TextView의 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPillCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = today // 최초에는 선택 날짜를 오늘로 초기화

        initViews()
        setOneWeekViewPager()
        setCalendarButtonClickListener()
        setMainButtonClickListener()
        handleStatusBarByScroll()

        // 오늘 날짜에 대한 데이터 호출
        fetchHomeData(today)
    }

    @RequiresApi(VERSION_CODES.O)
    private fun initViews() {
        with(binding) {
            dayTextViewList = listOf(
                tvSun, tvMon, tvTue, tvWed, tvThu, tvFri, tvSat
            )
        } // 현재 날짜에 해당하는 요일 파란색으로 설정
        setSelectedDay(selectedDate)
        binding.tvYearMonth.text = dateFormat(selectedDate)
    }

    private fun handleStatusBarByScroll() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val threshold = 320 // 스크롤 기준 값(px)

            if (scrollY > threshold && !isStatusBarLight) {
                animateStatusBarChange(
                    toColor = ContextCompat.getColor(requireContext(), android.R.color.white), lightIcons = true
                )
                isStatusBarLight = true
            } else if (scrollY <= threshold && isStatusBarLight) {
                animateStatusBarChange(
                    toColor = ContextCompat.getColor(requireContext(), R.color.main_blue_1), lightIcons = false
                )
                isStatusBarLight = false
            }
        }
    }

    // 상태바 색상 변경 애니메이션
    private fun animateStatusBarChange(toColor: Int, lightIcons: Boolean) {
        val activity = activity as? MainActivity ?: return
        val window = activity.window
        val fromColor = window.statusBarColor

        val animator = ValueAnimator.ofArgb(fromColor, toColor)
        animator.duration = 300
        animator.addUpdateListener {
            window.statusBarColor = it.animatedValue as Int
        }
        animator.start()

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = lightIcons
    }

    @RequiresApi(VERSION_CODES.O)
    private fun changeSelectedDateByWeeks(weeks: Long) {
        selectedDate = selectedDate.plusWeeks(weeks) // 선택된 날짜를 weeks만큼 이동
        saveSelectedDate(selectedDate) // 변경된 날짜 저장
        binding.tvYearMonth.text = dateFormat(selectedDate)
        setSelectedDay(selectedDate) // 선택된 날짜에 대한 데이터 호출
        fetchHomeData(selectedDate)
    }

    @RequiresApi(VERSION_CODES.O)
    private fun setOneWeekViewPager() {
        saveSelectedDate(today)
        val calendarAdapter = CalendarVPAdapter(requireActivity(), this)
        binding.vpCalendar.adapter = calendarAdapter
        binding.vpCalendar.setCurrentItem(Int.MAX_VALUE / 2, false)

        // 뷰페이저의 페이지 변경시 요일 색상 초기화
        binding.vpCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position) // 페이지 변경 시 요일 색상 리셋
                resetUi() // 현재 페이지의 주간 시작 날짜 계산
                val startOfWeek = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                    .plusWeeks((position - Int.MAX_VALUE / 2).toLong())

                // 선택된 날짜가 현재 페이지의 주간에 포함되는지 확인
                if (!selectedDate.isBefore(startOfWeek) && !selectedDate.isAfter(startOfWeek.plusDays(6))) {
                    setSelectedDay(selectedDate)
                }

                fetchWeeklyCalendarData(startOfWeek)
            }
        })
    }

    // <> 버튼 클릭 시, 오늘 버튼 클릭 시
    @RequiresApi(VERSION_CODES.O)
    private fun setCalendarButtonClickListener() {
        binding.btnLeft.setOnClickListener {
            changeSelectedDateByWeeks(-1) // 일주일 전으로 이동
            moveToPreviousPage()
        }

        binding.btnRight.setOnClickListener {
            changeSelectedDateByWeeks(1) // 일주일 후로 이동
            moveToNextPage()
        }

        binding.btnToday.setOnClickListener { // 오늘 날짜로 이동
            resetToToday()
            moveToTodayPage()
        }

        binding.btnUpload.setOnClickListener { // 약물 등록 액티비티로 이동
            val intent = Intent(requireContext(), MedicineRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // 공지, 마이페이지로 이동
    private fun setMainButtonClickListener() {
        binding.btnSetting.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        binding.btnAlarm.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // 안읽은 공지가 있을 경우
        binding.btnAlarmActive.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun resetToToday() {
        selectedDate = today
        saveSelectedDate(selectedDate)
        binding.tvYearMonth.text = dateFormat(selectedDate)
        setSelectedDay(selectedDate) // 오늘 날짜에 대한 데이터 호출
        fetchHomeData(selectedDate)
    }

    private fun moveToNextPage() {
        val currentItem = binding.vpCalendar.currentItem
        binding.vpCalendar.setCurrentItem(currentItem + 1, true) // 다음 페이지로 이동
    }

    private fun moveToPreviousPage() {
        val currentItem = binding.vpCalendar.currentItem
        binding.vpCalendar.setCurrentItem(currentItem - 1, true) // 이전 페이지로 이동
    }

    // 오늘 날짜가 포함된 페이지로 이동
    @RequiresApi(VERSION_CODES.O)
    private fun moveToTodayPage() {
        val startOfWeek = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        val weeksFromStart = (today.toEpochDay() - startOfWeek.toEpochDay()) / 7
        val position = (Int.MAX_VALUE / 2) + weeksFromStart.toInt()

        if (binding.vpCalendar.currentItem == position) { // 현재 페이지라면 강제로 갱신
            val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
            adapter?.updateFragment(position, today)
        } else { // 다른 페이지라면 이동
            binding.vpCalendar.setCurrentItem(position, false)
        }

    }

    @RequiresApi(VERSION_CODES.O)
    override fun onClickDate(date: LocalDate) {
        selectedDate = date // 선택 날짜 저장
        saveSelectedDate(selectedDate) // 선택한 날짜 표시
        binding.tvYearMonth.text = dateFormat(selectedDate)
        setSelectedDay(selectedDate) // 선택한 날짜에 대한 데이터 호출
        fetchHomeData(selectedDate)
    }

    private fun saveSelectedDate(date: LocalDate) {
        val sharedPreference = requireActivity().getSharedPreferences("CALENDAR-APP", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString("SELECTED-DATE", date.toString())
        editor.apply()
    }

    @RequiresApi(VERSION_CODES.O)
    private fun setSelectedDay(date: LocalDate) { // 선택한 날짜 UI
        resetUi() // 모든 요일의 색상을 초기화
        val dayOfWeek = date.dayOfWeek.value % 7 // 요일 인덱스 (일요일이 0)
        setSelectedDayColor(dayTextViewList[dayOfWeek])
    }

    private fun resetUi() {
        for (textView in dayTextViewList) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)
            textView.setTypeface(typeface, Typeface.NORMAL)
        }
    }

    private fun setSelectedDayColor(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue_1)) // 글자색
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_semibold)
        textView.setTypeface(typeface, Typeface.NORMAL)
    }

    // 홈 데이터 받아오기
    private fun fetchHomeData(selectedDate: LocalDate) {
        ServiceCreator.homeService.getHomeData(HomeData(date = selectedDate.toString()))
            .fetch { handleHomeResponse(it) }
    }

    private fun handleHomeResponse(data: ResponseHome) {
        val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
        val currentFragment = adapter?.fragments?.get(binding.vpCalendar.currentItem)
        currentFragment?.updateWeeklyIcons(data) // 데이터 전달
        // 데이터를 그룹화하여 RecyclerView에 설정
        setupIntakeCountRecyclerView(data)
        updateHomeUI(data)
    }

    private fun updateHomeUI(responseData: ResponseHome) {
        with(binding) {
            if (responseData.medicineList.isNullOrEmpty()) {
                layoutNone.visibility = View.VISIBLE
                layoutProgressbar.visibility = View.INVISIBLE
                intakeCountRecyclerView.visibility = View.INVISIBLE
                Log.i("updateHomeUI", "불러올 리스트가 없습니다.")
            } else {
                layoutNone.visibility = View.INVISIBLE
                layoutProgressbar.visibility = View.VISIBLE
                intakeCountRecyclerView.visibility = View.VISIBLE
                tvNum.text = responseData.countAll.toString()
                tvRemain.text = if (responseData.countLeft == 0) "복약 완료" else "${responseData.countLeft}회 남음"

                setupProgressBar(responseData.countAll, responseData.countLeft, animate = false)
                setupIntakeCountRecyclerView(responseData)
            }
        }
        updateNotificationIcon(responseData.notificationRead)
    }

    //주간 달력 스크롤 시 데이터 받아오기
    @RequiresApi(VERSION_CODES.O)
    private fun fetchWeeklyCalendarData(selectedDate: LocalDate) {
        val homeData = HomeData(date = selectedDate.toString()) // 클릭한 날짜를 사용

        ServiceCreator.weeklyCalendarService.getWeeklyCalendarData(homeData).fetch {
            val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
            val currentFragment = adapter?.fragments?.get(binding.vpCalendar.currentItem)

            currentFragment?.updateWeeklyIcons(it)
        }
    }

    //체크박스 체크여부 데이터 보내기
    private fun patchMedicineCheckData(checkDataList: List<MedicineCheckData>) {
        ServiceCreator.medicineCheckService.patchCheckData(checkDataList).fetch {
            val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
            val currentFragment = adapter?.fragments?.get(binding.vpCalendar.currentItem)

            currentFragment?.updateWeeklyIcons(it) // 데이터 전달

            // 데이터를 그룹화하여 RecyclerView에 설정
            setupIntakeCountRecyclerView(it)

            if (it.medicineList.isNullOrEmpty()) {
                Log.i("데이터 전송 성공", "불러올 리스트가 없습니다.")
            } else {
                with(binding) {
                    tvNum.text = it.countAll.toString()
                    if (it.countLeft == 0) {
                        tvRemain.text = "복약 완료"
                    } else {
                        tvRemain.text = "${it.countLeft}회 남음"
                    }
                }
                setupProgressBar(it.countAll, it.countLeft, animate = true)
            }
        }

    }

    private fun setupIntakeCountRecyclerView(responseHome: ResponseHome) { // 데이터 그룹화
        val groupedMedicines = groupMedicines(responseHome)

        if (isFirstLoad) {
            expandedStates.clear()
            for (i in groupedMedicines.indices) {
                expandedStates.add(i)
            }
            isFirstLoad = false
        }

        // intakeCount RecyclerView 설정
        if (binding.intakeCountRecyclerView.adapter == null) {
            val intakeCountAdapter = IntakeCountAdapter(groupedMedicines, expandedStates) { checkDataList ->
                patchMedicineCheckData(checkDataList)
            }
            binding.intakeCountRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = intakeCountAdapter
            }
            intakeCountAdapter.updateExpandedStates()
        } else {
            val adapter = binding.intakeCountRecyclerView.adapter as IntakeCountAdapter
            adapter.updateData(groupedMedicines)
        }
    }

    private fun groupMedicines(responseHome: ResponseHome): List<GroupedMedicine> { // intakeCount 기준으로 그룹화
        val intakeGroups = responseHome.medicineList.groupBy { it.intakeCount }

        // 각 intakeCount 그룹 내에서 intakeTime으로 그룹화
        return intakeGroups.map { (intakeCount, medicines) ->
            val timeGroups = medicines.groupBy { it.intakeTime }.map { (time, medicinesAtTime) ->
                TimeGroup(
                    intakeTime = time, medicines = medicinesAtTime
                )
            }
            GroupedMedicine(
                intakeCount = intakeCount, times = timeGroups
            )
        }
    }

    // 프로그래스바 값 설정
    private fun setupProgressBar(countAll: Int, countLeft: Int, animate: Boolean = true) {
        val targetProgress = countAll - countLeft
        val progress = targetProgress.toDouble() / max(countAll, 1)

        binding.root.doOnLayout {
            val progressBarWidth = binding.pbNumberOfMedications.width
            val bubbleWidth = binding.layoutBubble.width

            binding.pbNumberOfMedications.max = countAll

            // animate:false => 기존 상태 반영 시(날짜 변경 등)
            // animate:true => 복약 체크/해제 시
            if (animate) {
                val currentProgress = binding.pbNumberOfMedications.progress

                progressBarAnimator = ValueAnimator.ofInt(currentProgress, targetProgress).apply {
                    duration = 600
                    interpolator = android.view.animation.DecelerateInterpolator()
                    addUpdateListener { animator ->
                        _binding?.pbNumberOfMedications?.progress = animator.animatedValue as Int
                    }
                    start()
                }
            } else {
                binding.pbNumberOfMedications.progress = targetProgress
            }

            val targetX = clamp(
                progressBarWidth * progress - bubbleWidth / 2, 0.0, progressBarWidth.toDouble() - bubbleWidth.toDouble()
            ).toFloat()

            binding.layoutBubble.animate().x(targetX).setDuration(if (animate) 600 else 0)
                .setInterpolator(android.view.animation.DecelerateInterpolator()).start()
        }
    }

    private fun updateNotificationIcon(isRead: Boolean) {
        binding.btnAlarmActive.visibility = if (isRead) View.INVISIBLE else View.VISIBLE
        binding.btnAlarm.visibility = if (isRead) View.VISIBLE else View.INVISIBLE
    }

    @RequiresApi(VERSION_CODES.O)
    private fun dateFormat(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        return date.format(formatter)
    }

    override fun onResume() {
        super.onResume() // 상태바를 메인블루 색상으로 변경
        (activity as? MainActivity)?.setStatusBarColor(R.color.main_blue_1, false)
        fetchHomeData(selectedDate)
    }

    override fun onDestroyView() {
        progressBarAnimator?.cancel() // 애니메이션 중지
        progressBarAnimator = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val DATE_PATTERN = "yyyy년 MM월"
    }
}
