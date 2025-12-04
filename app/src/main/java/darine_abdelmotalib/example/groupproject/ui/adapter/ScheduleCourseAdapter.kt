package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.data.api.CourseSection
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.UicomponentCourseCard02Binding

class ScheduleCourseAdapter(
    private val onCourseClick: (CourseSection) -> Unit
) : ListAdapter<CourseSection, ScheduleCourseAdapter.ScheduleCourseViewHolder>(ScheduleCourseDiffCallback()) {

    inner class ScheduleCourseViewHolder(private val binding: UicomponentCourseCard02Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: CourseSection) {
            binding.courseCode.text = convertKey(course.code)
            binding.courseTitle.text = course.courseOutline.title
            binding.courseCredits.text = "(${course.courseOutline.units ?: "3"})"
            binding.courseInstructor.text = course.instructor

            // Set lecture details from scheduleInfo if available
            val scheduleInfo = course.scheduleInfo
            if (scheduleInfo != null) {
                binding.courseLectureSection.text = "Lecture ${scheduleInfo.lectureSection}"
                binding.courseLectureTime.text = scheduleInfo.lectureSchedule
                binding.courseLectureLocation.text = scheduleInfo.lectureLocation
                
                // Set lab details if available
                if (scheduleInfo.labSection != null) {
                    binding.courseLabDetailsLayout.visibility = View.VISIBLE
                    binding.courseLabSection.text = "Lab ${scheduleInfo.labSection}"
                    binding.courseLabTime.text = scheduleInfo.labSchedule ?: "TBA"
                    binding.courseLabLocation.text = scheduleInfo.labLocation ?: "TBA"
                } else {
                    binding.courseLabDetailsLayout.visibility = View.GONE
                }
            } else {
                // Fallback to basic display
                binding.courseLectureSection.text = "Lecture ${course.section}"
                binding.courseLectureTime.text = "TBA"
                binding.courseLectureLocation.text = "TBA"
                binding.courseLabDetailsLayout.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }

        private fun convertKey(key: String): String {
            val codePair = UserProgressDb.unKey(key)
            return "${codePair.first.uppercase()} ${codePair.second}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleCourseViewHolder {
        val binding = UicomponentCourseCard02Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleCourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ScheduleCourseDiffCallback : DiffUtil.ItemCallback<CourseSection>() {
    override fun areItemsTheSame(oldItem: CourseSection, newItem: CourseSection) =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: CourseSection, newItem: CourseSection) =
        oldItem == newItem
}
