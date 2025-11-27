package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.databinding.UicomponentCourseCard01Binding

class CourseAdapter(
    private val onCourseClick: (CourseItem) -> Unit
) : ListAdapter<CourseItem, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    inner class CourseViewHolder(private val binding: UicomponentCourseCard01Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: CourseItem) {
            binding.courseCode.text = course.code
            binding.courseTitle.text = course.title
            binding.courseCredits.text = course.credits
            binding.courseInstructor.text = course.instructor

            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = UicomponentCourseCard01Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CourseDiffCallback : DiffUtil.ItemCallback<CourseItem>() {
    override fun areItemsTheSame(oldItem: CourseItem, newItem: CourseItem) = oldItem.code == newItem.code
    override fun areContentsTheSame(oldItem: CourseItem, newItem: CourseItem) = oldItem == newItem
}