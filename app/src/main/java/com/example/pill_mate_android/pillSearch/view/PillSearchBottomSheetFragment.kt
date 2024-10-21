package com.example.pill_mate_android.pillSearch.view

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentSearchPillBinding
import com.example.pill_mate_android.pillSearch.SearchDividerItemDecoration
import com.example.pill_mate_android.pillSearch.model.PharmacyItem
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenter
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenterImpl
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenter
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenterImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PillSearchBottomSheetFragment(
    private val stepTwoView: StepTwoView // StepTwoView를 인자로 받음
) : BottomSheetDialogFragment(), PillSearchView {

    private var _binding: FragmentSearchPillBinding? = null
    private val binding get() = _binding!!
    private lateinit var pillSearchPresenter: PillSearchPresenter
    private lateinit var stepTwoPresenter: StepTwoPresenter // StepTwoPresenter 추가
    private lateinit var adapter: PillIdntfcAdapter
    private var currentQuery: String = "" // 현재 검색어 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPillBinding.inflate(inflater, container, false)
        pillSearchPresenter = PillSearchPresenterImpl(this) // PillSearchPresenter 초기화
        stepTwoPresenter = StepTwoPresenterImpl(stepTwoView) // StepTwoPresenter 초기화
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme // 바텀 시트의 스타일을 지정
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            // 화면을 꽉 채우도록 설정
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            // 키보드 자동 보이기
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()

        // BottomSheet의 높이를 전체 화면으로 설정
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        // 레이아웃 변경을 적용
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        //behavior.isDraggable = false // 스와이프로 닫히는 것을 막고 싶다면 추가
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PillIdntfcAdapter(onItemClick = { pillItem ->
            // 아이템 클릭 시 다이얼로그 생성 및 표시
            val dialog = PillDetailDialogFragment.newInstance(stepTwoPresenter, this, pillItem)
            dialog.show(parentFragmentManager, "PillDetailDialog")
        })

        // RecyclerView에 레이아웃 매니저와 어댑터 설정
        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter

        // Divider 추가 (아이템 구분선)
        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3) // 옅은 회색
        val dividerHeight = 1f
        binding.rvSuggestion.addItemDecoration(
            SearchDividerItemDecoration(dividerColor, dividerHeight)
        )

        initView() // 초기화 메서드 호출
    }

    private fun initView() {
        setupSearchBar() // 검색 바 세팅 메서드 호출
    }

    private fun setupSearchBar() {
        binding.etPillSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrEmpty()) {
                    binding.apply {
                        ivDelete.visibility = View.GONE
                        rvSuggestion.visibility = View.GONE
                        etPillSearch.setBackgroundResource(R.drawable.bg_search_view)
                    }
                } else {
                    binding.apply {
                        ivDelete.visibility = View.VISIBLE
                        etPillSearch.setBackgroundResource(R.drawable.bg_search_view_changed)
                    }
                    currentQuery = text.toString() // 검색어 업데이트
                    pillSearchPresenter.searchPills(currentQuery) // 검색 요청
                }
            }

            override fun afterTextChanged(text: Editable?) {}
        })
    }

    override fun showPillInfo(pills: List<PillInfoItem>) {
        Log.d("PillSearchFragment", "showPills called with ${pills.size} items")
    }

    override fun showPillIdntfc(pills: List<PillIdntfcItem>) {
        Log.d("PillSearchFragment", "showPills called with ${pills.size} items")
        pills.forEach { Log.d("PillSearchFragment", "Pill: ${it.ITEM_NAME}") }

        if (pills.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
            adapter.updateItems(pills, currentQuery)
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun showPharmacies(pills: List<PharmacyItem>) {
        Log.d("PharmacySearchFragment", "showPharmacy called with items")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}