package darine_abdelmotalib.example.groupproject.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.ActivityCourseDetailBinding
import darine_abdelmotalib.example.groupproject.utils.ToolbarUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Shows detailed info for a specific course section using the SFU API.
 *
 * - Sections endpoint:
 *   https://www.sfu.ca/bin/wcm/course-outlines?current/current/{dept}/{courseNumber}
 *
 * - Outline endpoint:
 *   https://www.sfu.ca/bin/wcm/course-outlines?current/current/{dept}/{courseNumber}/{sectionValue}
 */
class CourseDetailActivity : ComponentActivity() {

    private lateinit var binding: ActivityCourseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dept = intent.getStringExtra(EXTRA_DEPT) ?: "cmpt"
        val number = intent.getStringExtra(EXTRA_COURSE_NUMBER)
        val title = intent.getStringExtra(EXTRA_COURSE_TITLE) ?: ""

        if (number == null) {
            finish()
            return
        }

        ToolbarUtils.setupToolbar(this, binding.topAppBar.topAppBar)
        binding.topAppBar.topAppBar.title = "${dept.uppercase()} $number"

        binding.headerText.text = buildString {
            append("${dept.uppercase()} $number")
            if (title.isNotBlank()) append(" — ").append(title)
        }

        fetchCourseDetail(dept, number)
    }

    private fun fetchCourseDetail(dept: String, number: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.detailText.text = "Loading course details..."

        Thread {
            val detailText: CharSequence = try {
                val baseUrl = "https://www.sfu.ca/bin/wcm/course-outlines"
                val sectionsUrl = "$baseUrl?current/current/$dept/$number"

                //get sections for this course
                val sectionsJson = httpGet(sectionsUrl)
                val sectionValue = pickSectionValue(sectionsJson)

                if (sectionValue == null) {
                    "No sections found for ${dept.uppercase()} $number in the current term."
                } else {
                    //get outline for this section
                    val outlineUrl = "$baseUrl?current/current/$dept/$number/$sectionValue"
                    val outlineJson = httpGet(outlineUrl)
                    formatCourseOutline(outlineJson)
                }
            } catch (e: Exception) {
                "Error loading course details: ${e.message}"
            }

            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.detailText.text = detailText
            }
        }.start()
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val body = stream.bufferedReader().use(BufferedReader::readText)
        conn.disconnect()

        if (code !in 200..299) {
            throw RuntimeException("HTTP $code from $urlString\n$body")
        }

        return body
    }

    private fun pickSectionValue(sectionsJson: String): String? {
        val arr = JSONArray(sectionsJson)
        if (arr.length() == 0) return null

        var chosen: JSONObject? = null
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.optString("classType") == "e") {
                chosen = obj
                break
            }
        }
        if (chosen == null) {
            chosen = arr.getJSONObject(0)
        }

        return chosen.optString("value", null)
    }

    //build text from json
    private fun formatCourseOutline(json: String): CharSequence {
        val obj = JSONObject(json)
        val info = obj.optJSONObject("info")

        fun fromInfoOrRoot(key: String): String {
            val fromInfo = info?.optString(key)
            if (!fromInfo.isNullOrBlank()) return fromInfo
            val fromRoot = obj.optString(key)
            return if (fromRoot.isNullOrBlank()) "" else fromRoot
        }

        val sb = SpannableStringBuilder()
        val headingColor = ContextCompat.getColor(this, R.color.top_appbar_color)

        fun addHeading(title: String) {
            if (sb.isNotEmpty()) sb.append("\n")
            val start = sb.length
            sb.append(title).append("\n")
            val end = sb.length
            sb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(
                ForegroundColorSpan(headingColor),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        fun addBody(text: String) {
            sb.append(text).append("\n")
        }

        //general info
        val name = fromInfoOrRoot("name")
        val title = fromInfoOrRoot("title")
        val units = fromInfoOrRoot("units")
        val term = obj.optString("term")
        val prereq = fromInfoOrRoot("prerequisites")
        val notes = fromInfoOrRoot("notes")

        //description
        var desc = fromInfoOrRoot("description")
        if (desc.isBlank()) {
            val fromRootDesc = obj.optString("description")
            if (!fromRootDesc.isNullOrBlank()) desc = fromRootDesc
        }
        if (desc.isBlank()) {
            val fromCourseDetails = obj.optString("courseDetails")
            if (!fromCourseDetails.isNullOrBlank()) desc = fromCourseDetails
        }

        //general section
        val generalLines = mutableListOf<String>()
        if (name.isNotBlank()) generalLines.add("Name: $name")
        if (title.isNotBlank()) generalLines.add("Title: $title")
        if (term.isNotBlank()) generalLines.add("Term: $term")
        if (units.isNotBlank()) generalLines.add("Units: $units")
        if (prereq.isNotBlank()) generalLines.add("Prerequisites: $prereq")
        if (notes.isNotBlank()) generalLines.add("Notes: $notes")

        if (generalLines.isNotEmpty()) {
            addHeading("General")
            addBody(generalLines.joinToString("\n"))
        }

        //description section
        if (desc.isNotBlank()) {
            addHeading("Description")
            addBody(desc)
        }

        //instructors
        val instructors = obj.optJSONArray("instructor")
        if (instructors != null && instructors.length() > 0) {
            addHeading("Instructor(s)")
            val list = StringBuilder()
            for (i in 0 until instructors.length()) {
                val ins = instructors.getJSONObject(i)
                val nameI = ins.optString("name")
                val role = ins.optString("roleCode")
                val email = ins.optString("email")
                val office = ins.optString("office")

                if (nameI.isNotBlank()) {
                    list.append("- ").append(nameI)
                    if (role.isNotBlank()) list.append(" ($role)")
                    if (email.isNotBlank()) list.append(" — ").append(email)
                    if (office.isNotBlank()) list.append(" [Office: ").append(office).append("]")
                    list.append("\n")
                }
            }
            addBody(list.toString().trimEnd())
        }

        //schedule
        val schedule = obj.optJSONArray("courseSchedule")
        if (schedule != null && schedule.length() > 0) {
            addHeading("Schedule")
            val list = StringBuilder()
            for (i in 0 until schedule.length()) {
                val sch = schedule.getJSONObject(i)
                val isExam = sch.optBoolean("isExam", false)
                val days = sch.optString("days")
                val startTime = sch.optString("startTime")
                val endTime = sch.optString("endTime")
                val campus = sch.optString("campus")
                val sectionCode = sch.optString("sectionCode")

                list.append(if (isExam) "Exam" else "Class")
                if (sectionCode.isNotBlank()) list.append(" (").append(sectionCode).append(")")
                list.append(": ")

                if (days.isNotBlank()) list.append(days).append(" ")
                if (startTime.isNotBlank() || endTime.isNotBlank()) {
                    list.append(startTime)
                    if (endTime.isNotBlank()) list.append("–").append(endTime)
                    list.append(" ")
                }
                if (campus.isNotBlank()) list.append("@ ").append(campus)

                list.append("\n")
            }
            addBody(list.toString().trimEnd())
        }

        if (sb.isEmpty()) {
            sb.append("No additional information found for this course.")
        }

        return sb
    }

    companion object {
        const val EXTRA_DEPT = "extra_dept"
        const val EXTRA_COURSE_NUMBER = "extra_course_number"
        const val EXTRA_COURSE_TITLE = "extra_course_title"
    }
}
