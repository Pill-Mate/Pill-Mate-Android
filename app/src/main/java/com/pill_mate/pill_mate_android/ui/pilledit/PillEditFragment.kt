package com.pill_mate.pill_mate_android.ui.pilledit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.databinding.FragmentPillEditBinding
import com.pill_mate.pill_mate_android.medicine_edit.MedicineEditActivity
import com.pill_mate.pill_mate_android.ui.main.activity.MainActivity

class PillEditFragment : Fragment() {
    private var _binding: FragmentPillEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setDefaultStatusBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivNone.setOnClickListener {
            val intent = Intent(requireContext(), MedicineEditActivity::class.java).apply {
                putExtra("ITEM_SEQ", "47") // 임의의 itemSeq 값 전달
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}