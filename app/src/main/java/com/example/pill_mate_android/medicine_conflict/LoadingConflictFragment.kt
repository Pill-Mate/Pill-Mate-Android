package com.example.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentLoadingConflictBinding
import com.example.pill_mate_android.medicine_registration.model.EfcyDplctResponse
import com.example.pill_mate_android.medicine_registration.model.UsjntTabooResponse

class LoadingConflictFragment : Fragment() {
    private var _binding: FragmentLoadingConflictBinding? = null
    private val binding get() = _binding!!

    private var usjntTabooData: ArrayList<UsjntTabooResponse>? = null
    private var efcyDplctData: ArrayList<EfcyDplctResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingConflictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.lottieConflict.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }*/

        usjntTabooData = arguments?.getParcelableArrayList("usjntTabooData")
        efcyDplctData = arguments?.getParcelableArrayList("efcyDplctData")

        setupButton()
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            try {
                Log.d("LoadingConflictFragment", "Navigating to MedicineConflictFragment")
                findNavController().navigate(
                    R.id.action_loadingConflictFragment_to_medicineConflictFragment,
                    bundleOf(
                        "usjntTabooData" to usjntTabooData,
                        "efcyDplctData" to efcyDplctData
                    )
                )
            } catch (e: Exception) {
                Log.e("LoadingConflictFragment", "Error navigating to MedicineConflictFragment", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding.lottieConflict.cancelAnimation()
        _binding = null
    }
}