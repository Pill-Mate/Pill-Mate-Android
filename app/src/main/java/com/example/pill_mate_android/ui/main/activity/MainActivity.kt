package com.example.pill_mate_android.ui.main.activity

import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R.id
import com.example.pill_mate_android.databinding.ActivityMainBinding
import com.example.pill_mate_android.ui.main.contract.MainContract
import com.example.pill_mate_android.ui.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainPresenter

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDefaultStatusBar()

        presenter = MainPresenter(this)
        presenter.onCreate()
    }

    override fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initBottomNavi() {
        binding.bnvMain.setOnItemSelectedListener {
            presenter.onBottomNavigationItemSelected(it.itemId)
            true
        }
    }

    override fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(id.frm_main, fragment).commitAllowingStateLoss()
    }

    // 기본 상태바로 설정
    fun setDefaultStatusBar() {
        window.apply {
            statusBarColor = ContextCompat.getColor(this@MainActivity, android.R.color.transparent) // 기본 상태바 색상
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = true // 기본 상태바 아이콘(검정)
        }
    }

    // 상태바 색상 변경
    fun setStatusBarColor(colorResId: Int, isLightStatusBar: Boolean) {
        window.apply {
            statusBarColor = ContextCompat.getColor(this@MainActivity, colorResId) // 상태바 색상 변경
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = isLightStatusBar // 아이콘 색상 변경
        }
    }

}
