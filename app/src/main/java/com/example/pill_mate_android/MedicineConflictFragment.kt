package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.example.pill_mate_android.databinding.FragmentMedicineConflictBinding
import com.example.pill_mate_android.pillSearch.model.ConflictResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineConflictFragment : Fragment() {

    private var _binding: FragmentMedicineConflictBinding? = null
    private val binding get() = _binding!!

    private lateinit var contraindicationAdapter: ConflictAdapter
    private lateinit var efficiencyOverlapAdapter: ConflictAdapter
    private lateinit var sameIngredientOverlapAdapter: ConflictAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineConflictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        fetchConflictData()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }

        contraindicationAdapter = ConflictAdapter()
        efficiencyOverlapAdapter = ConflictAdapter()
        sameIngredientOverlapAdapter = ConflictAdapter()

        binding.rvContraindication.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contraindicationAdapter
        }

        binding.rvEfficiencyOverlap.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = efficiencyOverlapAdapter
        }

        binding.rvSameIngredientOverlap.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sameIngredientOverlapAdapter
        }

        binding.btnSkip.setOnClickListener {
            // 건너뛰기 로직 구현
        }

        binding.btnFinish.setOnClickListener {
            // 등록 종료하기 로직 구현
        }
    }

    private fun fetchConflictData() {
        val identifyNumber = arguments?.getString("identifyNumber") ?: run {
            Log.e("MedicineConflict", "Identify number is missing.")
            return
        }

        medicineRegistrationService.getMedicineConflicts(identifyNumber)
            .enqueue(object : Callback<ConflictResponse> {
                override fun onResponse(call: Call<ConflictResponse>, response: Response<ConflictResponse>) {
                    if (response.isSuccessful) {
                        updateUI(response.body())
                    } else {
                        Log.e("MedicineConflict", "Failed to fetch data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ConflictResponse>, t: Throwable) {
                    Log.e("MedicineConflict", "Network error: ${t.message}")
                }
            })
    }

    private fun updateUI(conflictData: ConflictResponse?) {
        conflictData?.let { data ->
            binding.tvPillName.text = data.medicine.name
            binding.tvPillClass.text = data.medicine.ingredient
            binding.tvPillEntp.text = data.medicine.manufacturer

            contraindicationAdapter.submitList(data.conflicts.filter { it.crashName == "병용금기" })
            efficiencyOverlapAdapter.submitList(data.conflicts.filter { it.crashName == "효능군 중복" })
            sameIngredientOverlapAdapter.submitList(data.conflicts.filter { it.crashName == "동일성분 중복" })

            binding.rvContraindication.visibility =
                if (contraindicationAdapter.itemCount > 0) View.VISIBLE else View.GONE
            binding.rvEfficiencyOverlap.visibility =
                if (efficiencyOverlapAdapter.itemCount > 0) View.VISIBLE else View.GONE
            binding.rvSameIngredientOverlap.visibility =
                if (sameIngredientOverlapAdapter.itemCount > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}