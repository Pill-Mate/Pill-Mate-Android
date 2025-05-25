package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentLoadingNoConflictBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity

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

        (activity as? MainActivity)?.hideBottomNav()
        setupButton()
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            try {
                findNavController().popBackStack(
                    R.id.ConflictPillSearchFragment,
                    inclusive = false
                )
            } catch (e: Exception) {
                Log.e("LoadingNoConflictFragment", "Navigation error", e)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}