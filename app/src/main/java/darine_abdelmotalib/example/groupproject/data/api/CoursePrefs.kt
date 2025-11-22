package darine_abdelmotalib.example.groupproject.data.prefs

import android.content.Context
import android.content.SharedPreferences

object CoursePrefs {

    private const val PREF_NAME = "course_completion_prefs"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isCourseCompleted(context: Context, key: String): Boolean =
        prefs(context).getBoolean(key, false)

    fun setCourseCompleted(context: Context, key: String, completed: Boolean) {
        prefs(context).edit().putBoolean(key, completed).apply()
    }
}
