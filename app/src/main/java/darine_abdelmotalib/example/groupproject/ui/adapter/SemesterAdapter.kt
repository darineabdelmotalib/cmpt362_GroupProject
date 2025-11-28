package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.databinding.ComponentSemesterPlanBinding



class SemesterAdapter(
    private val onEditSemesterButtonClick: (SemItem) -> Unit,
    private val onViewSemesterButtonClick: (SemItem) -> Unit,
    private val onCourseClick: (CourseItem) -> Unit
) : ListAdapter<SemItem, SemesterAdapter.SemesterViewHolder>(SemesterDiffCallback()) {

    inner class SemesterViewHolder(private val binding: ComponentSemesterPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(semester: SemItem) {
            binding.semesterTerm.text = semester.term
            binding.semesterTotalCredits.text = semester.credits

            // Edit Semester button
            binding.buttonEditSchedule.setOnClickListener {
                onEditSemesterButtonClick(semester)
            }

            // View Semester button
            binding.buttonViewSchedule.setOnClickListener {
                onViewSemesterButtonClick(semester)
            }

            val courseAdapter = CourseAdapter(onCourseClick)
            binding.semesterCourseList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = courseAdapter
            }

            courseAdapter.submitList(semester.courseList)
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

class SemesterDiffCallback : DiffUtil.ItemCallback<SemItem>() {
    override fun areItemsTheSame(oldItem: SemItem, newItem: SemItem) = oldItem.term == newItem.term
    override fun areContentsTheSame(oldItem: SemItem, newItem: SemItem) = oldItem == newItem
}
