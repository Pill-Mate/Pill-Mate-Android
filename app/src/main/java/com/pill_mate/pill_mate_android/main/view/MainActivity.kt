package com.pill_mate.pill_mate_android.main.view

import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R.id
import com.pill_mate.pill_mate_android.databinding.ActivityMainBinding
import com.pill_mate.pill_mate_android.main.contract.MainContract
import com.pill_mate.pill_mate_android.main.presenter.MainPresenter
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationActivity

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainPresenter

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDefaultStatusBar()
        onPlusButtonClick()

        presenter = MainPresenter(this)
        binding.bottomNavMain.itemIconTintList = null
        binding.bottomNavMain.selectedItemId = id.menu_home

        presenter.onCreate()

    }

    private fun onPlusButtonClick() {
        binding.floatingBtnAdd.setOnClickListener { // 약물 등록 액티비티로 이동
            val intent = Intent(this, MedicineRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initBottomNavi() {
        binding.bottomNavMain.setOnItemSelectedListener {
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
