package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.data.api.SemesterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SemesterPlanListViewModel(
    private val semPrefs: SharedPreferences,
    private val context: Context)
    : ViewModel() {

    private val _semesters = MutableLiveData<List<SemesterItem>>()
    val semesters: LiveData<List<SemesterItem>> = _semesters

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        loadSemesters()
    }

    init {
        semPrefs.registerOnSharedPreferenceChangeListener(listener)
        loadSemesters()
    }

    private fun loadSemesters() {
        viewModelScope.launch(Dispatchers.IO){
            Log.d("SemesterPlanListViewModel", "loadSemester() called")
            try {
                val list = UserProgressDb.getAllSemsList(context)
                _semesters.postValue(list)
            } catch (e: Exception) {
                Log.d("SemesterPlanListViewModel", "Error when loading semesters")
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        semPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }

    fun onDialogResult(seasonVal: String, yearVal: String) {
        addEmptySem(seasonVal, yearVal)
    }

    fun addEmptySem(seasonVal: String, yearVal: String): Boolean{

        val semIndex = when(seasonVal.lowercase()) {
            "spring" -> UserProgressDb.SPRING_SEM_INDEX
            "summer" -> UserProgressDb.SUMMER_SEM_INDEX
            "fall" -> UserProgressDb.FALL_SEM_INDEX
            else -> UserProgressDb.DEBUG_SEM_INDEX
        }
        val yearInt = yearVal.toInt()

        val doesSemExist = UserProgressDb.doesSemExist(context, semIndex, yearInt)

        if(!doesSemExist){
            UserProgressDb.setSem(context,
                semIndex,
                yearVal.toInt())

            UserProgressDb.debugPrintSem(context, semIndex, yearInt)
            Toast.makeText(context, "Added semester: $seasonVal $yearVal", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Semester $seasonVal $yearVal already exists", Toast.LENGTH_SHORT).show()
        }

        return doesSemExist
    }







    /*DEBUG PURPOSES ----------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------*/

    private val summer = UserProgressDb.SPRING_SEM_INDEX
    private val spring = UserProgressDb.SPRING_SEM_INDEX
    private val fall = UserProgressDb.FALL_SEM_INDEX
    private val debug = UserProgressDb.DEBUG_SEM_INDEX

    private val debugSemVal = listOf(UserProgressDb.FALL_SEM_INDEX, 2024)
    private val dbgSem = listOf(summer, spring, spring, debug, summer, fall, fall, spring)
    private val dbgYr = listOf( 2030,   2031,   2030,   2031,   2031,   2032, 2030, 2032)
    private val dbgDept = "cmpt"
    private val dbgNumber = listOf("362", "225", "120", "210", "276", "295", "201", "125")
    private var debugIndexA = 0
    private var debugIndexB = 0


    fun debugTest(){
        when(debugIndexB){
            0 -> {
                if(debugIndexA < 8){
                    UserProgressDb.setSem(context,
                        dbgSem[debugIndexA] as Int,
                        dbgYr[debugIndexA] as Int)
                    UserProgressDb.debugPrintSem(context, dbgSem[debugIndexA] as Int, dbgYr[debugIndexA] as Int)
                    debugIndexA++
                } else {
                    debugIndexA = 0
                    debugIndexB = 1
                }
            }
            1 -> {
                if(debugIndexA < 8){
                    if(debugIndexA == 3 || debugIndexA == 5){
                        UserProgressDb.addCourseToSem(context,
                            dbgSem[debugIndexA] as Int,
                            dbgYr[debugIndexA] as Int,
                            dbgDept as String,
                            dbgNumber[debugIndexA] as String)
                        UserProgressDb.debugPrintSem(context, dbgSem[debugIndexA] as Int, dbgYr[debugIndexA] as Int)
                        UserProgressDb.addCourseToSem(context,
                            dbgSem[debugIndexA] as Int,
                            dbgYr[debugIndexA] as Int,
                            dbgDept as String,
                            dbgNumber[debugIndexA+1] as String)
                        UserProgressDb.debugPrintSem(context, dbgSem[debugIndexA] as Int, dbgYr[debugIndexA] as Int)
                    } else {
                        UserProgressDb.addCourseToSem(context,
                            dbgSem[debugIndexA] as Int,
                            dbgYr[debugIndexA] as Int,
                            dbgDept as String,
                            dbgNumber[debugIndexA] as String)
                        UserProgressDb.debugPrintSem(context, dbgSem[debugIndexA] as Int, dbgYr[debugIndexA] as Int)
                    }
                    debugIndexA++
                } else {
                    debugIndexA = 0
                    debugIndexB = 2
                }
            }
            2 -> {
                if(debugIndexA < 8){
                    UserProgressDb.removeCourseFromSem(context,
                        dbgSem[debugIndexA] as Int,
                        dbgYr[debugIndexA] as Int,
                        dbgDept as String,
                        dbgNumber[debugIndexA] as String)
                    UserProgressDb.debugPrintSem(context, dbgSem[debugIndexA] as Int, dbgYr[debugIndexA] as Int)
                    debugIndexA++
                } else {
                    debugIndexA = 0
                    debugIndexB = 10
                }
            }
            else -> {Log.d("SemesterPlanListViewModel", "end debug_test")}
        }
    }


}