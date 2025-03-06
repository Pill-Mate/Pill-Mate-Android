package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pill_mate.pill_mate_android.R

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
        when (destination) {
            "step1" -> moveToStepOne()
            "step8" -> moveToStepEight()
        }
    }

    private fun moveToStepOne() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? MedicineRegistrationFragment
        fragment?.navigateToStepOne() // StepOneFragment로 이동
    }

    private fun moveToStepEight() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? MedicineRegistrationFragment
        fragment?.navigateToStepEight() // StepEightFragment로 이동
    }
}