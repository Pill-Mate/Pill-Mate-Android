package com.example.pill_mate_android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.pill_mate_android.databinding.FragmentLoadingBinding

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

        /*binding.lottieLoading.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }*/

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(
                R.id.action_loadingFragment_to_stepNineFragment,
                null,
                navOptions {
                    popUpTo(R.id.loadingFragment) { inclusive = true } // 백 스택에서 제거
                }
            )
        }, 3000) // 3초 후 이동
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding.lottieLoading.cancelAnimation()
        _binding = null
    }
}