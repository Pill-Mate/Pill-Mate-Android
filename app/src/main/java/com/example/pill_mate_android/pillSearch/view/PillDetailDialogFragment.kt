package com.example.pill_mate_android.pillSearch.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.databinding.FragmentPillDetailDialogBinding
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenter

class PillDetailDialogFragment(
    private val presenter: StepTwoPresenter,
    private val bottomSheet: PillSearchBottomSheetFragment // 바텀 시트 인스턴스 전달
) : DialogFragment() {

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
            binding.ivPillImage.load(it.ITEM_IMAGE) {
                transformations(RoundedCornersTransformation(20f))
            }
            binding.tvPillClass.text = it.CLASS_NAME
            binding.tvPillName.text = it.ITEM_NAME
            binding.tvPillEntp.text = it.ENTP_NAME
        }

        binding.btnYes.setOnClickListener {
            pillItem?.let { item ->
                presenter.onPillSelected(item) // `PillIdntfcItem` 객체 전달
            }
            bottomSheet.dismiss() // 바텀 시트 닫기
            dismiss() // 다이얼로그 닫기
        }

        binding.btnNo.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            presenter: StepTwoPresenter,
            bottomSheet: PillSearchBottomSheetFragment, // 바텀 시트 인스턴스 전달
            pillItem: PillIdntfcItem
        ): PillDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("pillItem", pillItem)
            val fragment = PillDetailDialogFragment(presenter, bottomSheet)
            fragment.arguments = args
            return fragment
        }
    }
}