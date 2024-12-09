package com.example.pill_mate_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter

class MainActivity : AppCompatActivity() {

    val medicineRegistrationPresenter = MedicineRegistrationPresenter() // Presenter를 Activity에서 관리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = MedicineRegistrationFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}