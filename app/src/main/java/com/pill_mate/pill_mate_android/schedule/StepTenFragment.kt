package com.pill_mate.pill_mate_android.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationActivity
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepTenBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository

class StepTenFragment : Fragment() {

    private var _binding: FragmentStepTenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnYes.setOnClickListener {
            DataRepository.clearAll()
            navigateToMedicineRegistrationActivity()
        }

        binding.btnNo.setOnClickListener {
            DataRepository.clearAll()
            findNavController().navigate(R.id.action_stepTenFragment_to_stepElevenFragment)
        }
    }

    private fun navigateToMedicineRegistrationActivity() {
        val intent = android.content.Intent(requireContext(), MedicineRegistrationActivity::class.java)
        intent.putExtra("destination", "step1")
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}