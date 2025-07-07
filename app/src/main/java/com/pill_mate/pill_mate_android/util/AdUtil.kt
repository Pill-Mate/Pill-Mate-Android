package com.pill_mate.pill_mate_android.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.pill_mate.pill_mate_android.R

fun loadNativeAd(context: Context, container: ViewGroup) {
    val adLoader = AdLoader.Builder(context, "ca-app-pub-4392518639765691/9657595600")
        .forNativeAd { nativeAd: NativeAd ->
            val adView = LayoutInflater.from(context)
                .inflate(R.layout.view_native_ad, container, false) as NativeAdView

            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            adView.headlineView = headlineView
            headlineView.text = nativeAd.headline

            // 기타 뷰 바인딩 (필요시)
            adView.setNativeAd(nativeAd)

            container.removeAllViews()
            container.addView(adView)
        }
        .withAdListener(object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                Log.e("AdMob", "Native ad failed to load: ${error.message}")
            }
        })
        .withNativeAdOptions(NativeAdOptions.Builder().build())
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
}