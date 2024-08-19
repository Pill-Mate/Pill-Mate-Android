package com.example.pill_mate_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.pill_mate_android.pillSearch.view.PillSearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if we are recreating the activity (i.e., not the first time)
        if (savedInstanceState == null) {
            // Create the initial fragment to display
            val fragment = PillSearchFragment() // Replace with your initial fragment
            // Add the fragment to the container
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
            }
        }
    }
}