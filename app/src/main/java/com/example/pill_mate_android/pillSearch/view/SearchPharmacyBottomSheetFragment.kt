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
import com.example.pill_mate_android.databinding.FragmentSearchPharmacyBinding
import com.example.pill_mate_android.pillSearch.SearchDividerItemDecoration
import com.example.pill_mate_android.pillSearch.model.HospitalItem
import com.example.pill_mate_android.pillSearch.model.PharmacyItem
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenter
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenterImpl
import com.example.pill_mate_android.pillSearch.util.SharedPreferencesHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchPharmacyBottomSheetFragment : BottomSheetDialogFragment(), PillSearchView {

    private var _binding: FragmentSearchPharmacyBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: PillSearchPresenter
    private lateinit var adapter: PharmacyAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPharmacyBinding.inflate(inflater, container, false)
        presenter = PillSearchPresenterImpl(this)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
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

        adapter = PharmacyAdapter(
            onItemClick = { selectedTerm ->
                binding.etPharmacySearch.setText(selectedTerm)
                binding.etPharmacySearch.setSelection(selectedTerm.length)
                presenter.searchPharmacies(selectedTerm)
            },
            onSearchResultClick = { pharmacyName ->
                sharedPreferencesHelper.saveSearchTerm(pharmacyName)
                sendPharmacyNameToParent(pharmacyName)
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
        setupSearchBar()
    }

    private fun setupSearchBar() {
        binding.etPharmacySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()

                // 입력된 텍스트가 있으면 하늘색(두께 2dp), 없으면 검정색(두께 1dp)
                val (underlineColor, underlineThickness) = if (currentQuery.isNotEmpty()) {
                    Pair(R.color.main_blue_1, 2) // 하늘색과 2dp
                } else {
                    Pair(R.color.black, 1) // 검정색과 1dp
                }

                updateUnderline(underlineColor, underlineThickness)

                if (currentQuery.isEmpty()) {
                    updateRecentSearches()
                } else {
                    presenter.searchPharmacies(currentQuery)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateUnderline(colorRes: Int, thickness: Int) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(ContextCompat.getColor(requireContext(), android.R.color.transparent)) // 투명 배경
            setStroke(thickness, ContextCompat.getColor(requireContext(), colorRes)) // 색상과 두께 설정
        }
        binding.vUnderline.background = drawable
    }

    private fun updateRecentSearches() {
        val recentSearches = sharedPreferencesHelper.getRecentSearches()
        if (recentSearches.isNotEmpty()) {
            adapter.updateRecentSearches(recentSearches)
            binding.rvSuggestion.visibility = View.VISIBLE
            binding.tvRecentSearch.visibility = View.VISIBLE
            binding.btnClearAll.visibility = View.VISIBLE
        } else {
            binding.rvSuggestion.visibility = View.GONE
            binding.tvRecentSearch.visibility = View.GONE
            binding.btnClearAll.visibility = View.GONE
        }
    }

    private fun sendPharmacyNameToParent(pharmacyName: String) {
        val result = Bundle().apply { putString("pharmacy", pharmacyName) }
        parentFragmentManager.setFragmentResult("requestKey", result)
    }

    override fun showPharmacies(pharmacies: List<PharmacyItem>) {
        if (_binding == null) return // binding이 null이면 아무 작업도 하지 않음

        if (pharmacies.isNotEmpty()) {
            adapter.updatePharmacies(pharmacies, currentQuery)
            binding.rvSuggestion.visibility = View.VISIBLE
            binding.tvRecentSearch.visibility = View.GONE
            binding.btnClearAll.visibility = View.GONE
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun showHospitals(pills: List<HospitalItem>) {
        Log.d("PharmacySearchFragment", "showPharmacy called with items")
    }

    override fun showPillInfo(pills: List<PillInfoItem>) {
        Log.d("SearchPharmacyFragment", "showPillInfo called with ${pills.size} items")
    }

    override fun showPillIdntfc(pills: List<PillIdntfcItem>) {
        Log.d("SearchPharmacyFragment", "showPillIdntfc called with ${pills.size} items")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}