package darine_abdelmotalib.example.groupproject.data.db

import android.content.Context
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs

object UserProgressDb {

    private val completedCourseKeys = mutableSetOf<String>()

    private fun makeKey(dept: String, number: String): String {
        return "${dept.lowercase()}-${number.lowercase()}"
    }

    fun markCourseCompleted(context: Context, dept: String, number: String) {
        val key = makeKey(dept, number)
        CoursePrefs.setCourseCompleted(context, key, true)
    }

    fun markCourseIncomplete(context: Context, dept: String, number: String) {
        val key = makeKey(dept, number)
        CoursePrefs.setCourseCompleted(context, key, false)
    }

    fun isCourseCompleted(context: Context, dept: String, number: String): Boolean {
        val key = makeKey(dept, number)
        return CoursePrefs.isCourseCompleted(context, key)
    }

    fun getCompletedCount(context: Context): Int {
        val allCourses = CsRequirementsDb.getAllUniqueCourses()
        var count = 0
        for (course in allCourses) {
            if (isCourseCompleted(context, course.dept, course.number)) {
                count++
            }
        }
        return count
    }
}