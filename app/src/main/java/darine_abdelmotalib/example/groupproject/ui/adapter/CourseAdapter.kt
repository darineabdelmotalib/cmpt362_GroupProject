package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.data.api.CourseSection
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.UicomponentCourseCard01Binding

class CourseAdapter(
    private val onCourseClick: (CourseSection) -> Unit
) : ListAdapter<CourseSection, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    inner class CourseViewHolder(private val binding: UicomponentCourseCard01Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: CourseSection) {
            binding.courseCode.text = convertKey(course.code)
            binding.courseTitle.text = course.courseOutline.title
            binding.courseCredits.text = "(${course.courseOutline.units})"
            binding.courseInstructor.text = course.instructor

            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }

        fun convertKey(key: String): String{
            val codePair = UserProgressDb.unKey(key)
            return "${codePair.first.uppercase()} ${codePair.second}"
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

class CourseDiffCallback : DiffUtil.ItemCallback<CourseSection>() {
    override fun areItemsTheSame(oldItem: CourseSection, newItem: CourseSection) = oldItem.code == newItem.code
    override fun areContentsTheSame(oldItem: CourseSection, newItem: CourseSection) = oldItem == newItem
}