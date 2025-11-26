package darine_abdelmotalib.example.groupproject.ui.maze

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.FragmentCourseMazeBinding

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
        generateMaze()
    }

    private fun generateMaze() {
        val context = requireContext()
        val container = binding.mazeContainer
        container.removeAllViews()

        val allCourses = CsRequirementsDb.getAllUniqueCourses()
            .sortedBy { it.number }

        //Group courses by 100 level, 200 level, ...
        val coursesByLevel = allCourses.groupBy { it.number.firstOrNull() ?: '0' }

        var lastCompletedView: View? = null

        for ((levelChar, levelCourses) in coursesByLevel) {

            val isLevelComplete = levelCourses.all { course ->
                UserProgressDb.isCourseCompleted(context, course.dept, course.number)
            }

            val levelTitle = TextView(context).apply {
                text = "LEVEL ${levelChar}00"
                textSize = 22f
                setTypeface(null, android.graphics.Typeface.BOLD)

                //Set level header text to gold if all courses are completed
                if (isLevelComplete) {
                    setTextColor(Color.parseColor("#FFD700"))
                    text = "${text} COMPLETED"
                } else {
                    setTextColor(Color.BLACK)
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 60, 0, 20)
                }
            }
            container.addView(levelTitle)

            for ((index, course) in levelCourses.withIndex()) {

                if (index > 0) {
                    val line = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(6, 80)
                        setBackgroundColor(Color.parseColor("#BDBDBD"))
                    }
                    container.addView(line)
                }

                val button = Button(context).apply {
                    text = "${course.dept.uppercase()} ${course.number}"
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(250, 150)
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor("#757575"))
                }

                val isCompleted = UserProgressDb.isCourseCompleted(context, course.dept, course.number)

                if (isCompleted) {
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50")) // Green
                    button.alpha = 1.0f
                    lastCompletedView = button
                } else {
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#757575")) // Grey
                    button.alpha = 0.6f
                }

                button.setOnClickListener {
                    val status = if (isCompleted) "Completed" else "Locked"
                    Toast.makeText(context, "${course.label}\nStatus: $status", Toast.LENGTH_SHORT).show()
                }

                container.addView(button)
            }
        }

        //Move character token
        container.post {
            if (lastCompletedView != null) {
                moveCharacterToView(lastCompletedView!!)
            } else {
                binding.characterToken.visibility = View.INVISIBLE
            }
        }
    }

    private fun moveCharacterToView(targetView: View) {
        val character = binding.characterToken

        val targetX = targetView.x + (targetView.width / 2) - (character.width / 2)
        val targetY = targetView.y + (targetView.height / 2) - (character.height / 2) + binding.mazeContainer.top

        character.x = targetX
        character.y = targetY
        character.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}