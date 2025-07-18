package com.pill_mate.pill_mate_android.pilledit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentPillEditBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.pilledit.view.adapter.PillEditViewPagerAdapter
import com.pill_mate.pill_mate_android.util.loadNativeAd

class PillEditFragment : Fragment() {
    private var _binding: FragmentPillEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPillEditBinding.inflate(layoutInflater, container, false)
        viewPager = binding.vpPillEdit
        tabLayout = binding.tabPillEdit
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        // 광고 초기화 및 광고 요청
        loadNativeAd(requireContext(), binding.nativeAdContainer)
    }

    private fun setupViewPager() {
        val pagerAdapter = PillEditViewPagerAdapter(this)

        pagerAdapter.addFragment(ActiveMedicineFragment())
        pagerAdapter.addFragment(InActiveMedicineFragment())

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "복용중"
                1 -> "복용중지"
                else -> ""
            }
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setDefaultStatusBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}