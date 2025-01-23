package com.example.pill_mate_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MedicineRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_registration)

        if (savedInstanceState == null) {
            val fragment = MedicineRegistrationFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        // Intent로 전달된 destination 확인
        val destination = intent.getStringExtra("destination")
        if (destination == "step8") {
            moveToStepEight()
        }
    }

    private fun moveToStepEight() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? MedicineRegistrationFragment
        fragment?.navigateToStepEight() // StepEightFragment로 이동
    }
}