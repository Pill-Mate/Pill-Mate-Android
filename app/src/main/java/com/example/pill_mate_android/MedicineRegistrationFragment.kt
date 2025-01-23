package com.example.pill_mate_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
        setupRecyclerView()
        setupNavigation()
        setupNextButton()
        setupSkipButton()
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
                is StepOneFragment -> if (currentFragment.isValidInput()) {
                    navHostFragment.navController.navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
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
                    navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepFiveFragment)
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
                    currentFragment.saveData() // 데이터 저장
                    showConfirmationBottomSheet { confirmed ->
                        if (confirmed) {
                            navigateToScheduleActivity() // ScheduleActivity로 전환
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
                    navigateToScheduleActivity() // ScheduleActivity로 전환
                }
            }
        }
    }

    private fun navigateToScheduleActivity() {
        val intent = Intent(requireContext(), ScheduleActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // MedicineRegistrationActivity 종료
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