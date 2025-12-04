package darine_abdelmotalib.example.groupproject.data.api

import android.util.Log
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
        val allSections = parseAllSections(sectionsJson)
        
        // Get the first lecture section for main course info
        val lectureSection = allSections.find { it.optString("classType") == "e" }
            ?: allSections.firstOrNull()
            ?: throw IllegalStateException("No sections for $dept $number")

        val sectionValue = lectureSection.optString("value", null)
            ?: throw IllegalStateException("No section value for $dept $number")

        val outlineUrl = "$BASE_URL?current/current/$deptLower/$number/$sectionValue"
        val outlineJson = httpGet(outlineUrl)

        // Parse main outline info
        val outline = parseOutlineJson(deptLower, number, outlineJson)
        
        // Fetch all section details
        val sectionInfoList = fetchAllSectionDetails(deptLower, number, allSections)
        
        return outline.copy(sections = sectionInfoList)
    }

    fun fetchCourseWithSections(dept: String, number: String): CourseOutline {
        return fetchCourseOutline(dept, number)
    }

    private fun parseAllSections(sectionsJson: String): List<JSONObject> {
        val arr = JSONArray(sectionsJson)
        val sections = mutableListOf<JSONObject>()
        for (i in 0 until arr.length()) {
            sections.add(arr.getJSONObject(i))
        }
        return sections
    }

    private fun fetchAllSectionDetails(
        deptLower: String,
        number: String,
        sections: List<JSONObject>
    ): List<SectionInfo> {
        val sectionInfoList = mutableListOf<SectionInfo>()
        
        for (section in sections) {
            try {
                val sectionValue = section.optString("value") ?: continue
                val sectionText = section.optString("text", sectionValue)
                val classType = section.optString("classType", "")
                
                val sectionType = when (classType) {
                    "e" -> "LEC"
                    "n" -> "LAB"
                    "t" -> "TUT"
                    else -> "LEC"
                }
                
                // Fetch detailed section info
                val outlineUrl = "$BASE_URL?current/current/$deptLower/$number/$sectionValue"
                val outlineJson = httpGet(outlineUrl)
                val obj = JSONObject(outlineJson)
                
                // Parse instructor
                val instructor = parseInstructor(obj)
                
                // Parse schedule
                val scheduleData = parseSchedule(obj)
                
                sectionInfoList.add(
                    SectionInfo(
                        sectionCode = sectionText,
                        sectionType = sectionType,
                        instructor = instructor,
                        schedule = scheduleData.first,
                        location = scheduleData.second,
                        classNumber = section.optString("associatedClass", "")
                    )
                )
            } catch (e: Exception) {
                Log.e("SfuCourseApi", "Error fetching section details: ${e.message}")
            }
        }
        
        return sectionInfoList
    }

    private fun parseInstructor(obj: JSONObject): String {
        // Try to get instructor from various locations in the JSON
        val instructor = obj.optJSONArray("instructor")
        if (instructor != null && instructor.length() > 0) {
            val firstInstructor = instructor.optJSONObject(0)
            if (firstInstructor != null) {
                val name = firstInstructor.optString("name", "")
                if (name.isNotBlank()) return name
                
                val firstName = firstInstructor.optString("firstName", "")
                val lastName = firstInstructor.optString("lastName", "")
                if (firstName.isNotBlank() || lastName.isNotBlank()) {
                    return "$firstName $lastName".trim()
                }
            }
        }
        
        // Try roleInstructor
        val roleInstructor = obj.optJSONArray("roleInstructor")
        if (roleInstructor != null && roleInstructor.length() > 0) {
            val firstInstructor = roleInstructor.optJSONObject(0)
            if (firstInstructor != null) {
                val name = firstInstructor.optString("name", "")
                if (name.isNotBlank()) return name
                
                val firstName = firstInstructor.optString("firstName", "")
                val lastName = firstInstructor.optString("lastName", "")
                if (firstName.isNotBlank() || lastName.isNotBlank()) {
                    return "$firstName $lastName".trim()
                }
            }
        }
        
        return "TBA"
    }

    private fun parseSchedule(obj: JSONObject): Pair<String, String> {
        var schedule = ""
        var location = ""
        
        val courseSchedule = obj.optJSONArray("courseSchedule")
        if (courseSchedule != null && courseSchedule.length() > 0) {
            val firstSchedule = courseSchedule.optJSONObject(0)
            if (firstSchedule != null) {
                val days = firstSchedule.optString("days", "")
                val startTime = firstSchedule.optString("startTime", "")
                val endTime = firstSchedule.optString("endTime", "")
                
                if (days.isNotBlank() && startTime.isNotBlank()) {
                    schedule = "$days $startTime"
                    if (endTime.isNotBlank()) {
                        schedule += " - $endTime"
                    }
                }
                
                val campus = firstSchedule.optString("campus", "")
                val buildingCode = firstSchedule.optString("buildingCode", "")
                val roomNumber = firstSchedule.optString("roomNumber", "")
                
                location = when {
                    buildingCode.isNotBlank() && roomNumber.isNotBlank() -> "$buildingCode $roomNumber"
                    campus.isNotBlank() -> campus
                    else -> "TBA"
                }
            }
        }
        
        if (schedule.isBlank()) schedule = "TBA"
        if (location.isBlank()) location = "TBA"
        
        return Pair(schedule, location)
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
