package com.pill_mate.pill_mate_android.schedule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.databinding.FragmentStepElevenBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.util.loadNativeAd

class StepElevenFragment : Fragment() {

    private var _binding: FragmentStepElevenBinding? = null
    private val binding get() = _binding!!

    private var interstitialAd: InterstitialAd? = null
    private val adUnitId = "ca-app-pub-4392518639765691/2440794028"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepElevenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButton()

        // 광고 요청
        loadNativeAd(requireContext(), binding.nativeAdContainer)
    }

    private fun setupButton() {
        binding.btnHome.setOnClickListener {
            if (interstitialAd != null) {
                interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        navigateToMainActivity()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        navigateToMainActivity()
                    }

                    override fun onAdShowedFullScreenContent() {
                        interstitialAd = null // 광고를 보여준 뒤 null 처리 → 재로딩
                    }
                }
                interstitialAd?.show(requireActivity())
            } else { // 광고가 없으면 바로 이동
                navigateToMainActivity()
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }
        })
    }

    private fun navigateToMainActivity() { // home으로 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}