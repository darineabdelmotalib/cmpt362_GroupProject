package darine_abdelmotalib.example.groupproject.data.db

object UserProgressDb {

    private val completedCourseKeys = mutableSetOf<String>()

    //Temp course progress
    init {
        markCourseCompleted("cmpt", "105w")
        markCourseCompleted("cmpt", "120")
        markCourseCompleted("cmpt", "125")
        markCourseCompleted("macm", "101")
        markCourseCompleted("cmpt", "307")
    }

    private fun makeKey(dept: String, number: String): String {
        return "${dept.lowercase()}-${number.lowercase()}"
    }

    fun markCourseCompleted(dept: String, number: String) {
        completedCourseKeys.add(makeKey(dept, number))
    }

    fun markCourseIncomplete(dept: String, number: String) {
        completedCourseKeys.remove(makeKey(dept, number))
    }

    fun isCourseCompleted(dept: String, number: String): Boolean {
        return completedCourseKeys.contains(makeKey(dept, number))
    }

    fun getCompletedCount(): Int {
        return completedCourseKeys.size
    }
}