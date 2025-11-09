package darine_abdelmotalib.example.groupproject.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.checkbox.MaterialCheckBox
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.ActivityProgramRequirementsBinding
import darine_abdelmotalib.example.groupproject.utils.ToolbarUtils

class ProgramRequirementsActivity : ComponentActivity() {

    private lateinit var binding: ActivityProgramRequirementsBinding
    private var lowerExpanded = true
    private var upperExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgramRequirementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // shared toolbar (include wrapper -> inner toolbar)
        ToolbarUtils.setupToolbar(this, binding.topAppBar.topAppBar, R.string.title_cs_major)

        updateLowerSection()
        updateUpperSection()

        binding.lowerHeader.setOnClickListener {
            lowerExpanded = !lowerExpanded; updateLowerSection()
        }
        binding.upperHeader.setOnClickListener {
            upperExpanded = !upperExpanded; updateUpperSection()
        }

        fillRowTexts()
        applyCheckboxTint()
        wireCourseRowClicks()
    }

    private fun updateLowerSection() {
        binding.lowerContent.visibility = if (lowerExpanded) View.VISIBLE else View.GONE
        binding.lowerExpandIcon.setImageResource(
            if (lowerExpanded) R.drawable.ic_expand_less_24 else R.drawable.ic_expand_more_24
        )
    }

    private fun updateUpperSection() {
        binding.upperContent.visibility = if (upperExpanded) View.VISIBLE else View.GONE
        binding.upperExpandIcon.setImageResource(
            if (upperExpanded) R.drawable.ic_expand_less_24 else R.drawable.ic_expand_more_24
        )
    }

    private fun fillRowTexts() {
        val ids = intArrayOf(
            R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5,
            R.id.row6, R.id.row7, R.id.row8, R.id.row9, R.id.row10,
            R.id.row_ud_1
        )
        ids.forEach { id ->
            val row = findViewById<View>(id)
            val tv = row?.findViewById<TextView>(R.id.text) ?: return@forEach
            val text = row.tag?.toString() ?: getString(R.string.placeholder_course)
            tv.text = text
        }
    }

    private fun applyCheckboxTint() {
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        val red = getColor(R.color.sfu_red)
        val gray = Color.parseColor("#777777")
        val tint = ColorStateList(states, intArrayOf(red, gray))

        val rows = listOf(
            R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5,
            R.id.row6, R.id.row7, R.id.row8, R.id.row9, R.id.row10,
            R.id.row_ud_1
        )
        rows.forEach { id ->
            val row = findViewById<View>(id)
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
            findViewById<View>(id)?.setOnClickListener {
                val intent = Intent(this, CourseInfoActivity::class.java).apply {
                    // Pass values now (easy to replace with real data later)
                    putExtra("toolbarTitle", getString(R.string.title_course_info_toolbar))
                    putExtra("codeCredits", getString(R.string.placeholder_code_credits))
                    putExtra("longTitle", getString(R.string.placeholder_course_long_title))
                    putStringArrayListExtra(
                        "prereqs",
                        arrayListOf(
                            getString(R.string.placeholder_prereq_units),
                            getString(R.string.placeholder_prereq_course_with_grade)
                        )
                    )
                    putExtra("description", getString(R.string.placeholder_description))
                }
                startActivity(intent)
            }
        }
    }
}
