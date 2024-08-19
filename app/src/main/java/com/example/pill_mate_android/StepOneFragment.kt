package com.example.pill_mate_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pill_mate_android.databinding.FragmentStepOneBinding
import androidx.navigation.fragment.findNavController

class StepOneFragment : Fragment() {

    private var _binding: FragmentStepOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPharmacy.setOnClickListener {
            findNavController().navigate(R.id.action_stepOneFragment_to_searchPharmacyFragment)
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}