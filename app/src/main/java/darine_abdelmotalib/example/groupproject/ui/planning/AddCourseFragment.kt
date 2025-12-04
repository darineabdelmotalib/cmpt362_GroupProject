package darine_abdelmotalib.example.groupproject.ui.planning

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.RequirementCourse
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.FragmentAddCourseBinding

class AddCourseFragment : Fragment() {

    private var _binding: FragmentAddCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CourseSearchAdapter
    private var allCourses: List<RequirementCourse> = emptyList()

    // Semester info passed from previous fragment
    private var semesterKey: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get semester key from arguments
        semesterKey = arguments?.getString("semester_key") ?: ""
        
        // Display which semester we're adding to
        if (semesterKey.isNotEmpty()) {
            val termPair = UserProgressDb.unKey(semesterKey)
            val semesterLabel = "${termPair.first.replaceFirstChar { it.uppercase() }} ${termPair.second}"
            binding.semesterLabel.text = "Adding to: $semesterLabel"
        }

        // Load all available courses from the requirements database
        allCourses = CsRequirementsDb.getAllUniqueCourses()

        adapter = CourseSearchAdapter(allCourses) { selectedCourse ->
            // Navigate to course detail page with selected course info
            val bundle = Bundle().apply {
                putString("semester_key", semesterKey)
                putString("course_dept", selectedCourse.dept)
                putString("course_number", selectedCourse.number)
                putString("course_label", selectedCourse.label)
            }
            findNavController().navigate(R.id.action_addCourseFragment_to_courseDetailAddFragment, bundle)
        }

        binding.courseResults.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@AddCourseFragment.adapter
        }

        binding.courseResultCount.text = "${allCourses.size} Available Courses"

        // Search functionality
        fun performSearch(query: String) {
            val cleanQuery = query.trim().lowercase()

            val filteredList = if (cleanQuery.isEmpty()) {
                allCourses
            } else {
                allCourses.filter { course ->
                    val courseCode = "${course.dept} ${course.number}".lowercase()
                    val courseLabel = course.label.lowercase()
                    courseCode.contains(cleanQuery) || courseLabel.contains(cleanQuery)
                }
            }

            adapter.updateList(filteredList)

            binding.courseResultCount.text = when {
                filteredList.isEmpty() -> "No courses found"
                filteredList.size == 1 -> "1 Course Found"
                else -> "${filteredList.size} Courses Found"
            }
        }

        binding.inputSearchCourse.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonSearchCourse.setOnClickListener {
            performSearch(binding.inputSearchCourse.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter for course search results
    class CourseSearchAdapter(
        private var items: List<RequirementCourse>,
        private val onClick: (RequirementCourse) -> Unit
    ) : RecyclerView.Adapter<CourseSearchAdapter.VH>() {

        fun updateList(newItems: List<RequirementCourse>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_search_result, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            val courseCode = "${item.dept.uppercase()} ${item.number.uppercase()}"
            holder.code.text = courseCode
            
            // Extract title from label (format: "CMPT 362 - Title")
            val title = item.label.substringAfter(" - ", item.label)
            holder.title.text = title
            
            // Units will be fetched from API later, show placeholder
            holder.units.text = "3 units"
            
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val code: TextView = view.findViewById(R.id.course_code)
            val title: TextView = view.findViewById(R.id.course_title)
            val units: TextView = view.findViewById(R.id.course_units)
        }
    }
}

