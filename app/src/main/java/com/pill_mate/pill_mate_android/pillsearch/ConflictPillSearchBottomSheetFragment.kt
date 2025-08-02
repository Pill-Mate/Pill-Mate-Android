package com.pill_mate.pill_mate_android.pillsearch

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentSearchPillBinding
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.model.Searchable
import com.pill_mate.pill_mate_android.search.presenter.*
import com.pill_mate.pill_mate_android.search.view.PillSearchView
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.pill_mate.pill_mate_android.util.KeyboardUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ConflictPillSearchBottomSheetFragment(
    private val resultView: PillSearchResultView
) : BottomSheetDialogFragment(), PillSearchView {

    private var _binding: FragmentSearchPillBinding? = null
    private val binding get() = _binding!!

    private lateinit var pillSearchPresenter: SearchPresenter
    private lateinit var pillResultPresenter: PillSearchResultPresenter
    private lateinit var adapter: SearchMedicineAdapter

    private var currentQuery: String = ""
    private val searchQueryFlow = MutableStateFlow("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPillBinding.inflate(inflater, container, false)
        pillSearchPresenter = SearchPresenterImpl(this)
        pillResultPresenter = PillSearchResultPresenterImpl(resultView)
        return binding.root
    }

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        adapter = SearchMedicineAdapter(onItemClick = { medicineItem ->
            val dialog = ConflictPillDetailBottomSheet.newInstance(this, medicineItem)
            dialog.show(parentFragmentManager, "PillDetailDialog")
        })

        binding.rvSuggestion.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ConflictPillSearchBottomSheetFragment.adapter
            val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3)
            val dividerHeight = 1f
            val marginStart = 24f
            val marginEnd = 24f
            addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))
            setOnTouchListener { v, _ ->
                KeyboardUtil.hideKeyboard(requireContext(), v)
                false
            }
        }

        // 키보드 숨김
        binding.mainBg.setOnTouchListener { v, _ ->
            KeyboardUtil.hideKeyboard(requireContext(), v)
            v.clearFocus()
            false // 터치 이벤트는 계속 전달
        }

        binding.ivExit.setOnClickListener { dismiss() }

        binding.etPillSearch.apply {
            requestFocus()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    currentQuery = s.toString()
                    searchQueryFlow.value = currentQuery
                    updateUnderline(if (currentQuery.isNotEmpty()) R.color.main_blue_1 else R.color.black)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        lifecycleScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collectLatest { query ->
                    pillSearchPresenter.searchMedicines(query)
                }
        }
    }

    private fun updateUnderline(colorRes: Int) {
        val underlineColor = ContextCompat.getColor(requireContext(), colorRes)
        binding.vUnderline.setBackgroundColor(underlineColor)
    }

    override fun showResults(results: List<Searchable>, type: SearchType) {
        // 사용 안 함
    }

    override fun showMedicines(medicines: List<SearchMedicineItem>) {
        if (medicines.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
            adapter.updateItems(medicines, currentQuery)
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private var dismissListener: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    companion object {
        fun newInstance(resultView: PillSearchResultView): ConflictPillSearchBottomSheetFragment {
            return ConflictPillSearchBottomSheetFragment(resultView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}