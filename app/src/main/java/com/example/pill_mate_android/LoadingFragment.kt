package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.pill_mate_android.databinding.FragmentLoadingBinding
import com.example.pill_mate_android.pillSearch.model.ConflictResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lottie 애니메이션 설정
        binding.lottieLoading.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }

        // 약물 충돌 검사 실행
        checkMedicineConflicts()
    }

    private fun checkMedicineConflicts() {
        val identifyNumber = arguments?.getString("identifyNumber") ?: run {
            Log.e("LoadingFragment", "identifyNumber is missing.")
            navigateToStepNineFragment()
            return
        }

        ServiceCreator.medicineRegistrationService.getMedicineConflicts(identifyNumber)
            .enqueue(object : Callback<ConflictResponse> {
                override fun onResponse(call: Call<ConflictResponse>, response: Response<ConflictResponse>) {
                    if (response.isSuccessful && response.body()?.conflicts?.isNotEmpty() == true) {
                        navigateToLoadingConflictFragment(identifyNumber)
                    } else {
                        navigateToStepNineFragment()
                    }
                }

                override fun onFailure(call: Call<ConflictResponse>, t: Throwable) {
                    Log.e("LoadingFragment", "Network error: ${t.message}")
                    navigateToStepNineFragment()
                }
            })
    }

    private fun navigateToLoadingConflictFragment(identifyNumber: String) {
        val bundle = Bundle().apply {
            putString("identifyNumber", identifyNumber)
        }
        findNavController().navigate(
            R.id.action_loadingFragment_to_loadingConflictFragment,
            bundle,
            getNavOptions()
        )
    }

    private fun navigateToStepNineFragment() {
        findNavController().navigate(
            R.id.action_loadingFragment_to_stepNineFragment,
            null,
            getNavOptions()
        )
    }

    private fun getNavOptions() = navOptions {
        popUpTo(R.id.loadingFragment) { inclusive = true }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieLoading.cancelAnimation()
        _binding = null
    }
}