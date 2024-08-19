package com.example.pill_mate_android.pillSearch.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import com.example.pill_mate_android.databinding.FragmentPillDetailDialogBinding
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem

class PillDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentPillDetailDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val pillItem = arguments?.getParcelable<PillIdntfcItem>("pillItem")

        pillItem?.let {
            binding.ivPillImage.load(it.ITEM_IMAGE)
            binding.tvPillClass.text = it.CLASS_NAME
            binding.tvPillName.text = it.ITEM_NAME
            binding.tvPillIngredient.text = it.ENTP_NAME
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(pillItem: PillIdntfcItem): PillDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("pillItem", pillItem)
            val fragment = PillDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}