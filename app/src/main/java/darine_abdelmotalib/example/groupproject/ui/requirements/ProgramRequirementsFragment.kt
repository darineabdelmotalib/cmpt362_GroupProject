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
import darine_abdelmotalib.example.groupproject.data.db.RequirementCourse
import darine_abdelmotalib.example.groupproject.data.db.RequirementGroup
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs
import darine_abdelmotalib.example.groupproject.databinding.FragmentProgramRequirementsBinding
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb

class ProgramRequirementsFragment : Fragment() {

    private var _binding: FragmentProgramRequirementsBinding? = null
    private val binding get() = _binding!!

    private var lowerExpanded = false
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
        setupLowerExpand()
        setupUpperExpand()

        fillLower()
        tintLower()

        renderUpper()
        refreshUpper()
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

    //lower division
    private fun fillLower() {
        CsRequirementsDb.lowerDivision.forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            val tv = row.findViewById<TextView>(R.id.text)
            val box = row.findViewById<MaterialCheckBox>(R.id.check)

            tv.text = course.label

            box.setOnCheckedChangeListener(null)
            box.isChecked = UserProgressDb.isCourseCompleted(requireContext(), course.dept, course.number)

            box.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    UserProgressDb.markCourseCompleted(requireContext(), course.dept, course.number)
                } else {
                    UserProgressDb.markCourseIncomplete(requireContext(), course.dept, course.number)
                }
            }

                //disabled for now
                // row.setOnClickListener { openCourse(course) }
            row.setOnClickListener { }
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
            val box = row.findViewById<MaterialCheckBox>(R.id.check)
            box.buttonTintList = tint
        }
    }

    //upper division
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
        val header = layoutInflater.inflate(R.layout.requirement_group_header, parent, false)

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
            inner.addView(createCourseRow(inner, course))
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
        val header = layoutInflater.inflate(R.layout.requirement_group_header, parent, false)
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
                textSize = 15f
                setPadding(0, 12, 0, 8)
            }
            outer.addView(subHeader)

            option.courses.forEach { course ->
                outer.addView(createCourseRow(outer, course))
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

    private fun createCourseRow(parent: LinearLayout, course: RequirementCourse): View {
        val row = layoutInflater.inflate(
            R.layout.row_requirement_red,
            parent,
            false
        )

        val tv = row.findViewById<TextView>(R.id.text)
        val box = row.findViewById<MaterialCheckBox>(R.id.check)

        tv.text = course.label

        //red tint
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val red = requireContext().getColor(R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        box.buttonTintList = ColorStateList(states, intArrayOf(red, gray))

        box.setOnCheckedChangeListener(null)
        box.isChecked = UserProgressDb.isCourseCompleted(requireContext(), course.dept, course.number)

        box.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                UserProgressDb.markCourseCompleted(requireContext(), course.dept, course.number)
            } else {
                UserProgressDb.markCourseIncomplete(requireContext(), course.dept, course.number)
            }
        }

        //navigation disabled for now
        row.setOnClickListener { }

        return row
    }

    //doesn't work
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

    override fun onResume() {
        super.onResume()
        refreshLower()
        refreshUpper()
    }

    private fun refreshLower() {
        CsRequirementsDb.lowerDivision.forEach { course ->
            val row = binding.root.findViewById<View>(course.rowId) ?: return@forEach
            val box = row.findViewById<MaterialCheckBox>(R.id.check)

            box.setOnCheckedChangeListener(null)
            box.isChecked = UserProgressDb.isCourseCompleted(requireContext(), course.dept, course.number)
            box.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    UserProgressDb.markCourseCompleted(requireContext(), course.dept, course.number)
                } else {
                    UserProgressDb.markCourseIncomplete(requireContext(), course.dept, course.number)
                }
            }
        }
    }

    private fun refreshUpper() {
        val wasExpanded = upperExpanded
        renderUpper()
        binding.upperDynamicContainer.visibility =
            if (wasExpanded) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
