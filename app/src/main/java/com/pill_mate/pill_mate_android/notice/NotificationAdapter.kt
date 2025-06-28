package com.pill_mate.pill_mate_android.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemNotificationBinding
import com.pill_mate.pill_mate_android.notice.NotificationAdapter.NotificationViewHolder
import java.text.SimpleDateFormat
import java.util.*

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
                tvDate.text = formatDate(data.notifyDate)
                tvTime.text = formatTime(data.notifyTime)
                tvTitle.text = data.title
                itemLine.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE
                icState.visibility = if (data.notificationRead) View.INVISIBLE else View.VISIBLE
                if (data.notificationId == -1L) {
                    btnDetail.visibility = View.INVISIBLE
                    tvNotificationType.text = "내 활동"
                    tvNotificationType.setTextColor(ContextCompat.getColor(root.context, R.color.main_pink_1))
                } else {
                    btnDetail.visibility = View.VISIBLE
                    tvNotificationType.text = "공지"
                    tvNotificationType.setTextColor(ContextCompat.getColor(root.context, R.color.main_blue_1))
                }
            }

            // 아이템 전체 영역 클릭 시 상세 페이지 이동
            if (data.notificationId != -1L) {
                binding.root.setOnClickListener {
                    onDetailClickListener.invoke(data)
                }
            } else {
                binding.root.setOnClickListener(null) // 클릭 막기
            }
        }
    }

    private fun formatDate(input: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = inputFormat.parse(input)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            input
        }
    }

    private fun formatTime(input: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = inputFormat.parse(input)
            outputFormat.format(time!!)
        } catch (e: Exception) {
            input
        }
    }
}