package com.pill_mate.pill_mate_android.main.view

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.pill_mate.pill_mate_android.R.id
import com.pill_mate.pill_mate_android.databinding.ActivityMainBinding
import com.pill_mate.pill_mate_android.main.contract.MainContract
import com.pill_mate.pill_mate_android.main.presenter.MainPresenter
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationActivity

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainPresenter
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(this)
        setDefaultStatusBar()
        setupPlusButtonClickListener()
        binding.bottomNavMain.itemIconTintList = null // 아이콘 원본 색상 적용
        binding.bottomNavMain.selectedItemId = id.menu_home // 홈 메뉴를 기본 탭으로 지정

        presenter.onCreate()

    }

    private fun setupPlusButtonClickListener() {
        binding.floatingBtnAdd.setOnClickListener { // 약물 등록 액티비티로 이동
            presenter.onPlusButtonClicked()
            firebaseAnalytics.logEvent("click_add_button", null) // [로그] 약물 등록 진입
        }
    }

    // 기본 상태바로 설정
    @Suppress("DEPRECATION")
    fun setDefaultStatusBar() {
        binding.viewStatusBarBg.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white)) // 기본 상태바 색상 (엣지 투 엣지 기기용)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white) // 구버전(non-edge-to-edge) 기기 호환용
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true // 기본 상태바 아이콘(검정)
    }

    // 상태바 색상 변경
    @Suppress("DEPRECATION")
    fun setStatusBarColor(colorResId: Int, isLightStatusBar: Boolean) {
        val color = ContextCompat.getColor(this, colorResId)
        binding.viewStatusBarBg.setBackgroundColor(color) // 상태바 색상 변경 (엣지 투 엣지 기기용)
        window.statusBarColor = color // 구버전(non-edge-to-edge) 기기 호환용
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightStatusBar // 아이콘 색상 변경
    }

    // 상태바 색상 애니메이션 변경 (스크롤 등에 연동해서 부드럽게 전환할 때 사용)
    @Suppress("DEPRECATION")
    fun animateStatusBarColor(toColor: Int, lightIcons: Boolean) {
        val statusBarBg = binding.viewStatusBarBg
        val fromColor =
            (statusBarBg.background as? ColorDrawable)?.color ?: ContextCompat.getColor(this, android.R.color.white)

        ValueAnimator.ofArgb(fromColor, toColor).apply {
            duration = 300
            addUpdateListener {
                val color = it.animatedValue as Int
                statusBarBg.setBackgroundColor(color) // 엣지 투 엣지 기기용
                window.statusBarColor = color // 구버전(non-edge-to-edge) 기기 호환용
            }
        }.start()

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = lightIcons
    }

    override fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 상단은 루트가 아닌 frm_main에서 개별적으로 소비 (루트에 주면 view_status_bar_bg도 함께 밀려 내려가 상태바 영역을 못 덮게 됨)
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.frmMain.setPadding(0, systemBars.top, 0, 0)
            binding.viewStatusBarBg.layoutParams = binding.viewStatusBarBg.layoutParams.apply {
                height = systemBars.top // 상태바 높이만큼 배경 뷰 크기 고정
            }
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

    override fun navigateToMedicineRegistration() {
        val intent = Intent(this, MedicineRegistrationActivity::class.java)
        startActivity(intent)
    }

    fun hideBottomNav() {
        binding.bottomNavMain.visibility = View.GONE
        binding.floatingBtnAdd.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavMain.visibility = View.VISIBLE
        binding.floatingBtnAdd.visibility = View.VISIBLE
    }
}
