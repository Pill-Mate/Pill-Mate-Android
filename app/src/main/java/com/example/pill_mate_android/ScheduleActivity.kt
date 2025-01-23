package com.example.pill_mate_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
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
        super.onBackPressed()
        // Step 9에서 뒤로가기 시 MedicineRegistrationActivity로 이동
        val intent = Intent(this, MedicineRegistrationActivity::class.java)
        intent.putExtra("destination", "step8")
        startActivity(intent)
        finish()
    }
}