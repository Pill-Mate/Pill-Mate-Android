package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pill_mate.pill_mate_android.R

class MedicineRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_registration)

        // Fragment를 항상 재사용하도록 변경
        val fragment = MedicineRegistrationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
