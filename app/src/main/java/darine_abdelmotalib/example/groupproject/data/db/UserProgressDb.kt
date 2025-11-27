package darine_abdelmotalib.example.groupproject.data.db

import android.content.Context
import android.util.Log
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs

object UserProgressDb {

    /*Course Logic --------------------------------------------*/
    private val completedCourseKeys = mutableSetOf<String>()

    /*course key*/
    private fun makeKey(id: String, number: String): String {
        /*eg. "cmpt-362"*/
        return "${id.lowercase()}-${number.lowercase()}"
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
    /*Course Logic End --------------------------------------------*/

    /*Semester Logic --------------------------------------------*/

    private val semesterKeys = mutableSetOf<String>()

    const val SPRING_SEM_INDEX = 0
    const val SUMMER_SEM_INDEX = 1
    const val FALL_SEM_INDEX = 2
    const val DEBUG_SEM_INDEX = 10

    /*Creates a shared preferences key for semesters*/
    private fun makeSemKey(sem: Int, year: Int): String {
        /*eg. fall-2025*/
        val semStr = when(sem){
            SPRING_SEM_INDEX -> "spring"
            SUMMER_SEM_INDEX -> "summer"
            FALL_SEM_INDEX -> "fall"
            DEBUG_SEM_INDEX -> "debug"
            else -> "unknown"
        }
        if(semStr == "unknown") Log.d("UserProgressDb", "Semester index not recognized: $sem")
        return makeKey(semStr, year.toString())
    }

    /*To initialize a semester with no courses*/
    fun setSem(context: Context, sem: Int, year: Int) {
        val key = makeSemKey(sem, year)
        CoursePrefs.setSem(context, key)
    }

    /*Returns true if course is successfully added
    * Returns false if course is already there*/
    fun addCourseToSem(context: Context, sem: Int, year: Int, dept: String, number: String): Boolean{
        val semKey = makeSemKey(sem, year)
        val insertedCourseKey = makeKey(dept, number)
        return CoursePrefs.addCourseToSem(context, semKey, insertedCourseKey)
    }

    /*Returns true if course is successfully removed
    * Returns false if course is not there*/
    fun removeCourseFromSem(context: Context, sem: Int, year: Int, dept: String, number: String): Boolean {
        val semKey = makeSemKey(sem, year)
        val removedCourseKey = makeKey(dept, number)
        return CoursePrefs.removeCourseFromSem(context, semKey, removedCourseKey)
    }

    /*Returns a map containing all semesters and its courses
          ([semester key] to [mutable list of course keys])
    * eg. ("fall-2025" to ["cmpt-362", "cmpt-225"])*/
    fun getAllSems(context: Context): Map<String, MutableList<String>>{
        return CoursePrefs.getAllSems(context)
    }

    /*Prints the specified semester information to logcat*/
    fun debugPrintSem(context: Context, sem: Int, year: Int){
        val semKey = makeSemKey(sem, year)
        CoursePrefs.debugPrintSem(context, semKey)
    }

    /*Semester Logic End --------------------------------------------*/
}