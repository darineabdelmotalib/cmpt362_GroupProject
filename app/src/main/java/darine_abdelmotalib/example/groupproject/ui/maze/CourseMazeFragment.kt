package darine_abdelmotalib.example.groupproject.ui.maze

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.FragmentCourseMazeBinding
import darine_abdelmotalib.example.groupproject.ui.profile.UserProfilePrefs

class CourseMazeFragment : Fragment() {

    private var _binding: FragmentCourseMazeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseMazeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCharacterImage()
        generateSagaMap()
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun loadCharacterImage() {
        val context = requireContext()
        val savedUri = UserProfilePrefs.getAvatarUri(context)
        val charToken = binding.characterToken

        if (savedUri != null) {
            try {
                charToken.setImageURI(Uri.parse(savedUri))
            } catch (e: Exception) {
                charToken.setImageResource(R.drawable.default_pfp)
            }
        } else {
            charToken.setImageResource(R.drawable.default_pfp)
        }
    }

    private fun generateSagaMap() {
        val context = requireContext()
        val rootContainer = binding.mazeContainer
        rootContainer.removeAllViews()

        val allCourses = CsRequirementsDb.getAllUniqueCourses()
            .sortedBy { it.number }

        val levels = allCourses.groupBy { it.number.firstOrNull() ?: '0' }

        var lastCompletedView: View? = null
        var firstView: View? = null

        val itemWidth = dpToPx(120)
        val itemHeight = dpToPx(60)
        val verticalGap = dpToPx(80)
        val sideMargin = dpToPx(40)

        for ((levelChar, courses) in levels) {

            val completedCount = courses.count {
                UserProgressDb.isCourseCompleted(context, it.dept, it.number)
            }

            val header = TextView(context).apply {
                text = "LEVEL ${levelChar}00 ($completedCount/${courses.size})"
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(24), 0, dpToPx(12))
            }
            rootContainer.addView(header)

            val levelFrame = FrameLayout(context)
            rootContainer.addView(levelFrame)

            for ((index, course) in courses.withIndex()) {

                val row = index / 2
                val col = index % 2
                val isLeftColumn = if (row % 2 == 0) (col == 0) else (col == 1)

                val button = Button(context).apply {
                    text = "${course.dept.uppercase()} ${course.number}"
                    textSize = 12f
                    gravity = Gravity.CENTER

                    layoutParams = FrameLayout.LayoutParams(itemWidth, itemHeight).apply {
                        topMargin = row * (itemHeight + verticalGap)
                        gravity = if (isLeftColumn) Gravity.START else Gravity.END
                        marginStart = if (isLeftColumn) sideMargin else 0
                        marginEnd = if (!isLeftColumn) sideMargin else 0
                    }
                }

                if (firstView == null) firstView = button

                val completed =
                    UserProgressDb.isCourseCompleted(context, course.dept, course.number)

                if (completed) {
                    button.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                    lastCompletedView = button
                } else {
                    button.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#757575"))
                    button.alpha = 0.6f
                }

                // ★ Navigate to CourseInfoFragment
                button.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("dept", course.dept)
                        putString("courseNumber", course.number)
                        putString("courseTitle", course.label)
                    }
                    findNavController().navigate(
                        R.id.action_courseMazeFragment_to_courseInfoFragment,
                        bundle
                    )
                }

                //courseinfo button
                levelFrame.addView(button)
            }
        }

        rootContainer.post {
            moveCharacterToView(lastCompletedView ?: firstView!!)
        }
    }

    private fun moveCharacterToView(target: View) {
        val charToken = binding.characterToken
        val parent = target.parent as View

        charToken.x =
            parent.x + target.x + (target.width / 2) - (charToken.width / 2)

        charToken.y =
            parent.y + target.y + (target.height / 2) - (charToken.height / 2) +
                    binding.mazeContainer.top

        charToken.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
