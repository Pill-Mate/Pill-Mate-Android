package com.pill_mate.pill_mate_android.ui.pilledit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ui.main.activity.MainActivity

class PillEditFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pill_edit, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setDefaultStatusBar()
    }

}