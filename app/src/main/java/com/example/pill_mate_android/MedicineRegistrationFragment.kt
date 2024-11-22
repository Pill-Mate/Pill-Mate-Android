package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.pill_mate_android.databinding.FragmentMedicineRegistrationBinding

class MedicineRegistrationFragment : Fragment() {

    private var _binding: FragmentMedicineRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMedicineRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupNextButton()
    }

    private fun setupNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
                val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

                if (currentFragment is StepOneFragment) {
                    showPillRegistrationDialog()
                } else {
                    navHostFragment.navController.navigateUp()
                }
            }
        })

        binding.ivBack.setOnClickListener {
            val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_steps) as NavHostFragment
            val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

            if (currentFragment is StepOneFragment) {
                showPillRegistrationDialog()
            } else {
                navHostFragment.navController.navigateUp()
            }
        }

        binding.ivDelete.setOnClickListener {
            showPillRegistrationDialog()
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
                // Add other fragments as needed
            }
        }
    }

    fun updateNextButtonState(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
        binding.btnNext.setBackgroundResource(
            if (isEnabled) R.drawable.bg_btn_main_blue_1 else R.drawable.bg_btn_gray_3
        )
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