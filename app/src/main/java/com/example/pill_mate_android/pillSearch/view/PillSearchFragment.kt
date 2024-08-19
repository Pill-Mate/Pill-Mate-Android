package com.example.pill_mate_android.pillSearch.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentPillSearchBinding
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenter
import com.example.pill_mate_android.pillSearch.presenter.PillSearchPresenterImpl

class PillSearchFragment : Fragment(), PillSearchView {

    private lateinit var binding: FragmentPillSearchBinding
    private lateinit var presenter: PillSearchPresenter
    private lateinit var adapter: PillIdntfcAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPillSearchBinding.inflate(inflater, container, false)
        presenter = PillSearchPresenterImpl(this)
        setupRecyclerView()
        setupSearchBar()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = PillIdntfcAdapter { pillItem ->
            // 아이템 클릭 시 DialogFragment
            val dialog = PillDetailDialogFragment.newInstance(pillItem)
            dialog.show(childFragmentManager, "PillDetailDialog")
        }
        binding.rvSuggestion.layoutManager = LinearLayoutManager(context)
        binding.rvSuggestion.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.etPillSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrEmpty()) {
                    binding.apply {
                        ivDelete.visibility = View.GONE
                        rvSuggestion.visibility = View.GONE
                        etPillSearch.setBackgroundResource(R.drawable.search_view_background)
                    }
                } else {
                    binding.apply {
                        ivDelete.visibility = View.VISIBLE
                        etPillSearch.setBackgroundResource(R.drawable.search_view_changed_background)
                    }
                    val query = text.toString()
                    presenter.searchPills(query)
                }
            }

            override fun afterTextChanged(text: Editable?) {}
        })
    }

    override fun showPillInfo(pills: List<PillInfoItem>) {
        Log.d("PillSearchFragment", "showPills called with ${pills.size} items")
        pills.forEach { Log.d("PillSearchFragment", "Pill: ${it.itemName}") }

        if (pills.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }

    override fun showPillIdntfc(pills: List<PillIdntfcItem>) {
        Log.d("PillSearchFragment", "showPills called with ${pills.size} items")
        pills.forEach { Log.d("PillSearchFragment", "Pill: ${it.ITEM_NAME}") }

        if (pills.isNotEmpty()) {
            binding.rvSuggestion.visibility = View.VISIBLE
            adapter.updateItems(pills)
        } else {
            binding.rvSuggestion.visibility = View.GONE
        }
    }
}