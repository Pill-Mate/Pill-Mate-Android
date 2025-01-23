package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.pill_mate_android.databinding.FragmentStepNineBinding
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.model.Schedule
import com.example.pill_mate_android.pillSearch.util.VerticalSpaceItemDecoration
import com.example.pill_mate_android.pillSearch.view.ScheduleAdapter

class StepNineFragment : Fragment(), AlarmSwitchDialogFragment.AlarmSwitchDialogListener {

    private var _binding: FragmentStepNineBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepNineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadMedicineData()
        loadScheduleData()
    }

    private fun setupUI() {
        // 스케줄 RecyclerView 설정
        scheduleAdapter = ScheduleAdapter(emptyList())
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = scheduleAdapter

        // RecyclerView 아이템 간 간격 추가
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.schedule_item_spacing) // 12dp
        binding.rvSchedule.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))

        // 스위치 상태 변경 처리 (기본값은 켜진 상태)
        binding.switchAlarm.isChecked = true // 기본값 설정

        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                showAlarmSwitchDialog()
            } else {
                updateAlarmState(true)
            }
        }

        // 뒤로가기 버튼 클릭 처리
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed() // ScheduleActivity의 onBackPressed 호출
        }

        // 등록 버튼 클릭 처리
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_stepNineFragment_to_stepTenFragment)
        }
    }

    private fun showAlarmSwitchDialog() {
        val dialogFragment = AlarmSwitchDialogFragment()
        dialogFragment.setAlarmSwitchDialogListener(this)
        dialogFragment.show(childFragmentManager, "AlarmSwitchDialog")
    }

    override fun onDialogPositiveClick() {
        binding.switchAlarm.isChecked = true
        updateAlarmState(false)
    }

    override fun onDialogNegativeClick() {
        updateAlarmState(true)
    }

    private fun loadMedicineData() {
        // 약물 데이터를 불러와 UI에 표시
        val medicine = DataRepository.getMedicine()
        val schedule = DataRepository.getSchedule()

        if (medicine != null && schedule != null) {
            binding.tvMedicineName.text = medicine.medicine_name
            binding.tvMedicineDose.text = "${schedule.eat_count}${schedule.eat_unit}"
            binding.ivMedicineImage.load(medicine.image) {
                error(R.drawable.ic_default_pill) // 이미지 로드 실패 시 기본 이미지 표시
            }
        } else {
            // 약물 또는 스케줄 데이터가 없는 경우 처리
            binding.tvMedicineName.text = "약물 없음"
            binding.tvMedicineDose.text = ""
            binding.ivMedicineImage.setImageResource(R.drawable.ic_default_pill)
        }
    }

    private fun loadScheduleData() {
        // 스케줄 데이터를 불러와 RecyclerView에 표시
        val schedule = DataRepository.getSchedule()
        if (schedule != null) {
            val scheduleList = createScheduleList(schedule)
            scheduleAdapter.updateScheduleList(scheduleList)
        }
    }

    private fun createScheduleList(schedule: Schedule): List<ScheduleAdapter.ScheduleItem> {
        // 스케줄 데이터를 변환하여 리스트 형태로 반환
        val result = mutableListOf<ScheduleAdapter.ScheduleItem>()

        val timeGroups = schedule.intake_count.split(",")
        for (group in timeGroups) {
            val time = "오전 8:00" // 온보딩 데이터 가져와서 설정해야함
            val mealTime = if (schedule.meal_unit.isNotEmpty()) "${schedule.meal_unit} ${schedule.meal_time}분" else null

            // 시간대에 따라 아이콘 설정
            val iconRes = when (group.trim()) {
                "아침" -> R.drawable.ic_breakfast
                "점심" -> R.drawable.ic_lunch
                "저녁" -> R.drawable.ic_dinner
                "공복" -> R.drawable.ic_emptystomach
                "취침전" -> R.drawable.ic_bedtime
                else -> R.drawable.ic_breakfast // 기본 아이콘
            }

            result.add(ScheduleAdapter.ScheduleItem(
                iconRes = iconRes,
                label = group.trim(),
                time = time,
                mealTime = mealTime
            ))
        }

        return result
    }

    private fun updateAlarmState(isEnabled: Boolean) {
        // 알람 상태 변경 처리
        val schedule = DataRepository.getSchedule()
        if (schedule != null) {
            val updatedSchedule = schedule.copy(is_alarm = isEnabled)
            DataRepository.saveSchedule(updatedSchedule)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}