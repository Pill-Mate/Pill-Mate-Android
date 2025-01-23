package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pill_mate_android.databinding.FragmentStepTenBinding

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
            // Navigate to MedicineRegistrationActivity's Step 1
        }

        binding.btnNo.setOnClickListener {
            // Navigate to Step 11
            findNavController().navigate(R.id.action_stepTenFragment_to_stepElevenFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}