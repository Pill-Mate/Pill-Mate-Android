package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.pill_mate_android.databinding.FragmentLoadingConflictBinding

class LoadingConflictFragment : Fragment() {

    private var _binding: FragmentLoadingConflictBinding? = null
    private val binding get() = _binding!!

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

        binding.lottieConflict.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }

        setupButton()
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            val identifyNumber = arguments?.getString("identifyNumber")
            val bundle = Bundle().apply {
                putString("identifyNumber", identifyNumber)
            }
            findNavController().navigate(
                R.id.action_loadingConflictFragment_to_medicineConflictFragment,
                bundle,
                navOptions {
                    popUpTo(R.id.loadingConflictFragment) { inclusive = true }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieConflict.cancelAnimation()
        _binding = null
    }
}