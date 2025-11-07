package darine_abdelmotalib.example.groupproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.ActivityCourseInfoBinding
import darine_abdelmotalib.example.groupproject.utils.ToolbarUtils

class CourseInfoActivity : ComponentActivity() {

    private lateinit var binding: ActivityCourseInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbarTitle = intent.getStringExtra("toolbarTitle")
            ?: getString(R.string.title_course_info_toolbar)

        val codeAndCredits = intent.getStringExtra("codeCredits")
            ?: getString(R.string.placeholder_code_credits)

        val longTitle = intent.getStringExtra("longTitle")
            ?: getString(R.string.placeholder_course_long_title)

        val prereqLines = intent.getStringArrayListExtra("prereqs") ?: arrayListOf(
            getString(R.string.placeholder_prereq_units),
            getString(R.string.placeholder_prereq_course_with_grade)
        )

        val description = intent.getStringExtra("description")
            ?: getString(R.string.placeholder_description)

        ToolbarUtils.setupToolbar(this, binding.topAppBar.topAppBar /* no titleRes */)
        binding.topAppBar.topAppBar.title = toolbarTitle

        binding.codeCredits.text = codeAndCredits
        binding.longTitle.text = longTitle

        binding.prereq1.text = prereqLines.getOrNull(0) ?: ""
        binding.prereq2.text = prereqLines.getOrNull(1) ?: ""

        binding.descriptionText.text = description
    }
}
