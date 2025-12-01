package darine_abdelmotalib.example.groupproject.ui.maze

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
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
        generateSagaMap()
    }

    private fun generateSagaMap() {
        val context = requireContext()
        val rootContainer = binding.mazeContainer
        rootContainer.removeAllViews()

        val allCourses = CsRequirementsDb.getAllUniqueCourses()
            .sortedBy { it.number }

        val levels = allCourses.groupBy { it.number.firstOrNull() ?: '0' }

        var lastCompletedView: View? = null

        for ((levelChar, courses) in levels) {
            val levelNum = levelChar.digitToIntOrNull() ?: 0

            val completedCount = courses.count { UserProgressDb.isCourseCompleted(context, it.dept, it.number) }
            val totalCount = courses.size
            val isAllDone = completedCount == totalCount

            val headerText = TextView(context).apply {
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, 60, 0, 30)

                //Lower Division (100/200): Requires 100% for "Completed"
                //Upper Division (300/400): Shows # courses completed
                if (levelNum < 3) {
                    if (isAllDone) {
                        text = "LEVEL ${levelChar}00 COMPLETED!"
                        setTextColor(Color.parseColor("#FFD700")) //Gold for completion
                    } else {
                        text = "LEVEL ${levelChar}00 ($completedCount/$totalCount)"
                        setTextColor(Color.BLACK)
                    }
                } else {
                    //For 300/400 levels where not all are required
                    text = "LEVEL ${levelChar}00 ($completedCount Completed)"

                    if (completedCount > 0) {
                        setTextColor(Color.BLUE)
                    } else {
                        setTextColor(Color.BLACK)
                    }
                }
            }
            rootContainer.addView(headerText)

            val levelContainer = FrameLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            rootContainer.addView(levelContainer)

            val itemWidth = 300
            val itemHeight = 160
            val vGap = 100

            for ((index, course) in courses.withIndex()) {
                val row = index / 2
                val col = index % 2

                //Zig-Zag Logic: Even rows Left->Right, Odd rows Right->Left
                val isLeft = if (row % 2 == 0) (col == 0) else (col == 1)

                val button = Button(context).apply {
                    text = "${course.dept.uppercase()} ${course.number}"
                    textSize = 12f
                    gravity = Gravity.CENTER
                    layoutParams = FrameLayout.LayoutParams(itemWidth, itemHeight).apply {
                        topMargin = row * (itemHeight + vGap)
                        gravity = if (isLeft) Gravity.START else Gravity.END
                        marginStart = if (isLeft) 100 else 0
                        marginEnd = if (!isLeft) 100 else 0
                    }
                }

                // Color Logic
                val isDone = UserProgressDb.isCourseCompleted(context, course.dept, course.number)
                if (isDone) {
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50")) // Green
                    lastCompletedView = button
                } else {
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#757575")) // Grey
                    button.alpha = 0.6f
                }

                button.setOnClickListener {
                    val status = if (isDone) "Completed" else "Not Completed"
                    Toast.makeText(context, "${course.label}\n$status", Toast.LENGTH_SHORT).show()
                }

                // Draw Lines
                if (index > 0) {
                    val isSameRow = (index / 2) == ((index - 1) / 2)
                    val line = View(context).apply {
                        if (isSameRow) {
                            // Horizontal
                            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10).apply {
                                topMargin = row * (itemHeight + vGap) + (itemHeight / 2) - 5
                                marginStart = itemWidth + 80
                                marginEnd = itemWidth + 80
                            }
                        } else {
                            // Vertical Drop
                            layoutParams = FrameLayout.LayoutParams(10, vGap).apply {
                                topMargin = (row * (itemHeight + vGap)) - vGap + (itemHeight/2)
                                val dropOnLeft = (row % 2 != 0)
                                gravity = if (!dropOnLeft) Gravity.START else Gravity.END
                                marginStart = if (!dropOnLeft) 100 + (itemWidth/2) else 0
                                marginEnd = if (dropOnLeft) 100 + (itemWidth/2) else 0
                            }
                        }
                        setBackgroundColor(Color.parseColor("#BDBDBD"))
                    }
                    levelContainer.addView(line)
                }
                levelContainer.addView(button)
            }
        }

        // 3. Move Character
        rootContainer.post {
            if (lastCompletedView != null) {
                moveCharacterToView(lastCompletedView)
            }
        }
    }

    private fun moveCharacterToView(target: View) {
        val charToken = binding.characterToken

        // We need to find the absolute position of the target view relative to the root container
        // Since 'target' is inside a FrameLayout which is inside the LinearLayout...

        val parent = target.parent as View

        val targetX = parent.x + target.x + (target.width / 2) - (charToken.width / 2)
        val targetY = parent.y + target.y + (target.height / 2) - (charToken.height / 2) + binding.mazeContainer.top

        charToken.x = targetX
        charToken.y = targetY
        charToken.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}