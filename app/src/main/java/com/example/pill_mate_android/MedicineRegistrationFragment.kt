package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.databinding.FragmentMedicineRegistrationBinding
import com.example.pill_mate_android.pillSearch.util.CustomDividerItemDecoration
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

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3) // 회색
        val dividerHeight = 1f // 1dp
        val marginStart = 24f
        val marginEnd = 24f
        binding.rvData.addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))
    }

    fun updateRecyclerViewData(label: String, data: String) {
        val existingItem = dataList.find { it.label == label }
        if (existingItem != null) {
            existingItem.data = data
        } else {
            dataList.add(RegistrationData(label, data))
        }

        adapter.updateData(dataList)
        if (dataList.isNotEmpty()) {
            binding.rvData.visibility = View.VISIBLE
        } else {
            binding.rvData.visibility = View.GONE
        }
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
                is StepFourFragment -> {
                    if (currentFragment.isValidInput()) {
                        navHostFragment.navController.navigate(R.id.action_stepFourFragment_to_stepFiveFragment)
                    }
                }
            }
        }
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