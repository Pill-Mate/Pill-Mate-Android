package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pill_mate_android.databinding.FragmentLoadingConflictBinding

class LoadingConflictFragment : Fragment() {
    private var _binding: FragmentLoadingConflictBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingConflictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.lottieConflict.apply {
            setAnimation(R.raw.loading_animation)
            loop(true)
            playAnimation()
        }*/

        setupButton()
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            try {
                val itemSeq = arguments?.getString("itemSeq")
                if (itemSeq == null) {
                    Log.e("LoadingConflictFragment", "itemSeq is null")
                    return@setOnClickListener
                }

                Log.d("LoadingConflictFragment", "itemSeq: $itemSeq")

                findNavController().navigate(
                    R.id.action_loadingConflictFragment_to_medicineConflictFragment,
                    bundleOf("itemSeq" to itemSeq)
                )
            } catch (e: Exception) {
                Log.e("LoadingConflictFragment", "Error in btnCheck click: ${e.message}", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding.lottieConflict.cancelAnimation()
        _binding = null
    }
}