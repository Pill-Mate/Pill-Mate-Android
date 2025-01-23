package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.pill_mate_android.databinding.FragmentStepNineBinding
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.model.Schedule
import com.example.pill_mate_android.pillSearch.view.ScheduleAdapter

class StepNineFragment : Fragment() {

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
        // Set up RecyclerView for schedules
        scheduleAdapter = ScheduleAdapter(emptyList())
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = scheduleAdapter

        // Handle switch state changes
        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            updateAlarmState(isChecked)
        }

        // Handle back button click
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Handle register button click
        binding.btnRegister.setOnClickListener {
            // Implement the action for the registration button
        }
    }

    private fun loadMedicineData() {
        val medicine = DataRepository.getMedicine()
        if (medicine != null) {
            binding.tvMedicineName.text = medicine.medicine_name
            binding.tvMedicineDose.text = "2정" // Example: You can dynamically set this value
            binding.ivMedicineImage.load(medicine.image) {
                //placeholder(R.drawable.ic_default_pill)
                error(R.drawable.ic_default_pill)
            }
        } else {
            // Handle empty medicine data
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
            val time = "오전 8:00" // Replace with dynamic logic
            val mealTime = if (schedule.meal_unit.isNotEmpty()) "${schedule.meal_unit} ${schedule.meal_time}분" else null
            result.add(ScheduleAdapter.ScheduleItem(
                iconRes = R.drawable.ic_breakfast, // Replace with the correct drawable
                label = group.trim(),
                time = time,
                mealTime = mealTime
            ))
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