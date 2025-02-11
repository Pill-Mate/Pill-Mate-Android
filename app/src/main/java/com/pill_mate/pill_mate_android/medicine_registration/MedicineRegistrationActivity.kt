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

        // Intent로 전달된 destination 확인
        val destination = intent.getStringExtra("destination")

        // MedicineRegistrationFragment에 destination 값을 전달
        val bundle = Bundle()
        bundle.putString("destination", destination)
        fragment.arguments = bundle
    }
}