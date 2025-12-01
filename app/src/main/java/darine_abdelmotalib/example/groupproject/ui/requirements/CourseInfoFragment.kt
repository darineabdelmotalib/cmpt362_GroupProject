package darine_abdelmotalib.example.groupproject.ui.requirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.databinding.FragmentReqCourseInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseInfoFragment : Fragment() {

    private var _binding: FragmentReqCourseInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReqCourseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.topAppBar.toolbar
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val dept = arguments?.getString("dept") ?: "cmpt"
        val number = arguments?.getString("courseNumber") ?: "120"
        val fallbackTitle = arguments?.getString("courseTitle") ?: ""

        binding.codeCredits.text = "${dept.uppercase()} $number"
        binding.longTitle.text = fallbackTitle
        binding.descriptionText.text = "Loading..."

        loadFromApi(dept, number)
    }

    private fun loadFromApi(dept: String, number: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val outline = try {
                SfuCourseApi.fetchCourseOutline(dept, number)
            } catch (_: Exception) {
                null
            }

            if (!isAdded) return@launch

            withContext(Dispatchers.Main) {
                if (outline == null) {
                    binding.descriptionText.text =
                        "Unable to load course info from SFU API."
                    return@withContext
                }

                toolbar.title = outline.code

                binding.codeCredits.text = outline.code +
                        (outline.units?.let { " ($it)" } ?: "")

                binding.longTitle.text = outline.title
                binding.descriptionText.text =
                    outline.description ?: "No description available."

                setupPrereqs(outline.prerequisites)
            }
        }
    }

    private fun setupPrereqs(prereqRaw: String?) {
        val container = binding.prereqContainer
        container.removeAllViews()

        if (prereqRaw.isNullOrBlank()) return

        val regex = Regex("""[A-Z]{3,4}\s\d{3}[A-Z]?""")
        val matches = regex.findAll(prereqRaw).map { it.value }.toList()

        if (matches.isEmpty()) {
            addPrereqRow(prereqRaw)
        } else {
            matches.forEach { addPrereqRow(it) }
        }
    }

    private fun addPrereqRow(text: String) {
        val row = layoutInflater.inflate(
            R.layout.course_prereq,
            binding.prereqContainer,
            false
        )
        row.findViewById<TextView>(R.id.prereqText).text = text
        binding.prereqContainer.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
