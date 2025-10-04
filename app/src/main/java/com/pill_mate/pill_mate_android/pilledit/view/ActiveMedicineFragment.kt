package com.pill_mate.pill_mate_android.pilledit.view

import android.content.Intent
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
import com.pill_mate.pill_mate_android.hideLoading
import com.pill_mate.pill_mate_android.pilledit.model.MedicineItemData
import com.pill_mate.pill_mate_android.pilledit.model.ResponseActiveMedicine
import com.pill_mate.pill_mate_android.pilledit.view.adapter.ActiveMedicineAdapter
import com.pill_mate.pill_mate_android.showLoading
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
        activeMedicineAdapter = ActiveMedicineAdapter(medicineItemDataList, { medicine ->
            navigateToEditPage(medicine)
        }, { medicine ->
            patchStopMedicineData(medicine)
        })
        binding.rvActiveMedicine.adapter = activeMedicineAdapter
    }

    @RequiresApi(VERSION_CODES.O)
    private fun fetchActiveMedicineData() {
        _binding?.apply { showLoading() }
        val call: Call<ResponseActiveMedicine> = ServiceCreator.activeMedicineService.getActiveMedicineList()

        call.enqueue(object : Callback<ResponseActiveMedicine> {
            override fun onResponse(
                call: Call<ResponseActiveMedicine>, response: Response<ResponseActiveMedicine>
            ) {
                _binding?.apply { hideLoading() }
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        if (it.result.currentPillResponseList.isNullOrEmpty()) {
                            Log.i("데이터 전송 성공", "불러올 약물이 없습니다.")
                            _binding?.let { showEmptyState() }
                        } else {
                            _binding?.let { binding ->
                                showDataState()
                                activeMedicineAdapter.updateList(it.result.currentPillResponseList)

                                pillCount = it.result.pillCount
                                binding.tvPillCnt.text = "${pillCount}개"
                            }
                        }
                    } ?: Log.e("데이터 전송 실패", "서버 응답은 성공했지만 데이터가 없습니다.")
                }
            }

            override fun onFailure(call: Call<ResponseActiveMedicine>, t: Throwable) {
                _binding?.apply { hideLoading() }
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
                showEmptyState()
            }
        })
    }

    private fun showEmptyState() {
        _binding?.let {
            with(binding) {
                layoutNone.visibility = View.VISIBLE
                layoutPillCntBox.visibility = View.INVISIBLE
                rvActiveMedicine.visibility = View.INVISIBLE
            }
        }
    }

    private fun showDataState() {
        _binding?.let {
            with(binding) {
                layoutNone.visibility = View.INVISIBLE
                layoutPillCntBox.visibility = View.VISIBLE
                rvActiveMedicine.visibility = View.VISIBLE
            }
        }
    }

    private fun patchStopMedicineData(medicine: MedicineItemData) {
        val call: Call<Void> = ServiceCreator.stopMedicineService.patchStopMedicineData(medicine.scheduleId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("복용중지 성공", "해당 약물이 복용중지되었습니다.")
                    medicineItemDataList.remove(medicine)
                    activeMedicineAdapter.notifyDataSetChanged()

                    // 마지막 아이템을 복용중지했을 때
                    if (medicineItemDataList.isEmpty()) {
                        showEmptyState()
                    }
                } else {
                    Log.e("복용중지 실패", "복용중지 요청에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("네트워크 오류", "복용중지 요청 실패: ${t.message}")
            }
        })
    }

    private fun navigateToEditPage(medicine: MedicineItemData) {
        val intent = Intent(requireContext(), MedicineEditActivity::class.java).apply {
            putExtra("scheduleId", medicine.scheduleId)
        }
        startActivity(intent)
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            fetchActiveMedicineData()
        }
    }

    override fun onDestroyView() {
        _binding?.apply { hideLoading() }
        _binding = null
        super.onDestroyView()
    }
}