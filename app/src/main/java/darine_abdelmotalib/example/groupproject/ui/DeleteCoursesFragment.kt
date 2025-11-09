package darine_abdelmotalib.example.groupproject.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.R
import com.google.android.material.card.MaterialCardView

// placeholder for database - for testing purposes
data class Course(
    val title: String,
    val name: String,
    val instructor: String
)
class DeleteCoursesFragment : Fragment() {
    private lateinit var recyclerViewCourses: RecyclerView
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var coursesSelected: TextView
    private lateinit var adapter: RecyclerViewAdapter

    // data for testing purposes
    private val courses = arrayListOf(
        Course("CMPT 362 (3)", "Mobile Applications Programming and Design", "Xingdong Yang"),
        Course("CMPT 123 (3)", "Course name here", "Instructor name here")
    )
    private val selectedCourses = mutableListOf<Course>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewCourses = view.findViewById<RecyclerView>(R.id.recyclerViewCourses)
        cancelButton = view.findViewById<Button>(R.id.cancelButton)
        deleteButton = view.findViewById<Button>(R.id.deleteButton)
        toolbar = view.findViewById<Toolbar>(R.id.toolbarDelete)
        coursesSelected = view.findViewById<TextView>(R.id.coursesSelected)

        // setup recyclerview layout for courses
        adapter = RecyclerViewAdapter(courses, selectedCourses) { count ->
            if (count == 1) {
                coursesSelected.text = "1 course selected"
            }
            else {
                coursesSelected.text = "$count courses selected"
            }
        }
        recyclerViewCourses.layoutManager = LinearLayoutManager(requireActivity())
        recyclerViewCourses.adapter = adapter

        toolbar.setNavigationOnClickListener {
            // navigate back to prev fragment
            parentFragmentManager.popBackStack()
        }

        cancelButton.setOnClickListener {
            // unselect all courses
            selectedCourses.clear() // empty list - no courses selected
            adapter.notifyDataSetChanged() // refresh view
        }

        deleteButton.setOnClickListener {
            // delete from database
            parentFragmentManager.popBackStack() // close page after deletion
        }
    }
}

class RecyclerViewAdapter(
    private val courses: List<Course>,
    private val selectedCourses: MutableList<Course>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val courseCard = view.findViewById<MaterialCardView>(R.id.cvCourse)
        val courseTitle = view.findViewById<TextView>(R.id.cvCourseTitle)
        val courseName = view.findViewById<TextView>(R.id.cvCourseName)
        val courseInstructor = view.findViewById<TextView>(R.id.cvInstructor)

        // set course data for cardview
        fun bind(course: Course, selected: Boolean) {
            courseTitle.text = course.title
            courseName.text = course.name
            courseInstructor.text = course.instructor
            courseCard.isChecked = selected
            cardOnClickListener(course)
        }

        // handle course card selection/deselection
        private fun cardOnClickListener(course: Course) {
            courseCard.setOnClickListener {
                courseCard.isChecked = !courseCard.isChecked
                if (courseCard.isChecked == true) {
                    selectedCourses.add(course)
                }
                else {
                    selectedCourses.remove(course)
                }

                listener(selectedCourses.size) // update count in fragment
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_delete, parent, false)

        return ViewHolder(view)
    }

    // fill data for each course in the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isChecked = selectedCourses.contains(courses[position])
        holder.bind(courses[position], isChecked)
    }

    override fun getItemCount(): Int {
        return courses.size
    }
}