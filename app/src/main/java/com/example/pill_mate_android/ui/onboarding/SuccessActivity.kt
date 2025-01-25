package com.example.pill_mate_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.ActivitySuccessBinding
import com.example.pill_mate_android.ui.main.activity.MainActivity

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.success)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onOkButtonClick()
    }

    private fun onOkButtonClick() {
        binding.btnOk.setOnClickListener {
            val intent = Intent(this@SuccessActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}