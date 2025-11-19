package darine_abdelmotalib.example.groupproject.ui.requirements

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.databinding.FragmentProgramRequirementsBinding

class ProgramRequirementsFragment : Fragment() {

    private var _binding: FragmentProgramRequirementsBinding? = null
    private val binding get() = _binding!!

    private var lowerExpanded = true
    private var upperExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgramRequirementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.debugForward.setOnClickListener {
            findNavController()
                .navigate(R.id.action_programRequirementsFragment_to_courseInfoFragment)
        }
        binding.debugBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupExpandCollapse()
        fillRequirementRows()
        tintCheckboxes()
        wireRequirementClicks()
    }

    private fun setupExpandCollapse() {
        updateLowerSection()
        updateUpperSection()

        binding.lowerHeader.setOnClickListener {
            lowerExpanded = !lowerExpanded
            updateLowerSection()
        }
        binding.upperHeader.setOnClickListener {
            upperExpanded = !upperExpanded
            updateUpperSection()
        }
    }

    private fun updateLowerSection() {
        binding.lowerContent.visibility =
            if (lowerExpanded) View.VISIBLE else View.GONE
        binding.lowerExpandIcon.setImageResource(
            if (lowerExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun updateUpperSection() {
        binding.upperContent.visibility =
            if (upperExpanded) View.VISIBLE else View.GONE
        binding.upperExpandIcon.setImageResource(
            if (upperExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun fillRequirementRows() {
        CsRequirementsDb.allCourses().forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            val tv = row.findViewById<TextView>(R.id.text)
            tv.text = course.label
        }
    }

    private fun tintCheckboxes() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val red = requireContext().getColor(R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        val tint = ColorStateList(states, intArrayOf(red, gray))

        CsRequirementsDb.allCourses().forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            row.findViewById<MaterialCheckBox>(R.id.check)?.buttonTintList = tint
        }
    }

    private fun wireRequirementClicks() {
        CsRequirementsDb.allCourses().forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            row.setOnClickListener {
                val args = bundleOf(
                    "dept" to course.dept,
                    "courseNumber" to course.number,
                    "courseTitle" to course.label
                )
                findNavController().navigate(
                    R.id.action_programRequirementsFragment_to_courseInfoFragment,
                    args
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
