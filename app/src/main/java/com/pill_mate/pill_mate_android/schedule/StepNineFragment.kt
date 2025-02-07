package com.pill_mate.pill_mate_android.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.pill_mate.pill_mate_android.databinding.FragmentStepNineBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.MedicineRegisterRequest
import com.pill_mate.pill_mate_android.medicine_registration.model.OnboardingTimeResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.Schedule
import com.pill_mate.pill_mate_android.util.VerticalSpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StepNineFragment : Fragment(), AlarmSwitchDialogFragment.AlarmSwitchDialogListener {

    private var _binding: FragmentStepNineBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleAdapter: ScheduleAdapter
    private val timeMap = mutableMapOf<String, String>() // 서버에서 받은 시간 저장

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
        fetchOnboardingTimes() // 서버에서 시간 데이터 가져오기
        loadMedicineData()
        loadScheduleData()
    }

    private fun setupUI() {
        // RecyclerView 설정
        scheduleAdapter = ScheduleAdapter(emptyList(), timeMap)
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = scheduleAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.schedule_item_spacing)
        binding.rvSchedule.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))

        // 알람 스위치 상태 변경
        binding.switchAlarm.isChecked = true
        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) showAlarmSwitchDialog() else updateAlarmState(true)
        }

        // 뒤로가기 버튼 처리
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }

        // 등록 버튼 처리
        binding.btnRegister.setOnClickListener { handleRegister() }
    }

    private fun fetchOnboardingTimes() {
        medicineRegistrationService.getMedicineOnboardingTimes().enqueue(object : Callback<OnboardingTimeResponse> {
            override fun onResponse(call: Call<OnboardingTimeResponse>, response: Response<OnboardingTimeResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val times = response.body()?.result
                    times?.let {
                        timeMap[getString(R.string.time_morning)] = formatTime(it.morningTime)
                        timeMap[getString(R.string.time_lunch)] = formatTime(it.lunchTime)
                        timeMap[getString(R.string.time_dinner)] = formatTime(it.dinnerTime)
                        timeMap[getString(R.string.time_empty)] = formatTime(it.wakeupTime)
                        timeMap[getString(R.string.time_before_sleep)] = formatTime(it.bedTime)

                        loadScheduleData()
                    }
                } else {
                    Log.e("OnboardingTimes", "Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<OnboardingTimeResponse>, t: Throwable) {
                Log.e("OnboardingTimes", "Network error: ${t.message}")
            }
        })
    }

    private fun formatTime(time: String): String {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val period = if (hour < 12) getString(R.string.am) else getString(R.string.pm)
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return "$period $formattedHour:${if (minute < 10) "0$minute" else minute}"
    }

    private fun handleRegister() {
        val request = DataRepository.createMedicineRegisterRequest()
        if (request != null) {
            Log.d("MedicineRegisterRequest", Gson().toJson(request)) // JSON 데이터 확인
            sendRegisterRequest(request)
        } else {
            showErrorMessage(getString(R.string.nine_registration_data_insufficient))
        }
    }

    private fun sendRegisterRequest(request: MedicineRegisterRequest) {
        medicineRegistrationService.registerMedicine(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    navigateToNextStep()
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("RegisterMedicine", "Error: $errorMessage")
                    showErrorMessage(getString(R.string.nine_registration_failed))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorMessage(getString(R.string.nine_network_error))
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
            Glide.with(binding.ivMedicineImage.context)
                .load(medicine.image)
                .transform(RoundedCorners(8))
                .error(R.drawable.ic_default_pill)
                .into(binding.ivMedicineImage)
        } else {
            binding.tvMedicineName.text = getString(R.string.nine_no_medicine)
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
            val iconRes = when (group.trim()) {
                getString(R.string.time_morning) -> R.drawable.ic_breakfast
                getString(R.string.time_lunch) -> R.drawable.ic_lunch
                getString(R.string.time_dinner) -> R.drawable.ic_dinner
                getString(R.string.time_empty) -> R.drawable.ic_emptystomach
                getString(R.string.time_before_sleep) -> R.drawable.ic_bedtime
                else -> R.drawable.ic_breakfast
            }

            result.add(
                ScheduleAdapter.ScheduleItem(
                    iconRes = iconRes,
                    label = group.trim(),
                    time = timeMap[group.trim()] ?: getString(R.string.nine_time_not_registered),
                    mealTime = if (schedule.meal_unit.isNotEmpty()) "${schedule.meal_unit} ${schedule.meal_time}분" else null
                )
            )
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