package com.example.pill_mate_android.ui.pillcheck.fragment

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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.FragmentPillCheckBinding
import com.example.pill_mate_android.medicine_registration.MedicineRegistrationActivity
import com.example.pill_mate_android.ui.main.activity.MainActivity
import com.example.pill_mate_android.ui.pillcheck.CalendarVPAdapter
import com.example.pill_mate_android.ui.pillcheck.GroupedMedicine
import com.example.pill_mate_android.ui.pillcheck.HomeData
import com.example.pill_mate_android.ui.pillcheck.IDateClickListener
import com.example.pill_mate_android.ui.pillcheck.IntakeCountAdapter
import com.example.pill_mate_android.ui.pillcheck.MedicineCheckData
import com.example.pill_mate_android.ui.pillcheck.ResponseHome
import com.example.pill_mate_android.ui.pillcheck.ResponseWeeklyCalendar
import com.example.pill_mate_android.ui.pillcheck.TimeGroup
import com.example.pill_mate_android.ui.setting.activity.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class PillCheckFragment : Fragment(), IDateClickListener {

    private var _binding: FragmentPillCheckBinding? = null
    private val binding get() = _binding!!
    private val expandedStates = mutableSetOf<Int>()

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

    private fun setMainButtonClickListener() {
        binding.btnSetting.setOnClickListener { // 공지, 마이페이지로 이동
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        binding.btnAlarm.setOnClickListener {
            Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_LONG).show()
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

    @RequiresApi(VERSION_CODES.O)
    private fun moveToTodayPage() { // 오늘 날짜가 포함된 페이지로 이동
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
    @RequiresApi(VERSION_CODES.O)
    private fun fetchHomeData(selectedDate: LocalDate) {
        val homeData = HomeData(date = selectedDate.toString()) // 클릭한 날짜를 사용
        val call: Call<ResponseHome> = ServiceCreator.homeService.getHomeData(homeData)

        call.enqueue(object : Callback<ResponseHome> {
            override fun onResponse(
                call: Call<ResponseHome>, response: Response<ResponseHome>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let { // ViewPager2에 현재 표시된 WeeklyCalendarFragment에 데이터 전달
                        val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
                        val currentFragment = adapter?.fragments?.get(binding.vpCalendar.currentItem)

                        currentFragment?.updateWeeklyIcons(it) // 데이터 전달

                        // 데이터를 그룹화하여 RecyclerView에 설정
                        setupIntakeCountRecyclerView(it)

                        if (it.medicineList.isNullOrEmpty()) {
                            Log.i("데이터 전송 성공", "불러올 리스트가 없습니다.")
                            with(binding) {
                                layoutNone.visibility = View.VISIBLE
                                layoutProgressbar.visibility = View.INVISIBLE
                                intakeCountRecyclerView.visibility = View.INVISIBLE
                            }
                        } else {
                            with(binding) {
                                layoutNone.visibility = View.INVISIBLE
                                layoutProgressbar.visibility = View.VISIBLE
                                intakeCountRecyclerView.visibility = View.VISIBLE
                                tvNum.text = it.countAll.toString()
                                if (it.countLeft == 0) {
                                    tvRemain.text = "복약 완료"
                                } else {
                                    tvRemain.text = "${it.countLeft}회 남음"
                                }
                            }
                            setupProgressBar(it.countAll, it.countLeft)
                        }
                    } ?: Log.e("데이터 전송 실패", "데이터 전송 실패")
                }
            }

            override fun onFailure(call: Call<ResponseHome>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    //주간 달력 스크롤 시 데이터 받아오기
    @RequiresApi(VERSION_CODES.O)
    private fun fetchWeeklyCalendarData(selectedDate: LocalDate) {
        val homeData = HomeData(date = selectedDate.toString()) // 클릭한 날짜를 사용
        val call: Call<ResponseWeeklyCalendar> = ServiceCreator.weeklyCalendarService.getWeeklyCalendarData(homeData)

        call.enqueue(object : Callback<ResponseWeeklyCalendar> {
            override fun onResponse(
                call: Call<ResponseWeeklyCalendar>, response: Response<ResponseWeeklyCalendar>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        val adapter = binding.vpCalendar.adapter as? CalendarVPAdapter
                        val currentFragment = adapter?.fragments?.get(binding.vpCalendar.currentItem)

                        currentFragment?.updateWeeklyIcons(it) // 데이터 전달

                    } ?: Log.e("데이터 전송 실패", "데이터 전송 실패")
                }
            }

            override fun onFailure(call: Call<ResponseWeeklyCalendar>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    //체크박스 체크여부 데이터 보내기
    @RequiresApi(VERSION_CODES.O)
    private fun patchMedicineCheckData(medicineScheduleId: Long, eatCheck: Boolean) { // 로컬 상태 업데이트

        val checkData = listOf(MedicineCheckData(medicineScheduleId, eatCheck))

        val call: Call<ResponseHome> = ServiceCreator.medicineCheckService.patchCheckData(checkData)

        call.enqueue(object : Callback<ResponseHome> {
            override fun onResponse(call: Call<ResponseHome>, response: Response<ResponseHome>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let { // ViewPager2에 현재 표시된 WeeklyCalendarFragment에 데이터 전달
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
                            setupProgressBar(it.countAll, it.countLeft)
                        }
                    } ?: Log.e("데이터 전송 실패", "데이터 전송에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<ResponseHome>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    @RequiresApi(VERSION_CODES.O)
    private fun setupIntakeCountRecyclerView(responseHome: ResponseHome) { // 데이터 그룹화
        val groupedMedicines = groupMedicines(responseHome)

        // intakeCount RecyclerView 설정
        if (binding.intakeCountRecyclerView.adapter == null) {
            val intakeCountAdapter =
                IntakeCountAdapter(groupedMedicines, expandedStates) { medicineScheduleId, eatCheck ->
                    patchMedicineCheckData(medicineScheduleId, eatCheck)
                }
            binding.intakeCountRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = intakeCountAdapter
            }
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
    private fun setupProgressBar(countAll: Int, countLeft: Int) {
        binding.pbNumberOfMedications.max = countAll
        binding.pbNumberOfMedications.progress = countAll - countLeft
    }

    @RequiresApi(VERSION_CODES.O)
    private fun dateFormat(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        return date.format(formatter)
    }

    companion object {
        const val DATE_PATTERN = "yyyy년 MM월"
    }

    override fun onResume() {
        super.onResume() // 상태바를 메인블루 색상으로 변경
        (activity as? MainActivity)?.setStatusBarColor(R.color.main_blue_1, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}