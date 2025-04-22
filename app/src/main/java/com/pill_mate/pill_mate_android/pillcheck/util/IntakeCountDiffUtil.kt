package com.pill_mate.pill_mate_android.pillcheck.util

import androidx.recyclerview.widget.DiffUtil
import com.pill_mate.pill_mate_android.pillcheck.model.GroupedMedicine

class IntakeCountDiffUtil(
    private val oldList: List<GroupedMedicine>, private val newList: List<GroupedMedicine>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean { // 같은 복약 시간 그룹(intakeCount)인지 비교
        return oldList[oldItemPosition].intakeCount == newList[newItemPosition].intakeCount
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean { // 데이터 전체 내용 비교
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return if (oldItem != newItem) {
            newItem // 변경된 데이터만 전달
        } else {
            null
        }
    }
}