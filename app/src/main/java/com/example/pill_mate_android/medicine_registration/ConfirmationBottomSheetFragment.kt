package com.example.pill_mate_android.medicine_registration

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.pill_mate_android.databinding.FragmentBottomSheetConfirmBinding
import com.example.pill_mate_android.medicine_registration.model.DataRepository
import com.example.pill_mate_android.medicine_registration.model.Schedule
import com.example.pill_mate_android.schedule.ScheduleActivity
import com.example.pill_mate_android.util.CustomDividerItemDecoration
import com.example.pill_mate_android.util.DateConversionUtil

class ConfirmationBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetConfirmBinding? = null
    private val binding get() = _binding!!
    private var onConfirmed: ((Boolean) -> Unit)? = null
    private lateinit var adapter: ConfirmationDataAdapter

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadMedicineData()
        loadScheduleData()

        binding.btnYes.setOnClickListener {
            navigateToScheduleActivity() // ScheduleActivity로 전환
            onConfirmed?.invoke(true)
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            onConfirmed?.invoke(false)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    private fun setupRecyclerView() {
        adapter = ConfirmationDataAdapter(emptyList())
        binding.rvData.layoutManager = LinearLayoutManager(requireContext())
        binding.rvData.adapter = adapter

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3)
        val dividerHeight = 1f
        val marginStart = 24f
        val marginEnd = 24f
        binding.rvData.addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))

        binding.rvData.visibility = View.VISIBLE
    }

    private fun loadMedicineData() {
        val medicine = DataRepository.getMedicine()
        binding.tvPillName.text = medicine?.medicine_name ?: "약물 이름 없음"
        binding.tvPillClass.text = medicine?.classname ?: "약물 분류 없음"
        binding.tvPillEntp.text = medicine?.entp_name ?: "제약회사 정보 없음"

        medicine?.image?.let { imageUrl ->
            binding.ivPillImage.load(imageUrl) {
                transformations(RoundedCornersTransformation(20f))
                crossfade(true)
                error(R.drawable.ic_default_pill)
            }
        } ?: run {
            binding.ivPillImage.setImageResource(R.drawable.ic_default_pill)
        }
    }

    private fun loadScheduleData() {
        val schedule = DataRepository.getSchedule()
        val registrationDataList = createRegistrationDataList(schedule)
        adapter.updateData(registrationDataList)
    }

    private fun createRegistrationDataList(schedule: Schedule?): List<RegistrationData> {
        if (schedule == null) return emptyList()

        return listOfNotNull(
            RegistrationData("약물명", schedule.medicine_name).takeIf { schedule.medicine_name.isNotEmpty() },
            RegistrationData("요일", schedule.intake_frequency).takeIf { schedule.intake_frequency.isNotEmpty() },
            RegistrationData("횟수", "${schedule.intake_count.split(",").size}회(${schedule.intake_count})").takeIf { schedule.intake_count.isNotEmpty() },
            RegistrationData("시간대", "${schedule.meal_unit} ${schedule.meal_time}분").takeIf { schedule.meal_unit.isNotEmpty() && schedule.meal_time > 0 },
            RegistrationData("투약량", "${schedule.eat_count}${schedule.eat_unit}").takeIf { schedule.eat_count > 0 && schedule.eat_unit.isNotEmpty() },
            RegistrationData("복약기간", calculateIntakePeriod(schedule)).takeIf { schedule.start_date.isNotEmpty() && schedule.intake_period > 0 },
            RegistrationData("투여용량", "${schedule.medicine_volume}${schedule.medicine_unit}").takeIf { schedule.medicine_volume > 0 && schedule.medicine_unit.isNotEmpty() }
        )
    }

    private fun calculateIntakePeriod(schedule: Schedule): String {
        return DateConversionUtil.calculateIntakePeriod(schedule.start_date, schedule.intake_period)
    }

    private fun navigateToScheduleActivity() {
        val intent = Intent(requireContext(), ScheduleActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // MedicineRegistrationActivity 종료
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onConfirmed: (Boolean) -> Unit): ConfirmationBottomSheet {
            return ConfirmationBottomSheet().apply {
                this.onConfirmed = onConfirmed
            }
        }
    }
}