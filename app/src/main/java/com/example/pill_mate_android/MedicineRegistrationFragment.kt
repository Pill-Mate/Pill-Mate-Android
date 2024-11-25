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
        android.util.Log.d("MedicineRegistrationFragment", "Update RecyclerView - Label: $label, Data: $data")

        val existingItem = dataList.find { it.label == label }
        if (existingItem != null) {
            existingItem.data = data
        } else {
            dataList.add(RegistrationData(label, data))
        }

        // 리사이클러뷰 가시성 변경
        binding.rvData.visibility = View.VISIBLE

        // 어댑터에 데이터 변경 알림
        adapter.notifyDataSetChanged()
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