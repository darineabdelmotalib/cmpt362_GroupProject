package darine_abdelmotalib.example.groupproject.data.db

import android.content.Context
import android.util.Log
import darine_abdelmotalib.example.groupproject.data.api.CourseOutline
import darine_abdelmotalib.example.groupproject.data.api.CourseSection
import darine_abdelmotalib.example.groupproject.data.api.SemesterItem
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs
import darine_abdelmotalib.example.groupproject.ui.Course

object UserProgressDb {

    /*Course Logic --------------------------------------------*/
    private val completedCourseKeys = mutableSetOf<String>()

    /*course key*/
    fun makeKey(id: String, number: String): String {
        /*eg. "cmpt-362"*/
        return "${id.lowercase()}-${number.lowercase()}"
    }

    /*converts a key into a pair of values
    * eg. "cmpt-362" to ["cmpt" to "362"]*/
    fun unKey(key: String): Pair<String, String>{
        val parts = key.split("-")
        try {
            return parts[0] to parts[1]
        } catch (e: Exception) {
            Log.d("UserProgressDb", "ERROR: key $key not in proper format")
            return "error" to "error"
        }
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

    fun fetchCourseOutline(dept: String, number:String): CourseOutline {
        return SfuCourseApi.fetchCourseOutline(dept, number)
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

    fun returnCourseSectionItem(key: String): CourseSection{
        val codePair = unKey(key)
        val dept = codePair.first
        val number = codePair.second
        val courseCode = "${dept.uppercase()} ${number}"
        val outline = SfuCourseApi.fetchCourseOutline(dept, number)

        return CourseSection(
            code = key,
            courseOutline = outline,
            instructor = "Instructor",
            section = "P100",
            classNumber = "0000"
        )
    }

    fun getSemOrder(key: String): Pair<Int, Int>{
        val semPair = unKey(key)
        val semIndex = when(semPair.first.lowercase()){
            "spring" -> 0
            "summer" -> 1
            "fall" -> 2
            else -> -10
        }
        return semIndex to semPair.second.toInt()
    }

    /*Returns a list of SemesterItems*/
    fun getAllSemsList(context: Context): List<SemesterItem> {
        // ("fall-2025" to ["cmpt-362", "cmpt-225"])
        val semsMap = getAllSems(context)

//        Log.d("UserProgressDb", "getAllSemsList ---------------------")
        val list = semsMap.map {(key, value) ->
//            Log.d("UserProgressDb", "key: $key; value: $value --------")
            var units = 0

            var courses = mutableListOf<CourseSection>()

            if (value.isNotEmpty()){
//                Log.d("UserProgressDb", "value.isNotEmpty() returns ${value.isNotEmpty()}")
                if(value[0] != "" && value[0] != CoursePrefs.ERROR_STRING){
//                    Log.d("UserProgressDb", "value[0] != \"\" returns ${value[0] != ""}")
//                    Log.d("UserProgressDb", "value[0] != CoursePrefs.ERROR_STRING returns ${value[0] != CoursePrefs.ERROR_STRING}")
                    courses = value.map { key ->
                        val courseSection = returnCourseSectionItem(key)
                        courseSection.courseOutline.units?.toInt()?.let { units += it }
                        courseSection
                    }.toMutableList()
                }
            }

            SemesterItem(
                term = key,
                totalUnits = units.toString(),
                courseList = courses
            )
        }

        return list.sortedWith(compareBy(
            {getSemOrder(it.term).first}, {getSemOrder(it.term).second}
        ))
    }

    /*Prints the specified semester information to logcat*/
    fun debugPrintSem(context: Context, sem: Int, year: Int){
        val semKey = makeSemKey(sem, year)
        CoursePrefs.debugPrintSem(context, semKey)
    }

    /*Semester Logic End --------------------------------------------*/
}