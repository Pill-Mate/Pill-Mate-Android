package com.example.pill_mate_android.medicine_registration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R

class ConfirmationDataAdapter(
    private var dataList: List<RegistrationData>
) : RecyclerView.Adapter<ConfirmationDataAdapter.ViewHolder>() {

    // ViewHolder 클래스 정의
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView: TextView = view.findViewById(R.id.tv_label)
        val dataTextView: TextView = view.findViewById(R.id.tv_selected_data)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.registration_selected_data_item, parent, false)
        return ViewHolder(view)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.labelTextView.text = item.label
        holder.dataTextView.text = item.data
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = dataList.size

    // 데이터 업데이트 메서드
    fun updateData(newDataList: List<RegistrationData>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}