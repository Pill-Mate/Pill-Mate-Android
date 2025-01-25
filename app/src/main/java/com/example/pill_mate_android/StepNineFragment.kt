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
import com.example.pill_mate_android.pillSearch.api.ServerApiClient
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.model.DataRepository.createMedicineRegisterRequest
import com.example.pill_mate_android.pillSearch.model.MedicineRegisterRequest
import com.example.pill_mate_android.pillSearch.model.Schedule
import com.example.pill_mate_android.pillSearch.util.VerticalSpaceItemDecoration
import com.example.pill_mate_android.pillSearch.view.ScheduleAdapter
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // 스위치 상태 변경 처리
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
            requireActivity().onBackPressed()
        }

        // 등록 버튼 클릭 처리
        binding.btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        val request = createMedicineRegisterRequest()
        if (request != null) {
            sendRegisterRequest(request)
        } else {
            showErrorMessage("등록에 필요한 데이터가 부족합니다.")
        }
    }

    private fun sendRegisterRequest(request: MedicineRegisterRequest) {
        val serverApiService = ServerApiClient.serverApiService
        serverApiService.registerMedicine(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    navigateToNextStep()
                } else {
                    showErrorMessage("등록에 실패했습니다. 다시 시도해주세요.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorMessage("네트워크 오류가 발생했습니다.")
            }
        })
    }

    private fun navigateToNextStep() {
        findNavController().navigate(R.id.action_stepNineFragment_to_stepTenFragment)
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
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
        val medicine = DataRepository.getMedicine()
        val schedule = DataRepository.getSchedule()

        if (medicine != null && schedule != null) {
            binding.tvMedicineName.text = medicine.medicine_name
            binding.tvMedicineDose.text = "${schedule.eat_count}${schedule.eat_unit}"
            binding.ivMedicineImage.load(medicine.image) {
                error(R.drawable.ic_default_pill)
            }
        } else {
            binding.tvMedicineName.text = "약물 없음"
            binding.tvMedicineDose.text = ""
            binding.ivMedicineImage.setImageResource(R.drawable.ic_default_pill)
        }
    }

    private fun loadScheduleData() {
        val schedule = DataRepository.getSchedule()
        if (schedule != null) {
            val scheduleList = createScheduleList(schedule)
            scheduleAdapter.updateScheduleList(scheduleList)
        }
    }

    private fun createScheduleList(schedule: Schedule): List<ScheduleAdapter.ScheduleItem> {
        val result = mutableListOf<ScheduleAdapter.ScheduleItem>()
        val timeGroups = schedule.intake_count.split(",")
        for (group in timeGroups) {
            val time = "오전 8:00" // 서버에서 가져와야해
            val mealTime = if (schedule.meal_unit.isNotEmpty()) "${schedule.meal_unit} ${schedule.meal_time}분" else null
            val iconRes = when (group.trim()) {
                "아침" -> R.drawable.ic_breakfast
                "점심" -> R.drawable.ic_lunch
                "저녁" -> R.drawable.ic_dinner
                "공복" -> R.drawable.ic_emptystomach
                "취침전" -> R.drawable.ic_bedtime
                else -> R.drawable.ic_breakfast
            }

            result.add(ScheduleAdapter.ScheduleItem(iconRes, group.trim(), time, mealTime))
        }
        return result
    }

    private fun updateAlarmState(isEnabled: Boolean) {
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