package darine_abdelmotalib.example.groupproject.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import darine_abdelmotalib.example.groupproject.data.api.CourseOutline
import darine_abdelmotalib.example.groupproject.data.api.CourseSection
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb

object CoursePrefs {

    private const val PREF_NAME = "course_completion_prefs"
    private const val SEM_PREF_NAME = "semester_list_prefs_debugVer0.8" // remove _debugVerA.B later
    const val ERROR_STRING = "error_string"

    /*Course ---------------------------------------- */
    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isCourseCompleted(context: Context, key: String): Boolean =
        prefs(context).getBoolean(key, false)

    fun setCourseCompleted(context: Context, key: String, completed: Boolean) {
        prefs(context).edit().putBoolean(key, completed).apply()
    }
    /*Course End ---------------------------------------- */

    /*Semester ---------------------------------------- */

    /*SharedPreferences containing semester data*/
    fun semesterPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(SEM_PREF_NAME, Context.MODE_PRIVATE)

    fun doesSemExist(context: Context, key: String): Boolean {
        return semesterPrefs(context).contains(key)
    }

    fun deleteSem(context: Context, key: String){
        if(doesSemExist(context, key)) semesterPrefs(context).edit { remove(key) }
    }

    /*Adds a sem according to semester key (eg. "fall-2025")*/
    fun setSem(context: Context, key: String) {
        semesterPrefs(context).edit { putString(key, "") }
    }

    /*Adds a sem according to semester key (eg. "fall-2025")
    * and a course list (eg.["cmpt-362", "cmpt-225", "cmpt-275"])*/
    fun setSem(context: Context, key: String, classList: MutableList<String>){
        val classListStr = toSemPrefCourseListString(classList)
        setSem(context, key, classListStr)
    }

    /*Adds a sem according to semester key (eg. "fall-2025")
    * and a course list (eg."cmpt-362;cmpt-225;cmpt-275")
    * use toSemPrefCourseListString to convert from mutable list to string*/
    fun setSem(context: Context, key: String, classList: String){
        semesterPrefs(context).edit { putString(key, classList) }
    }

    /*Returns a specified semester's course list
    * Returns a mutable list
    * (eg.["cmpt-362", "cmpt-225", "cmpt-275"]) */
    fun getCourseListFromSem(context: Context, key: String): MutableList<String> {
        val courseListString = semesterPrefs(context).getString(key, ERROR_STRING)
        if (courseListString!!.isNotEmpty() && courseListString != ERROR_STRING) return fromSemPrefCourseListString(courseListString!!)
        return mutableListOf()
    }

    /*Returns true if course is successfully added
    * Returns false if course is already there*/
    fun addCourseToSem(context: Context, key: String, courseKey: String): Boolean {
        val courseList = getCourseListFromSem(context, key)
        if (!courseList.contains(courseKey)) {
            courseList.add(courseKey)
            setSem(context, key, courseList)
            return true
        }
        Log.d("CoursePrefs", "course $courseKey already added")
        return false
    }

    /*Returns true if course is successfully removed
    * Returns false if course is not there*/
    fun removeCourseFromSem(context: Context, key: String, courseKey: String): Boolean {
        val courseList = getCourseListFromSem(context, key)
        if(courseList.contains(courseKey)){
            courseList.remove(courseKey)
            setSem(context, key, courseList)
            return true
        }
        Log.d("CoursePrefs", "course $courseKey not found")
        return false
    }

    /*Returns a map containing all semesters and its courses
          ([semester key] to [mutable list of course keys])
    * eg. ("fall-2025" to ["cmpt-362", "cmpt-225"])*/
    fun getAllSems(context: Context): Map<String, MutableList<String>> {
        val allSemesters: Map<String, String> = semesterPrefs(context).getAll().mapValues {it.value.toString()}
        val returnSemesterMap: Map<String, MutableList<String>> = allSemesters.map { (key, value) ->
            val valueList = fromSemPrefCourseListString(value)
            key to valueList
        }.toMap()
        return returnSemesterMap
    }

    /*Converts mutable list of course keys to one string
    * eg. ["cmpt-362", "cmpt-225"] --> "cmpt-362;cmpt-225"*/
    fun toSemPrefCourseListString(classList: MutableList<String>): String {
        if (classList.isNotEmpty()) return classList.joinToString(";")
        return ""
    }

    /*Converts string to mutable list of course keys
    * eg. "cmpt-362;cmpt-225 --> ["cmpt-362", "cmpt-225"]"*/
    fun fromSemPrefCourseListString(classList: String): MutableList<String>{
        if(classList != ERROR_STRING) return classList.split(";").toMutableList()
        return mutableListOf(ERROR_STRING)
    }

    /*Prints the specified semester information to logcat*/
    fun debugPrintSem(context: Context, key: String){
        val list = getCourseListFromSem(context, key)
        Log.d("debugPrintSem", "debug: sem: $key, courses: $list")
    }
    /*Semester ---------------------------------------- */

}
