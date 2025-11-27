package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.content.Context
import androidx.lifecycle.ViewModel
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb

class SemesterPlanListViewModel : ViewModel() {
    private val debugSemVal = listOf(UserProgressDb.DEBUG_SEM_INDEX, 2030)
    private val debugCourseVal = listOf("cmpt", "362", "cmpt", "225")
    private var debugIndex = 0


    fun debugAddSem(context: Context) {
        UserProgressDb.setSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int)
        UserProgressDb.debugPrintSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int)
    }

    fun debugAddCourse(context: Context){
        if (debugIndex == 0){
            UserProgressDb.addCourseToSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int, debugCourseVal[0] as String, debugCourseVal[1] as String)
            debugIndex++
        } else {
            UserProgressDb.addCourseToSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int, debugCourseVal[2] as String, debugCourseVal[3] as String)
        }
        UserProgressDb.debugPrintSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int)
    }
    fun debugRemoveCourse(context: Context){
        UserProgressDb.removeCourseFromSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int, debugCourseVal[0] as String, debugCourseVal[1] as String)
        UserProgressDb.debugPrintSem(context, debugSemVal[0] as Int, debugSemVal[1] as Int)
    }

}