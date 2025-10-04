package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.GlobalApplication

class MedicineRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_registration)

        // 약 등록 화면 진입 이벤트 로깅 (JSONObject 필요 없음)
        GlobalApplication.amplitude.track(
            "screen_view",
            mapOf("screen_name" to "screen_medicine_add")
        )

        // Fragment를 항상 재사용하도록 변경
        val fragment = MedicineRegistrationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
