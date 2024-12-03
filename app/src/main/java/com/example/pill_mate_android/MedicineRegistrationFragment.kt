package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.databinding.FragmentMedicineRegistrationBinding
import com.example.pill_mate_android.pillSearch.view.RegistrationData
import com.example.pill_mate_android.pillSearch.view.RegistrationDataAdapter

class MedicineRegistrationFragment : Fragment() {

    private var _binding: FragmentMedicineRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RegistrationDataAdapter
    private val dataList = mutableListOf<RegistrationData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupNavigation()
        setupNextButton()
    }

    private fun setupRecyclerView() {
        adapter = RegistrationDataAdapter(dataList)
        binding.rvData.layoutManager = LinearLayoutManager(requireContext())
        binding.rvData.adapter = adapter
    }

    fun updateRecyclerViewData(label: String, data: String) {
        val existingItem = dataList.find { it.label == label }
        if (existingItem != null) {
            existingItem.data = data
        } else {
            dataList.add(RegistrationData(label, data))
        }
        adapter.notifyDataSetChanged()

        // "다음" 버튼 상태 업데이트
        updateNextButtonState(isValidInput())
    }

    private fun isValidInput(): Boolean {
        // StepOneFragment, StepTwoFragment, StepThreeFragment 등에서 필요한 데이터 유효성 검증
        val hasDayData = dataList.any { it.label == "요일" && it.data.isNotEmpty() }
        val hasTimeData = dataList.any { it.label == "복용 시간대" && it.data.isNotEmpty() }
        return hasDayData && hasTimeData
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
                    if (currentFragment.isValidInput()) {
                        navHostFragment.navController.navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
                    } else {
                        currentFragment.showWarning(true)
                    }
                }
                is StepTwoFragment -> {
                    if (currentFragment.isValidInput()) {
                        navHostFragment.navController.navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
                    }
                }
                is StepThreeFragment -> {
                    if (currentFragment.isValidInput()) {
                        navHostFragment.navController.navigate(R.id.action_stepThreeFragment_to_stepFourFragment)
                    }
                }
            }
        }

/*        // 초기 상태
        updateNextButtonState(false)*/
    }

    fun updateNextButtonState(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
    }

    private fun showPillRegistrationDialog() {
        val dialog = PillRegistrationDialogFragment()
        dialog.show(childFragmentManager, "PillRegistrationDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}