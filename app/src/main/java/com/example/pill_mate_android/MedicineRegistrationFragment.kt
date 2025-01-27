package com.example.pill_mate_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.databinding.FragmentMedicineRegistrationBinding
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.util.CustomDividerItemDecoration
import com.example.pill_mate_android.pillSearch.view.MedicineRegistrationView
import com.example.pill_mate_android.pillSearch.view.RegistrationData
import com.example.pill_mate_android.pillSearch.view.RegistrationDataAdapter

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
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_progress_step)
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
                else -> 0
            }
            updateProgressBar(stepIndex)
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
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

        if (currentFragment is StepOneFragment) {
            showPillRegistrationDialog()
        } else {
            navHostFragment.navController.navigateUp()
        }
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
            val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

            when (currentFragment) {
                is StepOneFragment -> {
                    currentFragment.onNextButtonClicked()
                    if (currentFragment.isValidInput()) {
                        navHostFragment.navController.navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
                    }
                }
                is StepTwoFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    navHostFragment.navController.navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
                }
                is StepThreeFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    navHostFragment.navController.navigate(R.id.action_stepThreeFragment_to_stepFourFragment)
                }
                is StepFourFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    if (currentFragment.shouldMoveToStepFive()) {
                        navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepFiveFragment)
                    } else {
                        navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepSixFragment)
                    }
                }
                is StepFiveFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    navHostFragment.navController.navigate(R.id.action_stepFiveFragment_to_stepSixFragment)
                }
                is StepSixFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    navHostFragment.navController.navigate(R.id.action_stepSixFragment_to_stepSevenFragment)
                }
                is StepSevenFragment -> if (currentFragment.isValidInput()) {
                    presenter.updateSchedule { it }
                    presenter.updateView()
                    navHostFragment.navController.navigate(R.id.action_stepSevenFragment_to_stepEightFragment)
                }
                is StepEightFragment -> if (currentFragment.isValidInput()) {
                    currentFragment.saveData()
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
        val dialog = PillRegistrationDialogFragment()
        dialog.show(childFragmentManager, "PillRegistrationDialog")
    }

    fun showFullscreenFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fullscreen_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.fullscreenContainer.visibility = View.VISIBLE
    }

    fun hideFullscreenFragment() {
        childFragmentManager.popBackStack()
        binding.fullscreenContainer.visibility = View.GONE
    }

    fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showInvalidInputToast() {
        Toast.makeText(requireContext(), "모든 입력값을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}