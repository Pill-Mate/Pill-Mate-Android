package com.pill_mate.pill_mate_android.medicine_registration

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetConfirmBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Schedule
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.pill_mate.pill_mate_android.util.DateConversionUtil

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

        binding.apply {
            if (medicine == null) {
                // 데이터가 없으면 이미지와 텍스트 컨테이너를 통째로 숨김
                ivPillImage.isVisible = false
                layoutPillInfo.isVisible = false
            } else {
                // 데이터가 있으면 보임 처리
                ivPillImage.isVisible = true
                layoutPillInfo.isVisible = true

                // 텍스트 세팅
                tvPillName.text = medicine.medicine_name

                // 분류명 (데이터 있으면 보이고, 없으면 숨김)
                tvPillClass.isVisible = !medicine.classname.isNullOrBlank()
                tvPillClass.text = medicine.classname

                // 회사명 (데이터 있으면 보이고, 없으면 숨김)
                tvPillEntp.isVisible = !medicine.entp_name.isNullOrBlank()
                tvPillEntp.text = medicine.entp_name

                // 이미지 로드
                if (!medicine.image.isNullOrEmpty()) {
                    Glide.with(ivPillImage.context)
                        .load(medicine.image)
                        .transform(RoundedCorners(8))
                        .error(R.drawable.img_default)
                        .into(ivPillImage)
                } else {
                    ivPillImage.setImageResource(R.drawable.img_default)
                }
            }
        }
    }

    private fun loadScheduleData() {
        val schedule = DataRepository.getSchedule()
        val registrationDataList = createRegistrationDataList(schedule)
        adapter.updateData(registrationDataList)
    }

    private fun createRegistrationDataList(schedule: Schedule?): List<RegistrationData> {
        if (schedule == null) return emptyList()

        val timeSlot: RegistrationData? =
            if (schedule.meal_unit.isNotBlank() && schedule.meal_time >= 0) {
                val minuteText = if (schedule.meal_time == 0) "즉시" else "${schedule.meal_time}분"
                RegistrationData("시간대", "${schedule.meal_unit} $minuteText")
            } else null

        return listOfNotNull(
            RegistrationData("약물명", schedule.medicine_name).takeIf { schedule.medicine_name.isNotBlank() },
            RegistrationData("요일", schedule.intake_frequency).takeIf { schedule.intake_frequency.isNotBlank() },
            RegistrationData(
                "횟수",
                "${schedule.intake_count.split(",").size}회(${schedule.intake_count})"
            ).takeIf { schedule.intake_count.isNotBlank() },

            timeSlot, // 조건 안 맞으면 null → 리스트에서 제거됨

            RegistrationData("투약량", "${schedule.eat_count}${schedule.eat_unit}")
                .takeIf { schedule.eat_count > 0 && schedule.eat_unit.isNotBlank() },

            RegistrationData("복약기간", calculateIntakePeriod(schedule))
                .takeIf { schedule.start_date.isNotBlank() && schedule.intake_period > 0 },

            RegistrationData("투여용량", "${schedule.medicine_volume}${schedule.medicine_unit}")
                .takeIf { schedule.medicine_volume > 0 && schedule.medicine_unit.isNotBlank() }
        )
    }

    private fun calculateIntakePeriod(schedule: Schedule): String {
        return DateConversionUtil.calculateIntakePeriod(schedule.start_date, schedule.intake_period)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ConfirmationBottomSheet"

        fun newInstance(onConfirmed: (Boolean) -> Unit): ConfirmationBottomSheet {
            return ConfirmationBottomSheet().apply {
                this.onConfirmed = onConfirmed
            }
        }
    }
}