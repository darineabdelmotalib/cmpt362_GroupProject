package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import darine_abdelmotalib.example.groupproject.data.prefs.CoursePrefs

class SemesterPlanListViewModelFactory(private val context: Context)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val semPrefs: SharedPreferences = CoursePrefs.semesterPrefs(context)
        return SemesterPlanListViewModel(semPrefs, context) as T
    }
}