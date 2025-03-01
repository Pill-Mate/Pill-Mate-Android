package com.pill_mate.pill_mate_android.ui.pilledit

import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentActiveMedicineBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveMedicineFragment : Fragment() {

    private lateinit var activeMedicineAdapter: ActiveMedicineAdapter
    private var _binding: FragmentActiveMedicineBinding? = null
    private val binding get() = _binding!!

    private val medicineItemDataList = mutableListOf<MedicineItemData>()
    private var pillCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActiveMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        fetchActiveMedicineData()
    }

    private fun initAdapter() {
        activeMedicineAdapter = ActiveMedicineAdapter(medicineItemDataList) { medicine ->
            navigateToEditPage(medicine)
        }
        binding.rvActiveMedicine.adapter = activeMedicineAdapter
    }

    @RequiresApi(VERSION_CODES.O)
    private fun fetchActiveMedicineData() {
        val call: Call<ResponseActiveMedicine> = ServiceCreator.activeMedicineService.getActiveMedicineList()

        call.enqueue(object : Callback<ResponseActiveMedicine> {
            override fun onResponse(
                call: Call<ResponseActiveMedicine>, response: Response<ResponseActiveMedicine>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        if (it.result.currentPillResponseList.isNullOrEmpty()) {
                            Log.i("데이터 전송 성공", "불러올 약물이 없습니다.")
                            showEmptyState()
                        } else {
                            showDataState()

                            activeMedicineAdapter.updateList(it.result.currentPillResponseList)

                            pillCount = it.result.pillCount
                            binding.tvPillCnt.text = "${pillCount}개"

                        }
                    } ?: Log.e("데이터 전송 실패", "서버 응답은 성공했지만 데이터가 없습니다.")
                }
            }

            override fun onFailure(call: Call<ResponseActiveMedicine>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
                showEmptyState()
            }
        })
    }

    private fun showEmptyState() {
        with(binding) {
            layoutNone.visibility = View.VISIBLE
            layoutPillCntBox.visibility = View.INVISIBLE
            rvActiveMedicine.visibility = View.INVISIBLE
        }
    }

    private fun showDataState() {
        with(binding) {
            layoutNone.visibility = View.INVISIBLE
            layoutPillCntBox.visibility = View.VISIBLE
            rvActiveMedicine.visibility = View.VISIBLE
        }
    }

    private fun navigateToEditPage(medicine: MedicineItemData) {/*
        val intent = Intent(requireContext(), ActiveMedicineEditActivity::class.java).apply {
            putExtra("scheduleId", medicine.scheduleId)
        }
        startActivity(intent)
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}