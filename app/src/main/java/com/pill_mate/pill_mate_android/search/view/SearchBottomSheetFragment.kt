package com.pill_mate.pill_mate_android.search.view

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
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentSearchBottomSheetBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Hospital
import com.pill_mate.pill_mate_android.medicine_registration.model.Pharmacy
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.presenter.PillSearchPresenter
import com.pill_mate.pill_mate_android.search.presenter.PillSearchPresenterImpl
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.model.Searchable
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.pill_mate.pill_mate_android.util.SharedPreferencesHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchBottomSheetFragment(
    private val searchType: SearchType,
    private val onDismiss: (() -> Unit)? = null
) : BottomSheetDialogFragment(), PillSearchView {

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

        initView()
    }

    private fun initView() {
        binding.ivExit.setOnClickListener {
            dismiss()
        }

        binding.rvSuggestion.setOnTouchListener { _, _ ->
            hideKeyboard()
            false // RecyclerView의 기본 스크롤 동작 유지
        }

        adapter = SearchAdapter(
            onItemClick = { selectedTerm ->
                binding.etSearch.setText(selectedTerm)
                binding.etSearch.setSelection(selectedTerm.length)
                presenter.search(selectedTerm, searchType)
            },
            onSearchResultClick = { name, phone, address ->
                sharedPreferencesHelper.saveSearchTerm(name)
                saveDataToRepository(name, phone, address)
                dismiss()
            },
            onDeleteClick = { term ->
                sharedPreferencesHelper.deleteSearchTerm(term)
                updateRecentSearches()
            }
        )
        binding.etSearch.requestFocus()

        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3) // 회색
        val dividerHeight = 1f // 1dp
        val marginStart = 24f
        val marginEnd = 24f
        binding.rvSuggestion.addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))

        binding.btnClearAll.setOnClickListener {
            sharedPreferencesHelper.clearAllSearches()
            updateRecentSearches()
        }

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

    private fun saveDataToRepository(name: String, phone: String, address: String) {
        when (searchType) {
            SearchType.PHARMACY -> {
                DataRepository.pharmacyData = Pharmacy(
                    pharmacyName = name,
                    pharmacyPhone = phone,
                    pharmacyAddress = address
                )
            }
            SearchType.HOSPITAL -> {
                DataRepository.hospitalData = Hospital(
                    hospitalName = name,
                    hospitalPhone = phone,
                    hospitalAddress = address
                )
            }
        }
        Log.d("SearchBottomSheet", "Saved $name with phone $phone and address $address to DataRepository")
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

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onDismiss?.invoke() // BottomSheet가 닫힐 때 콜백 호출
    }
}