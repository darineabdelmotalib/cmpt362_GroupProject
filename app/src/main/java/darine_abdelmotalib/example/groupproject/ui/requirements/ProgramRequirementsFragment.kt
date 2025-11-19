package darine_abdelmotalib.example.groupproject.ui.requirements

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import darine_abdelmotalib.example.groupproject.R
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
        val root: View = binding.root

        val toolbar = binding.topAppBar.toolbar
        toolbar.title = getString(R.string.title_cs_major)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

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

        fillRowTexts()
        applyCheckboxTint()
        wireCourseRowClicks()

        /* -- DEBUG BUTTONS -- */
        binding.debugForward.setOnClickListener {
            findNavController()
                .navigate(R.id.action_programRequirementsFragment_to_courseInfoFragment)
        }
        binding.debugBack.setOnClickListener {
            findNavController()
                .navigate(R.id.action_programRequirementsFragment_to_chooseProgramFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    private fun updateLowerSection() {
        binding.lowerContent.visibility = if (lowerExpanded) View.VISIBLE else View.GONE
        binding.lowerExpandIcon.setImageResource(
            if (lowerExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun updateUpperSection() {
        binding.upperContent.visibility = if (upperExpanded) View.VISIBLE else View.GONE
        binding.upperExpandIcon.setImageResource(
            if (upperExpanded) R.drawable.ic_expand_less_24
            else R.drawable.ic_expand_more_24
        )
    }

    private fun fillRowTexts() {
        val ids = intArrayOf(
            R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5,
            R.id.row6, R.id.row7, R.id.row8, R.id.row9, R.id.row10,
            R.id.row_ud_1
        )

        ids.forEach { id ->
            val row = binding.root.findViewById<View>(id)
            val tv = row?.findViewById<TextView>(R.id.text) ?: return@forEach
            val text = row.tag?.toString() ?: getString(R.string.placeholder_course)
            tv.text = text
        }
    }

    private fun applyCheckboxTint() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val red = ContextCompat.getColor(requireContext(), R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        val tint = ColorStateList(states, intArrayOf(red, gray))

        val rows = listOf(
            R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5,
            R.id.row6, R.id.row7, R.id.row8, R.id.row9, R.id.row10,
            R.id.row_ud_1
        )
        rows.forEach { id ->
            val row = binding.root.findViewById<View>(id)
            row?.findViewById<MaterialCheckBox>(R.id.check)?.buttonTintList = tint
        }
    }

    private fun wireCourseRowClicks() {
        val rows = listOf(
            R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5,
            R.id.row6, R.id.row7, R.id.row8, R.id.row9, R.id.row10,
            R.id.row_ud_1
        )

        rows.forEach { id ->
            binding.root.findViewById<View>(id)?.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_programRequirementsFragment_to_courseInfoFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
