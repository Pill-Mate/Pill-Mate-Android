package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentLoadingNoConflictBinding

class LoadingNoConflictFragment : Fragment() {
    private var _binding: FragmentLoadingNoConflictBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingNoConflictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButton()
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            try {
                findNavController().popBackStack(
                    R.id.ConflictPillSearchFragment,
                    inclusive = false // false: 해당 fragment까지 되돌아가고 유지
                )
            } catch (e: Exception) {
                Log.e("LoadingNoConflictFragment", "Navigation error", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}