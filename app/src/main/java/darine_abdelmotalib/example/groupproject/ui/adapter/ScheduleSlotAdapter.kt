package darine_abdelmotalib.example.groupproject.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.databinding.ItemScheduleSlotBinding

data class ScheduleSlot(
    val day: String,
    val startTime: String,
    val endTime: String,
    val courseCode: String,
    val color: Int
)

class ScheduleSlotAdapter : ListAdapter<ScheduleSlot, ScheduleSlotAdapter.ScheduleSlotViewHolder>(ScheduleSlotDiffCallback()) {

    inner class ScheduleSlotViewHolder(private val binding: ItemScheduleSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: ScheduleSlot) {
            binding.scheduleTime.text = "${slot.day} ${slot.startTime} - ${slot.endTime}"
            binding.scheduleCourse.text = slot.courseCode
            binding.scheduleSlotContainer.setBackgroundColor(slot.color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleSlotViewHolder {
        val binding = ItemScheduleSlotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleSlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ScheduleSlotDiffCallback : DiffUtil.ItemCallback<ScheduleSlot>() {
    override fun areItemsTheSame(oldItem: ScheduleSlot, newItem: ScheduleSlot) =
        oldItem.day == newItem.day && oldItem.startTime == newItem.startTime && oldItem.courseCode == newItem.courseCode

    override fun areContentsTheSame(oldItem: ScheduleSlot, newItem: ScheduleSlot) =
        oldItem == newItem
}

