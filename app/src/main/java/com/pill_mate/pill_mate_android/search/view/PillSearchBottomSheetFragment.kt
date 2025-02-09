package com.pill_mate.pill_mate_android.search.view

import android.app.Dialog
import android.content.DialogInterface
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
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentSearchPillBinding
import com.pill_mate.pill_mate_android.medicine_conflict.PillDetailBottomSheetFragment
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.model.Searchable
import com.pill_mate.pill_mate_android.search.presenter.PillSearchPresenter
import com.pill_mate.pill_mate_android.search.presenter.PillSearchPresenterImpl
import com.pill_mate.pill_mate_android.search.presenter.StepTwoPresenter
import com.pill_mate.pill_mate_android.search.presenter.StepTwoPresenterImpl
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PillSearchBottomSheetFragment(
    private val stepTwoView: StepTwoView, // StepTwoView를 인자로 받음
    private val medicineRegistrationFragment: MedicineRegistrationFragment // 추가
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

        initView() // 초기화 메서드 호출
    }

    private fun initView() {
        adapter = PillIdntfcAdapter(onItemClick = { pillItem ->
            // 아이템 클릭 시 다이얼로그 생성 및 표시
            val dialog = PillDetailBottomSheetFragment.newInstance(
                this,
                medicineRegistrationFragment, // MedicineRegistrationFragment 전달
                pillItem
            )
            dialog.show(parentFragmentManager, "PillDetailDialog")
        })

        binding.ivExit.setOnClickListener {
            dismiss()
        }

        binding.rvSuggestion.setOnTouchListener { _, _ ->
            hideKeyboard()
            false // RecyclerView의 기본 스크롤 동작 유지
        }

        // 포커스 설정
        binding.etPillSearch.requestFocus()

        // RecyclerView에 레이아웃 매니저와 어댑터 설정
        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter

        // Divider 추가 (아이템 구분선)
        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3)
        val dividerHeight = 1f // 1dp
        val marginStart = 24f
        val marginEnd = 24f
        binding.rvSuggestion.addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))

        setupSearchBar() // 검색 바 세팅 메서드 호출
    }

    private fun setupSearchBar() {
        binding.etPillSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val underlineColor = if (currentQuery.isNotEmpty()) R.color.main_blue_1 else R.color.black
                updateUnderline(underlineColor)

                if (text.isNullOrEmpty()) {
                    binding.rvSuggestion.visibility = View.GONE
                } else {
                    currentQuery = text.toString() // 검색어 업데이트
                    pillSearchPresenter.searchPills(currentQuery) // 검색 요청
                }
            }

            override fun afterTextChanged(text: Editable?) {}
        })
    }

    private fun updateUnderline(colorRes: Int) {
        val underlineColor = ContextCompat.getColor(requireContext(), colorRes)
        binding.vUnderline.setBackgroundColor(underlineColor)
    }

    override fun showPillIdntfc(pills: List<PillIdntfcItem>) {
        pills.forEach { Log.d("PillSearchFragment", "Pill: ${it.ITEM_NAME}") }

        if (pills.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
            adapter.updateItems(pills, currentQuery)
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun showResults(results: List<Searchable>, type: SearchType) {
        Log.d("PillSearchFragment", "showPharmacy called with items")
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var dismissListener: (() -> Unit)? = null
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke() // 바텀시트 닫힐 때 콜백 실행
    }

    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    companion object {
        fun newInstance(
            stepTwoView: StepTwoView,
            medicineRegistrationFragment: MedicineRegistrationFragment
        ): PillSearchBottomSheetFragment {
            return PillSearchBottomSheetFragment(stepTwoView, medicineRegistrationFragment)
        }
    }
}