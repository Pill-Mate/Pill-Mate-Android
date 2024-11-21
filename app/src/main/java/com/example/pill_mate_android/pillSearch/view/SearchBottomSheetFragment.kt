package com.example.pill_mate_android.pillSearch.view

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
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
import com.example.pill_mate_android.databinding.FragmentSearchBottomSheetBinding
import com.example.pill_mate_android.pillSearch.SearchDividerItemDecoration
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenter
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenterImpl
import com.example.pill_mate_android.pillSearch.model.SearchType
import com.example.pill_mate_android.pillSearch.model.Searchable
import com.example.pill_mate_android.pillSearch.util.SharedPreferencesHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchBottomSheetFragment(private val searchType: SearchType) : BottomSheetDialogFragment(), PillSearchView {

    private var _binding: FragmentSearchBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: PillSearchPresenter
    private lateinit var adapter: SearchAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBottomSheetBinding.inflate(inflater, container, false)
        presenter = PillSearchPresenterImpl(this)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext(), searchType) // SearchType 기반으로 초기화
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        updateRecentSearches()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivExit.setOnClickListener {
            dismiss() // This closes the bottom sheet
        }

        adapter = SearchAdapter(
            onItemClick = { selectedTerm ->
                binding.etSearch.setText(selectedTerm)
                binding.etSearch.setSelection(selectedTerm.length)
                presenter.search(selectedTerm, searchType)
            },
            onSearchResultClick = { selectedResult ->
                sharedPreferencesHelper.saveSearchTerm(selectedResult)
                sendResultToParent(selectedResult)
                dismiss()
            },
            onDeleteClick = { term ->
                sharedPreferencesHelper.deleteSearchTerm(term)
                updateRecentSearches()
            }
        )

        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3)
        val dividerHeight = 1f
        binding.rvSuggestion.addItemDecoration(SearchDividerItemDecoration(dividerColor, dividerHeight))

        initView()

        binding.btnClearAll.setOnClickListener {
            sharedPreferencesHelper.clearAllSearches()
            updateRecentSearches()
        }
    }

    private fun initView() {
        setupTexts()
        setupSearchBar()
    }

    private fun setupTexts() {
        when (searchType) {
            SearchType.PHARMACY -> {
                binding.tvHead.text = getString(R.string.search_pharmacy)
                binding.etSearch.hint = getString(R.string.enter_pharmacy_name)
            }
            SearchType.HOSPITAL -> {
                binding.tvHead.text = getString(R.string.search_hospital)
                binding.etSearch.hint = getString(R.string.enter_hospital_name)
            }
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()

                val underlineColor = if (currentQuery.isNotEmpty()) R.color.main_blue_1 else R.color.black
                updateUnderline(underlineColor)

                if (currentQuery.isEmpty()) {
                    updateRecentSearches()
                } else {
                    presenter.search(currentQuery, searchType)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateUnderline(colorRes: Int) {
        val underlineColor = ContextCompat.getColor(requireContext(), colorRes)
        binding.vUnderline.setBackgroundColor(underlineColor)
    }

    private fun updateRecentSearches() {
        val recentSearches = sharedPreferencesHelper.getRecentSearches()
        if (recentSearches.isNotEmpty()) {
            adapter.updateRecentSearches(recentSearches)
            binding.rvSuggestion.visibility = View.VISIBLE
        } else {
            adapter.updateRecentSearches(emptyList())
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    private fun sendResultToParent(result: String) {
        val bundle = Bundle().apply { putString("selectedItem", result) }
        parentFragmentManager.setFragmentResult("requestKey", bundle)
    }

    override fun showPillInfo(pills: List<PillInfoItem>) {
        Log.d("SearchFragment", "showPillInfo called with items")
    }

    override fun showPillIdntfc(pills: List<PillIdntfcItem>) {
        Log.d("SearchFragment", "showPillIdntfc called with items")
    }

    override fun showResults(results: List<Searchable>, type: SearchType) {
        if (_binding == null) return

        if (results.isNotEmpty()) {
            adapter.updateResults(results, currentQuery) // currentQuery를 전달
            binding.rvSuggestion.visibility = View.VISIBLE
        } else {
            adapter.updateResults(emptyList(), "") // 빈 리스트와 빈 검색어 전달
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}