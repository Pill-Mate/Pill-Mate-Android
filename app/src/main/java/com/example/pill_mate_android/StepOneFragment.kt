package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.pill_mate_android.databinding.FragmentStepOneBinding
import com.example.pill_mate_android.pillSearch.view.SearchPharmacyBottomSheetFragment

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

        // FragmentResultListener 설정
        setFragmentResultListener("requestKey") { _, bundle ->
            val selectedPharmacy = bundle.getString("pharmacy")
            binding.etPharmacy.setText(selectedPharmacy)
        }

        // EditText에 포커스가 가면 바텀 시트를 띄움
        binding.etPharmacy.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val bottomSheetFragment = SearchPharmacyBottomSheetFragment()
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        }

        // 다음 버튼 클릭 시 이동
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // 리스너 해제
        parentFragmentManager.clearFragmentResultListener("requestKey")
    }
}