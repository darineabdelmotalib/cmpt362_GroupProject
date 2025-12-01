package darine_abdelmotalib.example.groupproject.ui.profile

import android.content.Context
import androidx.core.content.edit

object UserProfilePrefs {
    private const val PREFS_NAME = "user_profile_data"
    private const val KEY_NAME = "user_name"
    private const val KEY_MAJOR = "user_major"
    private const val KEY_MAJOR_2 = "user_major_2"
    private const val KEY_MINOR = "user_minor"
    private const val KEY_AVATAR_URI = "user_avatar_uri"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveProfile(context: Context, name: String, major: String, major2: String, minor: String, avatarUri: String?) {
        getPrefs(context).edit {
            putString(KEY_NAME, name)
            putString(KEY_MAJOR, major)
            putString(KEY_MAJOR_2, major2)
            putString(KEY_MINOR, minor)
            putString(KEY_AVATAR_URI, avatarUri)
        }
    }

    fun getName(context: Context): String = getPrefs(context).getString(KEY_NAME, "User's Name") ?: "User's Name"
    fun getMajor(context: Context): String = getPrefs(context).getString(KEY_MAJOR, "Computing Science") ?: "Computing Science"
    fun getMajor2(context: Context): String = getPrefs(context).getString(KEY_MAJOR_2, "") ?: ""
    fun getMinor(context: Context): String = getPrefs(context).getString(KEY_MINOR, "None") ?: "None"
    fun getAvatarUri(context: Context): String? = getPrefs(context).getString(KEY_AVATAR_URI, null)
}