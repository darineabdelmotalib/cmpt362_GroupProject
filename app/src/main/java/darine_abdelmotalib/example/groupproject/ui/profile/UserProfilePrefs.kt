package darine_abdelmotalib.example.groupproject.ui.profile

import android.content.Context
import androidx.core.content.edit
import darine_abdelmotalib.example.groupproject.R

object UserProfilePrefs {
    private const val PREFS_NAME = "user_profile_data"
    private const val KEY_NAME = "user_name"
    private const val KEY_MAJOR = "user_major"
    private const val KEY_MAJOR_2 = "user_major_2"
    private const val KEY_MINOR = "user_minor"
    private const val KEY_AVATAR_URI = "user_avatar_uri"
    private const val KEY_AVATAR_INDEX = "user_avatar_index"

    // Available avatar drawable resources
    val AVATARS = listOf(
        R.drawable.avatar_student_1,
        R.drawable.avatar_student_2,
        R.drawable.avatar_student_3,
        R.drawable.avatar_student_4,
        R.drawable.avatar_student_5,
        R.drawable.avatar_student_6
    )

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

    fun saveAvatarIndex(context: Context, index: Int) {
        getPrefs(context).edit {
            putInt(KEY_AVATAR_INDEX, index)
            // Clear custom avatar URI when using built-in avatar
            putString(KEY_AVATAR_URI, null)
        }
    }

    fun getAvatarIndex(context: Context): Int = getPrefs(context).getInt(KEY_AVATAR_INDEX, 0)

    fun getAvatarDrawable(context: Context): Int {
        val index = getAvatarIndex(context)
        return if (index in AVATARS.indices) AVATARS[index] else AVATARS[0]
    }

    fun getName(context: Context): String = getPrefs(context).getString(KEY_NAME, "User's Name") ?: "User's Name"
    fun getMajor(context: Context): String = getPrefs(context).getString(KEY_MAJOR, "Computing Science") ?: "Computing Science"
    fun getMajor2(context: Context): String = getPrefs(context).getString(KEY_MAJOR_2, "") ?: ""
    fun getMinor(context: Context): String = getPrefs(context).getString(KEY_MINOR, "None") ?: "None"
    fun getAvatarUri(context: Context): String? = getPrefs(context).getString(KEY_AVATAR_URI, null)
}