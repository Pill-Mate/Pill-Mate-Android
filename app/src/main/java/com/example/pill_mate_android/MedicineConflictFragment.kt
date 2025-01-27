package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.example.pill_mate_android.databinding.FragmentMedicineConflictBinding
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.model.EfcyDplctResponse
import com.example.pill_mate_android.pillSearch.model.UsjntTabooResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineConflictFragment : Fragment() {

    private var _binding: FragmentMedicineConflictBinding? = null
    private val binding get() = _binding!!

    private lateinit var contraindicationAdapter: ConflictAdapter
    private lateinit var efficiencyOverlapAdapter: ConflictAdapter

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
        loadMedicineData()
        fetchUsjntTabooData() // 병용금기 데이터 가져오기
        fetchEfcyDplctData()  // 효능군 중복 데이터 가져오기
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }

        // 병용금기 RecyclerView 설정
        contraindicationAdapter = ConflictAdapter()
        binding.rvContraindication.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contraindicationAdapter
        }

        // 효능군 중복 RecyclerView 설정
        efficiencyOverlapAdapter = ConflictAdapter()
        binding.rvEfficiencyOverlap.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = efficiencyOverlapAdapter
        }

        binding.btnSkip.setOnClickListener {
            // 부모 Fragment 찾기
            val parentFragment = parentFragment as? MedicineRegistrationFragment

            // 부모 Fragment를 통해 네비게이션
            parentFragment?.let {
                // fullscreen_container 숨기기
                it.hideFullscreenFragment()

                // nav_host_fragment_steps에 StepThree로 이동
                it.findNavController().navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
            }
        }

        binding.btnFinish.setOnClickListener {
            // 등록 종료하기 로직 구현
        }
    }

    private fun loadMedicineData() {
        val medicine = DataRepository.getMedicine()

        if (medicine != null) {
            binding.tvPillName.text = medicine.medicine_name
            binding.tvPillEntp.text = medicine.entp_name
            binding.tvPillClass.text = medicine.classname
            binding.ivMedicineImage.load(medicine.image) {
                transformations(RoundedCornersTransformation(20f))
                crossfade(true)
                error(R.drawable.ic_default_pill)
            }
        } else {
            binding.tvPillName.text = "약물 없음"
            binding.ivMedicineImage.setImageResource(R.drawable.ic_default_pill)
        }
    }

    // 병용금기 데이터 가져오기
    private fun fetchUsjntTabooData() {
        val itemSeq = arguments?.getString("itemSeq") ?: return
        medicineRegistrationService.getUsjntTaboo(itemSeq)
            .enqueue(object : Callback<List<UsjntTabooResponse>> {
                override fun onResponse(
                    call: Call<List<UsjntTabooResponse>>,
                    response: Response<List<UsjntTabooResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()
                        contraindicationAdapter.submitList(data)
                        binding.rvContraindication.visibility = if (data.isNotEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Log.e("UsjntTaboo", "Failed to fetch data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                    Log.e("UsjntTaboo", "Network error: ${t.message}")
                }
            })
    }

    // 효능군 중복 데이터 가져오기
    private fun fetchEfcyDplctData() {
        val itemSeq = arguments?.getString("itemSeq") ?: return
        medicineRegistrationService.getEfcyDplct(itemSeq)
            .enqueue(object : Callback<List<EfcyDplctResponse>> {
                override fun onResponse(
                    call: Call<List<EfcyDplctResponse>>,
                    response: Response<List<EfcyDplctResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body() ?: emptyList()
                        efficiencyOverlapAdapter.submitList(data)
                        binding.rvEfficiencyOverlap.visibility = if (data.isNotEmpty()) View.VISIBLE else View.GONE

                        Log.d("EfcyDplct", "Received data: $data")
                    } else {
                        Log.e("EfcyDplct", "Failed to fetch data: ${response.errorBody()?.string()}")
                        binding.rvEfficiencyOverlap.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    Log.e("EfcyDplct", "Network error: ${t.message}")
                    binding.rvEfficiencyOverlap.visibility = View.GONE
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}