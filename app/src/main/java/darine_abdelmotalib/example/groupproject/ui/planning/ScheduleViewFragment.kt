package darine_abdelmotalib.example.groupproject.ui.planning

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.api.CourseSection
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs
import darine_abdelmotalib.example.groupproject.databinding.FragmentScheduleViewBinding
import darine_abdelmotalib.example.groupproject.ui.adapter.ScheduleCourseAdapter
import darine_abdelmotalib.example.groupproject.ui.adapter.ScheduleSlot
import darine_abdelmotalib.example.groupproject.ui.adapter.ScheduleSlotAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewFragment : Fragment() {
    private var _binding: FragmentScheduleViewBinding? = null
    private val binding get() = _binding!!

    private var semesterKey: String = ""
    private lateinit var courseAdapter: ScheduleCourseAdapter
    private lateinit var scheduleSlotAdapter: ScheduleSlotAdapter

    // Course colors for schedule display
    private val courseColors = listOf(
        Color.parseColor("#B3E5FC"), // Light Blue
        Color.parseColor("#C8E6C9"), // Light Green
        Color.parseColor("#B3E5FC"), // Light Blue
        Color.parseColor("#FFE0B2"), // Light Orange/Cream
        Color.parseColor("#E1BEE7"), // Light Purple
        Color.parseColor("#FFCDD2"), // Light Red
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get semester key from arguments
        semesterKey = arguments?.getString("semester_key") ?: ""

        // Display semester name
        if (semesterKey.isNotEmpty()) {
            val termPair = UserProgressDb.unKey(semesterKey)
            val semesterLabel = "${termPair.first.replaceFirstChar { it.uppercase() }} ${termPair.second}"
            binding.semesterName.text = semesterLabel
        }

        // Setup schedule slot adapter
        scheduleSlotAdapter = ScheduleSlotAdapter()
        binding.scheduleRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleSlotAdapter
        }

        // Setup course adapter
        courseAdapter = ScheduleCourseAdapter { course ->
            // Handle course click if needed
        }

        binding.coursesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courseAdapter
        }

        // Setup collapsible sections
        setupCollapsibleSections()

        // Load courses for this semester
        loadSemesterCourses()
    }

    private fun setupCollapsibleSections() {
        var scheduleExpanded = true
        var coursesExpanded = true

        binding.scheduleHeaderLayout.setOnClickListener {
            scheduleExpanded = !scheduleExpanded
            binding.scheduleContentArea.visibility = if (scheduleExpanded) View.VISIBLE else View.GONE
            binding.toggleSchedule.rotation = if (scheduleExpanded) 0f else 180f
        }

        binding.coursesHeaderLayout.setOnClickListener {
            coursesExpanded = !coursesExpanded
            binding.coursesRecyclerView.visibility = if (coursesExpanded) View.VISIBLE else View.GONE
            binding.toggleCourses.rotation = if (coursesExpanded) 0f else 180f
        }
    }

    private fun loadSemesterCourses() {
        if (semesterKey.isEmpty()) {
            showEmptyState()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val courses = withContext(Dispatchers.IO) {
                    val courseKeys = CoursePrefs.getCourseListFromSem(requireContext(), semesterKey)
                    if (courseKeys.isEmpty() || (courseKeys.size == 1 && courseKeys[0].isEmpty())) {
                        emptyList()
                    } else {
                        courseKeys.filter { it.isNotEmpty() && it != CoursePrefs.ERROR_STRING }
                            .map { key ->
                                UserProgressDb.returnCourseSectionItem(key)
                            }
                    }
                }

                if (courses.isEmpty()) {
                    showEmptyState()
                } else {
                    showCourses(courses)
                }
            } catch (e: Exception) {
                showEmptyState()
            }
        }
    }

    private fun showEmptyState() {
        binding.emptyCoursesMessage.visibility = View.VISIBLE
        binding.coursesRecyclerView.visibility = View.GONE
        binding.scheduleContentArea.visibility = View.GONE
        binding.emptyScheduleMessage.visibility = View.VISIBLE
        binding.semesterUnitsTaken.text = "(0) units"
    }

    private fun showCourses(courses: List<CourseSection>) {
        binding.emptyCoursesMessage.visibility = View.GONE
        binding.coursesRecyclerView.visibility = View.VISIBLE
        binding.scheduleContentArea.visibility = View.VISIBLE
        binding.emptyScheduleMessage.visibility = View.GONE

        // Calculate total units
        var totalUnits = 0
        courses.forEach { course ->
            course.courseOutline.units?.toIntOrNull()?.let { totalUnits += it }
        }
        binding.semesterUnitsTaken.text = "($totalUnits) units"

        // Update course list
        courseAdapter.submitList(courses)

        // Generate schedule slots from courses
        val scheduleSlots = generateScheduleSlots(courses)
        scheduleSlotAdapter.submitList(scheduleSlots)
    }

    private fun generateScheduleSlots(courses: List<CourseSection>): List<ScheduleSlot> {
        val slots = mutableListOf<ScheduleSlot>()

        courses.forEachIndexed { index, course ->
            val color = courseColors[index % courseColors.size]
            val courseCode = course.courseOutline.code
            
            val scheduleInfo = course.scheduleInfo
            if (scheduleInfo != null) {
                // Parse the real schedule data
                val lectureSlots = parseScheduleString(scheduleInfo.lectureSchedule, courseCode, color)
                slots.addAll(lectureSlots)
                
                // Add lab slots if available
                if (scheduleInfo.labSchedule != null) {
                    val labSlots = parseScheduleString(scheduleInfo.labSchedule, "$courseCode Lab", color)
                    slots.addAll(labSlots)
                }
            } else {
                // Fallback: use placeholder data
                val dayMapping = listOf("MON", "TUE", "WED", "THU", "FRI")
                slots.add(ScheduleSlot(
                    day = dayMapping[index % dayMapping.size],
                    startTime = "10:30",
                    endTime = "11:20",
                    courseCode = courseCode,
                    color = color
                ))
            }
        }

        // Sort by day order then by time
        val dayOrder = mapOf("MON" to 0, "TUE" to 1, "WED" to 2, "THU" to 3, "FRI" to 4, "SAT" to 5, "SUN" to 6)
        return slots.sortedWith(compareBy({ dayOrder[it.day] ?: 99 }, { it.startTime }))
    }
    
    private fun parseScheduleString(schedule: String, courseCode: String, color: Int): List<ScheduleSlot> {
        val slots = mutableListOf<ScheduleSlot>()
        
        if (schedule.isBlank() || schedule == "TBA") {
            return slots
        }
        
        // Parse schedule string like "Mon, Wed, Fri 10:30 - 11:20" or "Mo, We 14:30 - 15:20"
        val dayMappings = mapOf(
            "mo" to "MON", "mon" to "MON", "monday" to "MON",
            "tu" to "TUE", "tue" to "TUE", "tuesday" to "TUE",
            "we" to "WED", "wed" to "WED", "wednesday" to "WED",
            "th" to "THU", "thu" to "THU", "thursday" to "THU",
            "fr" to "FRI", "fri" to "FRI", "friday" to "FRI",
            "sa" to "SAT", "sat" to "SAT", "saturday" to "SAT",
            "su" to "SUN", "sun" to "SUN", "sunday" to "SUN"
        )
        
        // Extract time portion (look for time pattern like "10:30" or "10:30 - 11:20")
        val timeRegex = Regex("(\\d{1,2}:\\d{2})\\s*[-–]?\\s*(\\d{1,2}:\\d{2})?")
        val timeMatch = timeRegex.find(schedule)
        val startTime = timeMatch?.groupValues?.getOrNull(1) ?: "TBA"
        val endTime = timeMatch?.groupValues?.getOrNull(2) ?: ""
        
        // Extract days
        val scheduleLower = schedule.lowercase()
        for ((key, value) in dayMappings) {
            if (scheduleLower.contains(key)) {
                // Avoid duplicates (e.g., "thu" and "th" both matching)
                if (!slots.any { it.day == value && it.startTime == startTime }) {
                    slots.add(ScheduleSlot(
                        day = value,
                        startTime = startTime,
                        endTime = endTime,
                        courseCode = courseCode,
                        color = color
                    ))
                }
            }
        }
        
        return slots
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
