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
import com.example.pill_mate_android.databinding.FragmentSearchPharmacyBinding
import com.example.pill_mate_android.pillSearch.SearchDividerItemDecoration
import com.example.pill_mate_android.pillSearch.model.PharmacyItem
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenter
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenterImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchPharmacyBottomSheetFragment : BottomSheetDialogFragment(), PillSearchView {

    private var _binding: FragmentSearchPharmacyBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: PillSearchPresenter
    private lateinit var adapter: PharmacyAdapter
    private var currentQuery: String = "" // 현재 검색어 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPharmacyBinding.inflate(inflater, container, false)
        presenter = PillSearchPresenterImpl(this) // 여기서 초기화
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
            // 키보드
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
        // PharmacyAdapter 초기화, 아이템 클릭 시 데이터 전달
        adapter = PharmacyAdapter(onItemClick = { pharmacy ->
            val result = Bundle().apply {
                putString("pharmacy", pharmacy.dutyName)
            }
            parentFragmentManager.setFragmentResult("requestKey", result)
            dismiss() // 바텀 시트 닫기
        })

        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter // RecyclerView에 adapter 설정

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
        binding.etPharmacySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrEmpty()) {
                    binding.apply {
                        ivDelete.visibility = View.GONE
                        rvSuggestion.visibility = View.GONE
                        etPharmacySearch.setBackgroundResource(R.drawable.bg_search_view)
                    }
                } else {
                    binding.apply {
                        ivDelete.visibility = View.VISIBLE
                        etPharmacySearch.setBackgroundResource(R.drawable.bg_search_view_changed)
                    }
                    currentQuery = text.toString() // 검색어 업데이트
                    presenter.searchPharmacies(currentQuery)
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
    }

    override fun showPharmacies(pharmacies: List<PharmacyItem>) {
        Log.d("PharmacySearchFragment", "showPharmacy called with ${pharmacies.size} items")
        pharmacies.forEach { Log.d("PharmacySearchFragment", "Pharmacy: ${it.dutyName}") }

        if (pharmacies.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
            adapter.updateItems(pharmacies, currentQuery)
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}