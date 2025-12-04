package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.data.api.SemesterItem
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.ComponentSemesterPlanBinding


class SemesterAdapter(
    private val onEditSemesterButtonClick: (SemesterItem) -> Unit,
    private val onViewSemesterButtonClick: (SemesterItem) -> Unit,
    private val onInfoButtonClick: (SemesterItem) -> Unit
) : ListAdapter<SemesterItem, SemesterAdapter.SemesterViewHolder>(SemesterDiffCallback()) {

    inner class SemesterViewHolder(private val binding: ComponentSemesterPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(semester: SemesterItem) {
            binding.semesterTerm.text = convertKey(semester.term)
            binding.semesterTotalCredits.text = "${semester.totalUnits} total units"

            // Semester Info button
            binding.buttonSemesterInfo.setOnClickListener {
                onInfoButtonClick(semester)
            }

            // Edit Semester button
            binding.buttonEditSchedule.setOnClickListener {
                onEditSemesterButtonClick(semester)
            }

            // View Semester button
            binding.buttonViewSchedule.setOnClickListener {
                onViewSemesterButtonClick(semester)
            }
        }

        fun convertKey(key: String): String{
            val termPair = UserProgressDb.unKey(key)
            return "${termPair.first.replaceFirstChar{c -> c.uppercase()}} ${termPair.second}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterViewHolder {
        val binding = ComponentSemesterPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SemesterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SemesterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SemesterDiffCallback : DiffUtil.ItemCallback<SemesterItem>() {
    override fun areItemsTheSame(oldItem: SemesterItem, newItem: SemesterItem) = oldItem.term == newItem.term
    override fun areContentsTheSame(oldItem: SemesterItem, newItem: SemesterItem) = oldItem == newItem
}
