package darine_abdelmotalib.example.groupproject.data.api

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

data class SemesterDates(
    val term: String,
    val year: Int,
    val enrollmentStart: String,
    val classesBegin: String,
    val lastDayToAdd: String,
    val lastDayToDropNoW: String,
    val lastDayToDropWithW: String,
    val classesEnd: String,
    val examPeriod: String
)

object SfuCalendarApi {

    private const val BASE_URL = "https://www.sfu.ca/bin/wcm/academic-calendar"

    /**
     * Fetches important dates for a given semester from SFU's academic calendar API.
     * Falls back to estimated dates if the API is unavailable.
     */
    fun fetchSemesterDates(term: String, year: Int): SemesterDates {
        return try {
            fetchFromApi(term, year)
        } catch (e: Exception) {
            Log.e("SfuCalendarApi", "Error fetching from API: ${e.message}, using estimated dates")
            getEstimatedDates(term, year)
        }
    }

    private fun fetchFromApi(term: String, year: Int): SemesterDates {
        // Try to fetch from SFU's API
        val termCode = when (term.lowercase()) {
            "spring" -> "spring"
            "summer" -> "summer"
            "fall" -> "fall"
            else -> "fall"
        }

        val url = "$BASE_URL?$year/$termCode"

        try {
            val response = httpGet(url)
            return parseDatesFromResponse(response, term, year)
        } catch (e: Exception) {
            Log.e("SfuCalendarApi", "API call failed: ${e.message}")
            throw e
        }
    }

    private fun parseDatesFromResponse(response: String, term: String, year: Int): SemesterDates {
        // Try to parse JSON response from SFU
        try {
            val json = JSONObject(response)
            
            return SemesterDates(
                term = term,
                year = year,
                enrollmentStart = json.optString("enrollmentStart", getEstimatedEnrollmentStart(term, year)),
                classesBegin = json.optString("classesBegin", getEstimatedClassesBegin(term, year)),
                lastDayToAdd = json.optString("lastDayToAdd", getEstimatedLastDayToAdd(term, year)),
                lastDayToDropNoW = json.optString("lastDayToDropNoW", getEstimatedDropNoW(term, year)),
                lastDayToDropWithW = json.optString("lastDayToDropWithW", getEstimatedDropWithW(term, year)),
                classesEnd = json.optString("classesEnd", getEstimatedClassesEnd(term, year)),
                examPeriod = json.optString("examPeriod", getEstimatedExamPeriod(term, year))
            )
        } catch (e: Exception) {
            // If parsing fails, use estimated dates
            return getEstimatedDates(term, year)
        }
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val body = stream.bufferedReader().use(BufferedReader::readText)
        conn.disconnect()

        if (code !in 200..299) {
            throw RuntimeException("HTTP $code from $urlString")
        }

        return body
    }

    /**
     * Returns estimated dates based on typical SFU academic calendar patterns.
     * These are approximate and based on historical SFU semester patterns.
     */
    fun getEstimatedDates(term: String, year: Int): SemesterDates {
        return SemesterDates(
            term = term,
            year = year,
            enrollmentStart = getEstimatedEnrollmentStart(term, year),
            classesBegin = getEstimatedClassesBegin(term, year),
            lastDayToAdd = getEstimatedLastDayToAdd(term, year),
            lastDayToDropNoW = getEstimatedDropNoW(term, year),
            lastDayToDropWithW = getEstimatedDropWithW(term, year),
            classesEnd = getEstimatedClassesEnd(term, year),
            examPeriod = getEstimatedExamPeriod(term, year)
        )
    }

    private fun getEstimatedEnrollmentStart(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "November ${year - 1}"
            "summer" -> "March $year"
            "fall" -> "June $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedClassesBegin(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "January 6, $year"
            "summer" -> "May 5, $year"
            "fall" -> "September 3, $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedLastDayToAdd(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "January 17, $year"
            "summer" -> "May 16, $year"
            "fall" -> "September 14, $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedDropNoW(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "January 24, $year"
            "summer" -> "May 23, $year"
            "fall" -> "September 21, $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedDropWithW(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "March 7, $year"
            "summer" -> "July 4, $year"
            "fall" -> "November 7, $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedClassesEnd(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "April 8, $year"
            "summer" -> "August 1, $year"
            "fall" -> "December 3, $year"
            else -> "TBA"
        }
    }

    private fun getEstimatedExamPeriod(term: String, year: Int): String {
        return when (term.lowercase()) {
            "spring" -> "April 11-23, $year"
            "summer" -> "August 4-9, $year"
            "fall" -> "December 6-18, $year"
            else -> "TBA"
        }
    }
}

