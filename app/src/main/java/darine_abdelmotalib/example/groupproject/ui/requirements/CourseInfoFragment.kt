package darine_abdelmotalib.example.groupproject.ui.requirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.databinding.FragmentCourseInfoBinding

class CourseInfoFragment : Fragment() {

    private var _binding: FragmentCourseInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.debugForward.setOnClickListener {
            findNavController()
                .navigate(R.id.action_courseInfoFragment_to_programRequirementsFragment)
        }
        binding.debugBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val dept = arguments?.getString("dept") ?: "cmpt"
        val number = arguments?.getString("courseNumber") ?: "120"
        val titleFromArgs = arguments?.getString("courseTitle") ?: ""

        binding.codeCredits.text = "${dept.uppercase()} $number"
        binding.longTitle.text =
            titleFromArgs.ifBlank { getString(R.string.placeholder_course_long_title) }
        binding.descriptionText.text = "Loading from SFU API…"

        binding.topAppBar.toolbar.title = "${dept.uppercase()} $number"

        loadFromApi(dept, number)
    }

    private fun loadFromApi(dept: String, number: String) {
        Thread {
            val outline = try {
                SfuCourseApi.fetchCourseOutline(dept, number)
            } catch (_: Exception) {
                null
            }

            if (!isAdded) return@Thread

            requireActivity().runOnUiThread {
                if (outline == null) {
                    binding.descriptionText.text =
                        getString(R.string.placeholder_description) +
                                "\n\n(Unable to load SFU API data for $dept $number.)"
                    return@runOnUiThread
                }

                val unitsPart = outline.units?.let { " ($it)" } ?: ""
                binding.codeCredits.text = outline.code + unitsPart
                binding.longTitle.text = outline.title
                binding.topAppBar.toolbar.title = outline.code

                setupPrereqCheckboxes(outline.prerequisites)
                binding.descriptionText.text =
                    outline.description.ifBlank {
                        getString(R.string.placeholder_description)
                    }
            }
        }.start()
    }

    /** Build one checkbox row per course in the prereq string. */
    private fun setupPrereqCheckboxes(prereqRaw: String?) {
        val container = binding.prereqContainer
        container.removeAllViews()

        if (prereqRaw.isNullOrBlank()) {
            return
        }

        val courseRegex = Regex("""[A-Z]{3,4}\s\d{3}[A-Z]?""")
        val matches = courseRegex.findAll(prereqRaw).map { it.value }.toList()

        if (matches.isEmpty()) {
            addPrereqRow(prereqRaw)
            return
        }

        matches.forEach { courseCode ->
            addPrereqRow(courseCode)
        }
    }

    private fun addPrereqRow(text: String) {
        val row = layoutInflater.inflate(
            R.layout.course_prereq,
            binding.prereqContainer,
            false
        )

        val tv = row.findViewById<TextView>(R.id.prereqText)
        tv.text = text

        binding.prereqContainer.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
