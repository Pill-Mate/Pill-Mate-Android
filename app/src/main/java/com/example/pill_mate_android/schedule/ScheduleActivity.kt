package com.example.pill_mate_android.schedule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.pill_mate_android.medicine_registration.MedicineRegistrationActivity
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.ActivityScheduleBinding

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                    as NavHostFragment
            val navController = navHostFragment.navController
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment

        when (currentFragment) {
            is StepNineFragment -> {
                val intent = Intent(this, MedicineRegistrationActivity::class.java).apply {
                    putExtra("destination", "step8")
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}