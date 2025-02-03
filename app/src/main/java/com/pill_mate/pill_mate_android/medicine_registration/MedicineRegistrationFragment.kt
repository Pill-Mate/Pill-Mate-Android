package com.pill_mate.pill_mate_android.medicine_registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentMedicineRegistrationBinding
import com.pill_mate.pill_mate_android.search.view.StepOneFragment
import com.pill_mate.pill_mate_android.search.view.StepTwoFragment
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.schedule.ScheduleActivity
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.pill_mate.pill_mate_android.util.KeyboardUtil

class MedicineRegistrationFragment : Fragment(), MedicineRegistrationView {

    private var _binding: FragmentMedicineRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RegistrationDataAdapter
    private lateinit var presenter: MedicineRegistrationPresenter
    private val stepCount = 8 // 총 단계 수

    fun getPresenter(): MedicineRegistrationPresenter {
        return presenter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineRegistrationBinding.inflate(inflater, container, false)
        presenter = MedicineRegistrationPresenter(DataRepository, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeProgressBar()
        setupRecyclerView()
        setupNavigation()
        setupNextButton()
        setupSkipButton()
        setupNavigationListener()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        /*KeyboardUtil.handleKeyboardVisibility(
            binding.root,
            binding.btnNext,
            resources.getDimensionPixelSize(R.dimen.default_btn_margin)
        )*/
    }

    private fun initializeProgressBar() {
        val progressBarLayout = binding.progressBarSteps
        progressBarLayout.removeAllViews()

        repeat(stepCount) { index ->
            val stepView = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                ).apply {
                    if (index != 0) setMargins(4, 0, 0, 0)
                }
                background = ContextCompat.getDrawable(requireContext(),
                    R.drawable.bg_progress_step
                )
            }
            progressBarLayout.addView(stepView)
        }
    }

    private fun updateProgressBar(currentStep: Int) {
        val progressBarLayout = binding.progressBarSteps
        for (i in 0 until stepCount) {
            val stepView = progressBarLayout.getChildAt(i)
            stepView.background = ContextCompat.getDrawable(
                requireContext(),
                if (i < currentStep) R.drawable.bg_progress_step_active
                else R.drawable.bg_progress_step
            )
        }
    }

    private fun setupNavigationListener() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val stepIndex = when (destination.id) {
                R.id.stepOneFragment -> 1
                R.id.stepTwoFragment -> 2
                R.id.stepThreeFragment -> 3
                R.id.stepFourFragment -> 4
                R.id.stepFiveFragment -> 5
                R.id.stepSixFragment -> 6
                R.id.stepSevenFragment -> 7
                R.id.stepEightFragment -> 8
                R.id.loadingConflictFragment, R.id.medicineConflictFragment -> 2
                else -> 0
            }
            updateProgressBar(stepIndex)
            updateViewVisibility(destination.id)
            presenter.updateView(stepIndex)
        }
    }

    private fun setupRecyclerView() {
        adapter = RegistrationDataAdapter(emptyList())
        binding.rvData.layoutManager = LinearLayoutManager(requireContext())
        binding.rvData.adapter = adapter

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3)
        val dividerHeight = 1f
        val marginStart = 24f
        val marginEnd = 24f
        binding.rvData.addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))
    }

    override fun updateRecyclerView(data: List<RegistrationData>) {
        adapter.updateData(data)
        Log.d("MedicineRegistrationFragment", "RecyclerView updated: ${data.size} items")

        // 🔹 데이터가 없으면 RecyclerView 숨김 처리
        binding.rvData.visibility = if (data.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun showConfirmationBottomSheet() {
        val bottomSheetFragment = ConfirmationBottomSheet()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun setupNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })

        binding.ivBack.setOnClickListener { handleBackPressed() }
        binding.ivDelete.setOnClickListener { showPillRegistrationDialog() }
    }

    private fun handleBackPressed() {
        val presenter = getPresenter()
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
        val navController = navHostFragment.navController
        val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

        // StepOneFragment라면 다이얼로그 표시
        if (currentFragment is StepOneFragment) {
            showPillRegistrationDialog()
            return
        }

        // 현재 프래그먼트 위치를 기반으로 Step 계산
        val currentStep = when (navController.currentDestination?.id) {
            R.id.stepTwoFragment -> 2
            R.id.stepThreeFragment -> 3
            R.id.stepFourFragment -> 4
            R.id.stepFiveFragment -> 5
            R.id.stepSixFragment -> 6
            R.id.stepSevenFragment -> 7
            R.id.stepEightFragment -> 8
            else -> null
        }

        // 사용자가 뒤로 갈 때, "바로 직전 Step"의 데이터를 초기화
        currentStep?.let {
            presenter.clearDataForStep(it - 1)
            presenter.updateView(it - 1)  // 뒤로 가기 후 뷰 업데이트
        }

        navController.navigateUp()
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
            val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

            when (currentFragment) {
                is StepOneFragment -> {
                    currentFragment.onNextButtonClicked()
                    if (currentFragment.isValidInput()) {
                        presenter.updateView(2) // 다음 단계에서 업데이트 반영
                        navHostFragment.navController.navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
                    }
                }
                is StepTwoFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(3)
                    navHostFragment.navController.navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
                }
                is StepThreeFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(4)
                    navHostFragment.navController.navigate(R.id.action_stepThreeFragment_to_stepFourFragment)
                }
                is StepFourFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(if (currentFragment.shouldMoveToStepFive()) 5 else 6)
                    if (currentFragment.shouldMoveToStepFive()) {
                        navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepFiveFragment)
                    } else {
                        navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepSixFragment)
                    }
                }
                is StepFiveFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(6)
                    navHostFragment.navController.navigate(R.id.action_stepFiveFragment_to_stepSixFragment)
                }
                is StepSixFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(7)
                    navHostFragment.navController.navigate(R.id.action_stepSixFragment_to_stepSevenFragment)
                }
                is StepSevenFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView(8)
                    navHostFragment.navController.navigate(R.id.action_stepSevenFragment_to_stepEightFragment)
                }
                is StepEightFragment -> if (currentFragment.isValidInput()) {
                    currentFragment.saveData()  // 저장
                    showConfirmationBottomSheet { confirmed ->
                        if (confirmed) {
                            navigateToScheduleActivity()
                        }
                    }
                } else {
                    showInvalidInputToast()
                }
            }
        }
    }

    private fun setupSkipButton() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as? NavHostFragment
        val navController = navHostFragment?.navController

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            binding.btnSkip.visibility = if (destination.id == R.id.stepEightFragment) View.VISIBLE else View.GONE
        }

        binding.btnSkip.setOnClickListener {
            presenter.skipVolumeAndUnitInput() // 새로운 함수 호출

            showConfirmationBottomSheet { confirmed ->
                if (confirmed) {
                    navigateToScheduleActivity()
                }
            }
        }
    }

    private fun navigateToScheduleActivity() {
        val intent = Intent(requireContext(), ScheduleActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun navigateToStepOne() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as? NavHostFragment
        val navController = navHostFragment?.navController

        navController?.navigate(R.id.stepOneFragment)
    }

    fun navigateToStepEight() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as? NavHostFragment
        val navController = navHostFragment?.navController

        navController?.navigate(R.id.stepEightFragment)
    }

    private fun showConfirmationBottomSheet(onConfirmed: (Boolean) -> Unit) {
        val bottomSheetFragment = ConfirmationBottomSheet.newInstance(onConfirmed)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    fun updateNextButtonState(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
    }

    private fun showPillRegistrationDialog() {
        val dialog = RegistrationExitDialogFragment()
        dialog.show(childFragmentManager, "PillRegistrationDialog")
    }

    private fun updateViewVisibility(destinationId: Int) {
        val isConflictFragment = destinationId == R.id.loadingConflictFragment || destinationId == R.id.medicineConflictFragment
        val isStepEightFragment = destinationId == R.id.stepEightFragment

        // 상단 뷰 (뒤로 가기 버튼, 제목, 삭제 버튼) 처리
        binding.ivBack.visibility = if (isConflictFragment) View.GONE else View.VISIBLE
        binding.tvTitle.visibility = if (isConflictFragment) View.GONE else View.VISIBLE
        binding.ivDelete.visibility = if (isConflictFragment) View.GONE else View.VISIBLE

        // ProgressBar, Next 버튼, RecyclerView 처리
        binding.progressBarSteps.visibility = if (isConflictFragment) View.GONE else View.VISIBLE
        binding.btnNext.visibility = if (isConflictFragment) View.GONE else View.VISIBLE

        // 충돌 프래그먼트일 경우 RecyclerView 숨김 및 데이터 초기화
        if (isConflictFragment) {
            adapter.updateData(emptyList()) // 어댑터 데이터 초기화
            binding.rvData.visibility = View.GONE

            // RecyclerView 숨김이 확실하게 적용되도록 postDelayed 사용
            binding.rvData.postDelayed({
                binding.rvData.visibility = View.GONE
            }, 50) // 50ms 후 강제 적용
        } else {
            binding.rvData.visibility = View.VISIBLE
        }

        // StepEightFragment에서만 건너뛰기 버튼 표시
        binding.btnSkip.visibility = if (isStepEightFragment) View.VISIBLE else View.GONE
    }

    fun showInvalidInputToast() {
        Toast.makeText(requireContext(), "모든 입력값을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}