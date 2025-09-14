package com.pill_mate.pill_mate_android.pilledit.view

import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentInactiveMedicineBinding
import com.pill_mate.pill_mate_android.pilledit.model.MedicineItemData
import com.pill_mate.pill_mate_android.pilledit.model.ResponseInActiveMedicine
import com.pill_mate.pill_mate_android.pilledit.view.adapter.InActiveMedicineAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InActiveMedicineFragment : Fragment() {

    private lateinit var inActiveMedicineAdapter: InActiveMedicineAdapter
    private var _binding: FragmentInactiveMedicineBinding? = null
    private val binding get() = _binding!!

    private val medicineItemDataList = mutableListOf<MedicineItemData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInactiveMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        fetchInActiveMedicineData()
    }

    private fun initAdapter() {
        inActiveMedicineAdapter = InActiveMedicineAdapter(medicineItemDataList) { medicine ->
            navigateToEditPage(medicine)
        }
        binding.rvInactiveMedicine.adapter = inActiveMedicineAdapter
    }

    @RequiresApi(VERSION_CODES.O)
    private fun fetchInActiveMedicineData() {
        val call: Call<ResponseInActiveMedicine> = ServiceCreator.inActiveMedicineService.getInActiveMedicineList()

        call.enqueue(object : Callback<ResponseInActiveMedicine> {
            override fun onResponse(
                call: Call<ResponseInActiveMedicine>, response: Response<ResponseInActiveMedicine>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        if (it.result.isNullOrEmpty()) {
                            Log.i("데이터 전송 성공", "불러올 약물이 없습니다.")
                            _binding?.let { showEmptyState() }
                        } else {
                            _binding?.let { binding ->
                                showDataState()
                                inActiveMedicineAdapter.updateList(it.result)
                            }
                        }
                    } ?: Log.e("데이터 전송 실패", "서버 응답은 성공했지만 데이터가 없습니다.")
                }
            }

            override fun onFailure(call: Call<ResponseInActiveMedicine>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
                showEmptyState()
            }
        })
    }

    private fun showEmptyState() {
        _binding?.let {
            with(binding) {
                layoutNone.visibility = View.VISIBLE
                rvInactiveMedicine.visibility = View.INVISIBLE
            }
        }
    }

    private fun showDataState() {
        _binding?.let {
            with(binding) {
                layoutNone.visibility = View.INVISIBLE
                rvInactiveMedicine.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToEditPage(medicine: MedicineItemData) {/*
        val intent = Intent(requireContext(), InActiveMedicineEditActivity::class.java).apply {
            putExtra("scheduleId", medicine.scheduleId)
        }
        startActivity(intent)
         */
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            fetchInActiveMedicineData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}