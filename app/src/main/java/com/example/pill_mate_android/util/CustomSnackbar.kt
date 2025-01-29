package com.example.pill_mate_android.util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pill_mate_android.R
import com.google.android.material.snackbar.Snackbar

object CustomSnackbar {

    fun showCustomSnackbar(context: Context, rootView: View, message: String) {
        // 기본 Snackbar 생성
        val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)

        // Custom View Inflate
        val customView = LayoutInflater.from(context).inflate(R.layout.snackbar_custom, null)

        // Custom View에서 텍스트 설정
        val textView = customView.findViewById<TextView>(R.id.tv_content)
        textView.text = message

        // Snackbar의 기본 배경을 투명하게 설정
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        // Snackbar의 부모 레이아웃 가져오기
        val snackbarLayout = snackbar.view as ViewGroup

        // 기존 뷰 제거 및 커스텀 뷰 추가
        snackbarLayout.removeAllViews()
        snackbarLayout.addView(customView)

        // Snackbar의 마진 설정
        val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            0,
            0,
            0,
            context.resources.getDimensionPixelSize(R.dimen.snackbar_margin_bottom) // 하단 마진 적용
        )
        snackbar.view.layoutParams = params

        // Snackbar 표시
        snackbar.show()
    }
}