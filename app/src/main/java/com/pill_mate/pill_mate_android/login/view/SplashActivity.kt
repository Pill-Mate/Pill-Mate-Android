package com.pill_mate.pill_mate_android.login.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.GlobalApplication
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.main.view.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jwtToken = GlobalApplication.getToken()

        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({ // jwtToken 보유 여부에 따라 다음 페이지로 이동
            val nextActivity = if (jwtToken.isNotEmpty()) {
                MainActivity::class.java
            } else {
                KakaoLoginActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000)
    }
}