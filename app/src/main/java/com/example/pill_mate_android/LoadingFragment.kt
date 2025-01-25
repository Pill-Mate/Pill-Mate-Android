package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.pill_mate_android.databinding.FragmentLoadingBinding
import com.example.pill_mate_android.pillSearch.api.ServerApiClient
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

        binding.lottieLoading.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }

        checkMedicineConflicts()
    }

    private fun checkMedicineConflicts() {
        val identifyNumber = arguments?.getString("identifyNumber") ?: return

        val serverApiService = ServerApiClient.serverApiService
        serverApiService.getMedicineConflicts(identifyNumber).enqueue(object : Callback<ConflictResponse> {
            override fun onResponse(call: Call<ConflictResponse>, response: Response<ConflictResponse>) {
                if (response.isSuccessful && response.body()?.conflicts?.isNotEmpty() == true) {
                    navigateToLoadingConflictFragment(identifyNumber)
                } else {
                    navigateToStepNineFragment()
                }
            }

            override fun onFailure(call: Call<ConflictResponse>, t: Throwable) {
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
            navOptions {
                popUpTo(R.id.loadingFragment) { inclusive = true }
            }
        )
    }

    private fun navigateToStepNineFragment() {
        findNavController().navigate(
            R.id.action_loadingFragment_to_stepNineFragment,
            null,
            navOptions {
                popUpTo(R.id.loadingFragment) { inclusive = true }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieLoading.cancelAnimation()
        _binding = null
    }
}