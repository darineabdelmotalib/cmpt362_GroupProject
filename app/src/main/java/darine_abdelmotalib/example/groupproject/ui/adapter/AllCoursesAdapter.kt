package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.databinding.ItemApiCourseBinding
import darine_abdelmotalib.example.groupproject.databinding.ItemDepartmentHeaderBinding

data class AllCoursesItem(
    val departmentCode: String,
    val departmentName: String,
    val courseNumber: String?,
    val courseTitle: String?,
    val isHeader: Boolean
)

class AllCoursesAdapter(
    private val onCourseClick: (deptCode: String, courseNumber: String, courseTitle: String?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<AllCoursesItem>()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_COURSE = 1
    }

    fun updateItems(newItems: List<AllCoursesItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isHeader) VIEW_TYPE_HEADER else VIEW_TYPE_COURSE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemDepartmentHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HeaderViewHolder(binding)
        } else {
            val binding = ItemApiCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CourseViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is HeaderViewHolder) {
            holder.bind(item)
        } else if (holder is CourseViewHolder) {
            holder.bind(item)
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemDepartmentHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllCoursesItem) {
            binding.deptName.text = item.departmentName
        }
    }

    inner class CourseViewHolder(
        private val binding: ItemApiCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllCoursesItem) {
            val dept = item.departmentName
            val number = item.courseNumber ?: ""
            val title = item.courseTitle ?: ""

            binding.courseCode.text = "$dept $number"
            binding.courseTitle.text = title

            binding.root.setOnClickListener {
                if (!number.isNullOrBlank()) {
                    onCourseClick(item.departmentCode, number, title)
                }
            }
        }
    }
}
