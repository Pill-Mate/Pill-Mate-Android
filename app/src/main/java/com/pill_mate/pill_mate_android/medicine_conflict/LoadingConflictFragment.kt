package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentLoadingConflictBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem

class LoadingConflictFragment : Fragment() {
    private var _binding: FragmentLoadingConflictBinding? = null
    private val binding get() = _binding!!

    private var usjntTabooData: ArrayList<UsjntTabooResponse>? = null
    private var efcyDplctData: ArrayList<EfcyDplctResponse>? = null
    private var pillItem: PillIdntfcItem? = null
    private var source: String? = null

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

        (activity as? MainActivity)?.hideBottomNav()

        usjntTabooData = arguments?.getParcelableArrayList("usjntTabooData")
        efcyDplctData = arguments?.getParcelableArrayList("efcyDplctData")
        pillItem = arguments?.getParcelable("pillItem")
        source = arguments?.getString("source")

        Log.d("LoadingConflictFragment", "📦 전달받은 source: $source")
        Log.d("LoadingConflictFragment", "📦 전달할 pillItem: ${pillItem?.ITEM_NAME}")

        setupButton()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupButton() {
        binding.btnCheck.setOnClickListener {
            try {
                val bundle = bundleOf(
                    "usjntTabooData" to usjntTabooData,
                    "efcyDplctData" to efcyDplctData,
                    "pillItem" to pillItem
                )

                when (source) {
                    "pillSearch" -> {
                        findNavController().navigate(
                            R.id.action_loadingConflictFragment_to_ConflictPillDetailFragment,
                            bundle
                        )
                    }
                    "medicineRegistration" -> {
                        findNavController().navigate(
                            R.id.action_loadingConflictFragment_to_medicineConflictFragment,
                            bundle
                        )
                    }
                    else -> {
                        Log.w("LoadingConflictFragment", "알 수 없는 source: $source → 기본 이동 없음")
                    }
                }
            } catch (e: Exception) {
                Log.e("LoadingConflictFragment", "Error navigating from LoadingConflictFragment", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}