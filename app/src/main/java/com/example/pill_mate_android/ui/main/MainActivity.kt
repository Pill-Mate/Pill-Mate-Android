package com.example.pill_mate_android.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.R.id
import com.example.pill_mate_android.databinding.ActivityMainBinding
import com.example.pill_mate_android.ui.mypage.MyPageFragment
import com.example.pill_mate_android.ui.pillcheck.PillCheckFragment
import com.example.pill_mate_android.ui.pilledit.PillEditFragment
import com.example.pill_mate_android.ui.pillsearch.PillSearchFragment
import com.example.pill_mate_android.ui.pillstats.PillStatsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        initBottomNavi()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initBottomNavi() {

        supportFragmentManager.beginTransaction().replace(id.frm_main, PillCheckFragment()).commitAllowingStateLoss()

        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                id.menu_check -> {
                    supportFragmentManager.beginTransaction().replace(id.frm_main, PillCheckFragment())
                        .commitAllowingStateLoss()
                    true
                }

                id.menu_stats -> {
                    supportFragmentManager.beginTransaction().replace(id.frm_main, PillStatsFragment())
                        .commitAllowingStateLoss()
                    true
                }

                id.menu_edit -> {
                    supportFragmentManager.beginTransaction().replace(id.frm_main, PillEditFragment())
                        .commitAllowingStateLoss()
                    true
                }

                id.menu_search -> {
                    supportFragmentManager.beginTransaction().replace(id.frm_main, PillSearchFragment())
                        .commitAllowingStateLoss()
                    true
                }

                else -> {
                    supportFragmentManager.beginTransaction().replace(id.frm_main, MyPageFragment())
                        .commitAllowingStateLoss()
                    true
                }
            }
        }
    }
}