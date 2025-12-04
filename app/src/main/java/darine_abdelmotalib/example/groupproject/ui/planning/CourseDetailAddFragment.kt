package darine_abdelmotalib.example.groupproject.ui.planning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.api.SectionInfo
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs
import darine_abdelmotalib.example.groupproject.databinding.FragmentCourseDetailAddBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailAddFragment : Fragment() {

    private var _binding: FragmentCourseDetailAddBinding? = null
    private val binding get() = _binding!!

    private var semesterKey: String = ""
    private var courseDept: String = ""
    private var courseNumber: String = ""
    private var courseLabel: String = ""
    
    private var selectedLecture: SectionInfo? = null
    private var selectedLab: SectionInfo? = null
    private var courseUnits: String = "3"
    private var courseTitle: String = ""
    
    private var lectureSections: List<SectionInfo> = emptyList()
    private var labSections: List<SectionInfo> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get course info from arguments
        semesterKey = arguments?.getString("semester_key") ?: ""
        courseDept = arguments?.getString("course_dept") ?: ""
        courseNumber = arguments?.getString("course_number") ?: ""
        courseLabel = arguments?.getString("course_label") ?: ""

        // Set toolbar title
        val termPair = if (semesterKey.isNotEmpty()) UserProgressDb.unKey(semesterKey) else Pair("", "")
        val semesterLabel = "${termPair.first.replaceFirstChar { it.uppercase() }} ${termPair.second}"
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Add ${courseDept.uppercase()}${courseNumber} to $semesterLabel"

        // Display basic info immediately
        val courseCode = "${courseDept.uppercase()}${courseNumber}"
        courseTitle = courseLabel.substringAfter(" - ", courseLabel)
        
        binding.courseCode.text = "$courseCode (3)"
        binding.courseTitle.text = courseTitle

        // Show loading state
        binding.lecture1Section.text = "Loading..."
        binding.lecture1Instructor.text = ""
        binding.lecture1Time.text = ""
        binding.lecture1Location.text = ""

        // Cancel button - go back to add course page
        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // Add Course button - add course to semester and go to semester plan
        binding.buttonAddCourse.setOnClickListener {
            addCourseToSemester()
        }

        // Fetch detailed course info from API
        fetchCourseDetails()
    }

    private fun setupSectionSelection() {
        // Setup lecture selection
        binding.lectureOption1.isChecked = true
        if (lectureSections.isNotEmpty()) {
            selectedLecture = lectureSections[0]
        }
        
        binding.lectureRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLecture = when (checkedId) {
                R.id.lecture_option_1 -> lectureSections.getOrNull(0)
                R.id.lecture_option_2 -> lectureSections.getOrNull(1)
                else -> lectureSections.getOrNull(0)
            }
            updateSelectedSectionSummary()
        }

        // Setup lab selection
        if (labSections.isNotEmpty()) {
            binding.labOption1.isChecked = true
            selectedLab = labSections[0]
            binding.labSectionLabel.visibility = View.VISIBLE
            binding.labRadioGroup.visibility = View.VISIBLE
            binding.lab1Card.visibility = View.VISIBLE
        } else {
            binding.labSectionLabel.visibility = View.GONE
            binding.labRadioGroup.visibility = View.GONE
            binding.lab1Card.visibility = View.GONE
        }
        
        binding.labRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLab = when (checkedId) {
                R.id.lab_option_1 -> labSections.getOrNull(0)
                else -> labSections.getOrNull(0)
            }
            updateSelectedSectionSummary()
        }

        // Make cards clickable
        binding.lecture1Card.setOnClickListener {
            binding.lectureOption1.isChecked = true
        }
        binding.lecture2Card.setOnClickListener {
            binding.lectureOption2.isChecked = true
        }
        binding.lab1Card.setOnClickListener {
            binding.labOption1.isChecked = true
        }
    }

    private fun updateSelectedSectionSummary() {
        val courseCode = "${courseDept.uppercase()} ${courseNumber}"
        binding.selectedCourseCode.text = "$courseCode ($courseUnits)"
        binding.selectedCourseTitle.text = courseTitle
        binding.selectedInstructor.text = selectedLecture?.instructor ?: "TBA"
        
        binding.selectedLectureSection.text = "Lecture ${selectedLecture?.sectionCode ?: "TBA"}"
        binding.selectedLectureTime.text = selectedLecture?.schedule ?: "TBA"
        binding.selectedLectureLocation.text = selectedLecture?.location ?: "TBA"
        
        if (selectedLab != null) {
            binding.selectedLabSection.visibility = View.VISIBLE
            binding.selectedLabSection.text = "Lab ${selectedLab?.sectionCode ?: ""}"
            binding.selectedLabTime.text = selectedLab?.schedule ?: "TBA"
            binding.selectedLabLocation.text = selectedLab?.location ?: "TBA"
        } else {
            binding.selectedLabSection.visibility = View.GONE
            binding.selectedLabTime.text = ""
            binding.selectedLabLocation.text = ""
        }
    }

    private fun fetchCourseDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val outline = withContext(Dispatchers.IO) {
                    SfuCourseApi.fetchCourseOutline(courseDept, courseNumber)
                }
                
                // Update UI with fetched data
                courseTitle = outline.title
                courseUnits = outline.units ?: "3"
                
                binding.courseCode.text = "${courseDept.uppercase()}${courseNumber} ($courseUnits)"
                binding.courseTitle.text = courseTitle
                
                // Separate lecture and lab sections
                lectureSections = outline.sections.filter { it.sectionType == "LEC" }
                labSections = outline.sections.filter { it.sectionType == "LAB" || it.sectionType == "TUT" }
                
                // Update lecture cards
                if (lectureSections.isNotEmpty()) {
                    val lecture1 = lectureSections[0]
                    binding.lecture1Section.text = "Lecture ${lecture1.sectionCode}"
                    binding.lecture1Instructor.text = lecture1.instructor
                    binding.lecture1Time.text = lecture1.schedule
                    binding.lecture1Location.text = lecture1.location
                    
                    if (lectureSections.size > 1) {
                        val lecture2 = lectureSections[1]
                        binding.lecture2Card.visibility = View.VISIBLE
                        binding.lecture2Section.text = "Lecture ${lecture2.sectionCode}"
                        binding.lecture2Instructor.text = lecture2.instructor
                        binding.lecture2Time.text = lecture2.schedule
                        binding.lecture2Location.text = lecture2.location
                    } else {
                        binding.lecture2Card.visibility = View.GONE
                    }
                }
                
                // Update lab cards
                if (labSections.isNotEmpty()) {
                    val lab1 = labSections[0]
                    binding.lab1Section.text = "Lab ${lab1.sectionCode}"
                    binding.lab1Instructor.text = lab1.instructor
                    binding.lab1Time.text = lab1.schedule
                    binding.lab1Location.text = lab1.location
                    binding.labSectionLabel.visibility = View.VISIBLE
                    binding.lab1Card.visibility = View.VISIBLE
                } else {
                    binding.labSectionLabel.visibility = View.GONE
                    binding.lab1Card.visibility = View.GONE
                }
                
                // Setup selection handlers
                setupSectionSelection()
                updateSelectedSectionSummary()
                
            } catch (e: Exception) {
                // Use fallback data if API fails
                binding.lecture1Section.text = "Lecture D100"
                binding.lecture1Instructor.text = "TBA"
                binding.lecture1Time.text = "TBA"
                binding.lecture1Location.text = "TBA"
                binding.lecture2Card.visibility = View.GONE
                binding.labSectionLabel.visibility = View.GONE
                binding.lab1Card.visibility = View.GONE
                
                setupSectionSelection()
                updateSelectedSectionSummary()
            }
        }
    }

    private fun addCourseToSemester() {
        if (semesterKey.isEmpty()) {
            Toast.makeText(context, "Error: No semester selected", Toast.LENGTH_SHORT).show()
            return
        }

        val context = requireContext()
        
        // Add course to the semester using CoursePrefs
        val courseKey = UserProgressDb.makeKey(courseDept, courseNumber)
        val success = CoursePrefs.addCourseToSem(context, semesterKey, courseKey)

        if (success) {
            Toast.makeText(context, "Course added successfully!", Toast.LENGTH_SHORT).show()
            // Navigate back to semester plan list
            findNavController().navigate(R.id.action_courseDetailAddFragment_to_semesterPlanListFragment)
        } else {
            Toast.makeText(context, "Course is already in this semester", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
