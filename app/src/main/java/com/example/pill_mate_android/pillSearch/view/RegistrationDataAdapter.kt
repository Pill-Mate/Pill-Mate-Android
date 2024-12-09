package com.example.pill_mate_android.pillSearch.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R

class RegistrationDataAdapter(private var dataList: List<RegistrationData>) :
    RecyclerView.Adapter<RegistrationDataAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_label)
        val data: TextView = view.findViewById(R.id.tv_selected_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.registration_selected_data_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.label.text = item.label
        holder.data.text = item.data
        Log.d("AdapterBinding", "Binding data at position $position: Label=${item.label}, Data=${item.data}")
    }

    override fun getItemCount(): Int {
        Log.d("AdapterItemCount", "Item count: ${dataList.size}")
        return dataList.size
    }

    fun updateData(newData: List<RegistrationData>) {
        this.dataList = newData
        Log.d("AdapterUpdate", "Updating data. New size: ${newData.size}")
        notifyDataSetChanged()
    }
}

data class RegistrationData(val label: String, var data: String)