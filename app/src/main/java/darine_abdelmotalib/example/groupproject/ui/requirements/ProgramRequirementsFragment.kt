package darine_abdelmotalib.example.groupproject.ui.requirements

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.RequirementGroup
import darine_abdelmotalib.example.groupproject.data.db.RequirementCourse
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
        binding.debugForward.setOnClickListener {
            findNavController().navigate(
                R.id.action_programRequirementsFragment_to_courseInfoFragment
            )
        }
        binding.debugBack.setOnClickListener { findNavController().popBackStack() }

        setupLowerExpand()
        setupUpperExpand()
        fillLower()
        tintLower()
        renderUpper()
    }

    private fun setupLowerExpand() {
        updateLower()
        binding.lowerHeader.setOnClickListener {
            lowerExpanded = !lowerExpanded
            updateLower()
        }
    }

    private fun setupUpperExpand() {
        updateUpper()
        binding.upperHeader.setOnClickListener {
            upperExpanded = !upperExpanded
            updateUpper()
        }
    }

    private fun updateLower() {
        binding.lowerContent.visibility =
            if (lowerExpanded) View.VISIBLE else View.GONE
        binding.lowerExpandIcon.setImageResource(
            if (lowerExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun updateUpper() {
        binding.upperDynamicContainer.visibility =
            if (upperExpanded) View.VISIBLE else View.GONE
        binding.upperExpandIcon.setImageResource(
            if (upperExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun fillLower() {
        CsRequirementsDb.lowerDivision.forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            val tv = row.findViewById<TextView>(R.id.text)
            tv.text = course.label
            row.setOnClickListener { openCourse(course) }
        }
    }

    private fun tintLower() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val red = requireContext().getColor(R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        val tint = ColorStateList(states, intArrayOf(red, gray))

        CsRequirementsDb.lowerDivision.forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            row.findViewById<MaterialCheckBox>(R.id.check)?.buttonTintList = tint
        }
    }

    private fun renderUpper() {
        val container = binding.upperDynamicContainer
        container.removeAllViews()
        CsRequirementsDb.upperGroups.forEach { group ->
            when (group) {
                is RequirementGroup.CourseList -> addCourseList(container, group)
                is RequirementGroup.OrGroup -> addOrGroup(container, group)
            }
        }
    }

    private fun addCourseList(parent: LinearLayout, group: RequirementGroup.CourseList) {
        val header = layoutInflater.inflate(
            R.layout.requirement_group_header,
            parent,
            false
        )
        val title = header.findViewById<TextView>(R.id.reqGroupTitle)
        val icon = header.findViewById<ImageView>(R.id.reqGroupIcon)

        title.text = group.title
        var expanded = false

        val inner = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setPadding(0, 0, 0, 16)
        }

        group.courses.forEach { course ->
            inner.addView(createCourseRow(course))
        }

        header.setOnClickListener {
            expanded = !expanded
            inner.visibility = if (expanded) View.VISIBLE else View.GONE
            icon.setImageResource(
                if (expanded) R.drawable.ic_expand_less_24
                else R.drawable.ic_expand_more_24
            )
        }

        parent.addView(header)
        parent.addView(inner)
    }

    private fun addOrGroup(parent: LinearLayout, group: RequirementGroup.OrGroup) {
        val header = layoutInflater.inflate(
            R.layout.requirement_group_header,
            parent,
            false
        )
        val title = header.findViewById<TextView>(R.id.reqGroupTitle)
        val icon = header.findViewById<ImageView>(R.id.reqGroupIcon)

        title.text = group.title
        var expanded = false

        val outer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }

        group.options.forEach { option ->
            val subHeader = TextView(requireContext()).apply {
                text = option.title
                setPadding(0, 12, 0, 6)
                textSize = 15f
                setTextColor(requireContext().getColor(R.color.black))
            }
            outer.addView(subHeader)

            option.courses.forEach { course ->
                outer.addView(createCourseRow(course))
            }
        }

        header.setOnClickListener {
            expanded = !expanded
            outer.visibility = if (expanded) View.VISIBLE else View.GONE
            icon.setImageResource(
                if (expanded) R.drawable.ic_expand_less_24
                else R.drawable.ic_expand_more_24
            )
        }

        parent.addView(header)
        parent.addView(outer)
    }

    private fun createCourseRow(course: RequirementCourse): View {
        val row = layoutInflater.inflate(R.layout.row_requirement_red, null, false)
        val tv = row.findViewById<TextView>(R.id.text)
        tv.text = course.label

        val box = row.findViewById<MaterialCheckBox>(R.id.check)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val red = requireContext().getColor(R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        box.buttonTintList = ColorStateList(states, intArrayOf(red, gray))

        row.setOnClickListener { openCourse(course) }
        return row
    }

    private fun openCourse(course: RequirementCourse) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
