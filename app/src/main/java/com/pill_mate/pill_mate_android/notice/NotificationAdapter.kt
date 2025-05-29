package com.pill_mate.pill_mate_android.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.databinding.ItemNotificationBinding
import com.pill_mate.pill_mate_android.notice.NotificationAdapter.NotificationViewHolder

class NotificationAdapter(
    private val notificationList: MutableList<ResponseNotificationItem>,
    private val onDetailClickListener: (ResponseNotificationItem) -> Unit,
) : RecyclerView.Adapter<NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val isLastItem = position == notificationList.size - 1
        holder.onBind(notificationList[position], isLastItem)
    }

    override fun getItemCount(): Int = notificationList.size

    fun updateList(newList: List<ResponseNotificationItem>) {
        notificationList.clear()
        notificationList.addAll(newList)
        notifyDataSetChanged() // UI 갱신
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: ResponseNotificationItem, isLastItem: Boolean) {
            with(binding) {
                tvDate.text = data.notifyDate
                tvTime.text = data.notifyTime
                tvTitle.text = data.title
                itemLine.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE
            }

            // 아이템 전체 영역 클릭 시 상세 페이지 이동
            binding.root.setOnClickListener {
                onDetailClickListener.invoke(data)
            }
        }
    }
}