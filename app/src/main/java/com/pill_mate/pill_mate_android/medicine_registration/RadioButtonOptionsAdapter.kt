package com.pill_mate.pill_mate_android.medicine_registration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R

class RadioButtonOptionsAdapter(
    private val options: List<String>,
    initialSelection: String? = null,
    private val onOptionClick: (String?) -> Unit
) : RecyclerView.Adapter<RadioButtonOptionsAdapter.ViewHolder>() {

    // 반복적인 indexOf 호출을 피하기 위해 변수로 저장
    private var selectedPosition = initialSelection?.let { options.indexOf(it) }.takeIf { it != -1 }

    // ViewHolder 캐싱을 위한 맵 추가
    private val viewHolders = mutableMapOf<Int, ViewHolder>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_option)
        val radioButton: RadioButton = view.findViewById(R.id.rb_option)

        init {
            // 전체 아이템 클릭 선택 처리
            view.setOnClickListener {
                handleSelection(adapterPosition)
            }
        }

        // ViewHolder가 연결될 때 맵에 추가
        fun bind(position: Int) {
            viewHolders[position] = this
        }

        // ViewHolder가 재사용될 때 맵에서 제거
        fun unbind() {
            viewHolders.entries.removeIf { it.value == this }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radiobutton_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.textView.text = option

        // ViewHolder 맵에 추가
        holder.bind(position)

        // 상태 업데이트
        holder.radioButton.isChecked = position == selectedPosition

        // 애니메이션 깜빡임 방지
        holder.radioButton.jumpDrawablesToCurrentState()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // ViewHolder가 재사용될 때 맵에서 제거
        holder.unbind()
    }

    override fun getItemCount(): Int = options.size

    private fun handleSelection(position: Int) {
        // 유효하지 않은 위치이거나 이미 선택된 항목이면 무시
        if (position == RecyclerView.NO_POSITION || position == selectedPosition) return

        val previousPosition = selectedPosition
        selectedPosition = position

        // 즉시 UI 업데이트 - 이전 선택 항목
        previousPosition?.let { prevPos ->
            viewHolders[prevPos]?.let { holder ->
                holder.radioButton.isChecked = false
                holder.radioButton.jumpDrawablesToCurrentState()
            } ?: notifyItemChanged(prevPos) // 화면에 없는 경우 일반 갱신
        }

        // 즉시 UI 업데이트 - 새 선택 항목
        viewHolders[position]?.let { holder ->
            holder.radioButton.isChecked = true
            holder.radioButton.jumpDrawablesToCurrentState()
        } ?: notifyItemChanged(position) // 화면에 없는 경우 일반 갱신

        // 선택된 옵션으로 콜백 호출
        onOptionClick(options[position])
    }
}