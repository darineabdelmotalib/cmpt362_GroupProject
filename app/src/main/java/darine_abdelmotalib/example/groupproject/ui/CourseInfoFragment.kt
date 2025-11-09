package darine_abdelmotalib.example.groupproject.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import darine_abdelmotalib.example.groupproject.R

class CourseInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar2)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val prereqList = view.findViewById<LinearLayout>(R.id.prerequisitesList)
        val prereqs = arrayListOf(
            Triple("Completion of x units", false, null),
            Triple("CMPT 225", true, "(with a minimum grade of C-)")
        )

        for ((prereq, check, detail) in prereqs) {
            val row = LayoutInflater.from(requireActivity())
                .inflate(R.layout.course_prereq, prereqList, false)
            val prereqCheck = row.findViewById<ImageView>(R.id.prereqCheck)
            val prereqText = row.findViewById<TextView>(R.id.prereqText)
            val prereqDetail = row.findViewById<TextView>(R.id.prereqDetail)

            prereqText.text = prereq
            if (check) {
                prereqCheck.setImageResource(R.drawable.baseline_check_box_24)
            }
            else {
                prereqCheck.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            }

            if (detail != null) {
                prereqDetail.text = detail
                prereqDetail.visibility = View.VISIBLE
            }
            else {
                prereqDetail.visibility = View.GONE
            }

            prereqList.addView(row)
        }

        val sectionHeader = view.findViewById<TextView>(R.id.sectionHeader)
        val sectionInfoContent = view.findViewById<LinearLayout>(R.id.sectionInfoContent)
        val courseHeader = view.findViewById<TextView>(R.id.courseHeader)
        val courseInfoContent = view.findViewById<LinearLayout>(R.id.courseInfoContent)

        sectionHeader.setOnClickListener {
            if (sectionInfoContent.visibility == View.VISIBLE) {
                sectionInfoContent.visibility = View.GONE
                sectionHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_arrow_drop_down_24, 0)
            }
            else {
                sectionInfoContent.visibility = View.VISIBLE
                sectionHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_arrow_drop_up_24, 0)
            }
        }

        courseHeader.setOnClickListener {
            if (courseInfoContent.visibility == View.VISIBLE) {
                courseInfoContent.visibility = View.GONE
                courseHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_arrow_drop_down_24, 0)
            }
            else {
                courseInfoContent.visibility = View.VISIBLE
                courseHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_arrow_drop_up_24, 0)
            }
        }
    }
}