package darine_abdelmotalib.example.groupproject.utils

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import darine_abdelmotalib.example.groupproject.R

object ToolbarUtils {

    fun setupToolbar(
        activity: Activity,
        toolbar: Toolbar,
        @StringRes titleRes: Int? = null
    ) {
        if (titleRes != null && titleRes != 0) {
            toolbar.title = activity.getString(titleRes)
        }

        toolbar.setNavigationOnClickListener { activity.finish() }

        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_program_requirements)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_dummy1 -> { /* TODO */ true }
                R.id.action_dummy2 -> { /* TODO */ true }
                else -> false
            }
        }
    }
}
