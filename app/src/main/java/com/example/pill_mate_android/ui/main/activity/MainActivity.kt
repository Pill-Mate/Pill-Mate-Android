package com.example.pill_mate_android.ui.main.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R.id
import com.example.pill_mate_android.databinding.ActivityMainBinding
import com.example.pill_mate_android.ui.main.contract.MainContract
import com.example.pill_mate_android.ui.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        presenter = MainPresenter(this)
        presenter.onCreate()
    }

    override fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initBottomNavi() {
        binding.bnvMain.setOnItemSelectedListener {
            presenter.onBottomNavigationItemSelected(it.itemId)
            true
        }
    }

    override fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(id.frm_main, fragment).commitAllowingStateLoss()
    }
}
