package darine_abdelmotalib.example.groupproject.data.api

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

object SfuCourseApi {

    private const val BASE_URL =
        "https://www.sfu.ca/bin/wcm/course-outlines"

    fun fetchCourseOutline(dept: String, number: String): CourseOutline {
        val deptLower = dept.lowercase()
        val sectionsUrl = "$BASE_URL?current/current/$deptLower/$number"

        val sectionsJson = httpGet(sectionsUrl)
        val sectionValue = pickSectionValue(sectionsJson)
            ?: throw IllegalStateException("No sections for $dept $number")

        val outlineUrl = "$BASE_URL?current/current/$deptLower/$number/$sectionValue"
        val outlineJson = httpGet(outlineUrl)

        return parseOutlineJson(deptLower, number, outlineJson)
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

    private fun parseOutlineJson(
        deptLower: String,
        number: String,
        json: String
    ): CourseOutline {
        val obj = JSONObject(json)
        val info = obj.optJSONObject("info")

        fun fromInfoOrRoot(key: String): String {
            val fromInfo = info?.optString(key)
            if (!fromInfo.isNullOrBlank()) return fromInfo
            val fromRoot = obj.optString(key)
            return if (fromRoot.isNullOrBlank()) "" else fromRoot
        }

        val title = fromInfoOrRoot("title")
        val units = fromInfoOrRoot("units").ifBlank { null }
        var description = fromInfoOrRoot("description")
        if (description.isBlank()) {
            val fromRootDesc = obj.optString("description")
            if (!fromRootDesc.isNullOrBlank()) description = fromRootDesc
        }
        if (description.isBlank()) {
            val fromCourseDetails = obj.optString("courseDetails")
            if (!fromCourseDetails.isNullOrBlank()) description = fromCourseDetails
        }

        val prereq = fromInfoOrRoot("prerequisites").ifBlank { null }

        val code = "${deptLower.uppercase()} $number"

        return CourseOutline(
            code = code,
            title = title.ifBlank { code },
            units = units,
            description = description,
            prerequisites = prereq
        )
    }
}
