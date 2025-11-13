package darine_abdelmotalib.example.groupproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import darine_abdelmotalib.example.groupproject.databinding.ActivityDatabaseTestBinding
import darine_abdelmotalib.example.groupproject.ui.adapter.AllCoursesAdapter
import darine_abdelmotalib.example.groupproject.ui.adapter.AllCoursesItem
import darine_abdelmotalib.example.groupproject.utils.ToolbarUtils
import org.json.JSONArray
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class DatabaseTestActivity : ComponentActivity() {

    private lateinit var binding: ActivityDatabaseTestBinding
    private lateinit var adapter: AllCoursesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarUtils.setupToolbar(this, binding.topAppBar.topAppBar)
        binding.topAppBar.topAppBar.title = "All courses (SFU API)"

        adapter = AllCoursesAdapter { deptCode, courseNumber, courseTitle ->
            val intent = Intent(this, CourseDetailActivity::class.java).apply {
                putExtra(CourseDetailActivity.EXTRA_DEPT, deptCode)
                putExtra(CourseDetailActivity.EXTRA_COURSE_NUMBER, courseNumber)
                putExtra(CourseDetailActivity.EXTRA_COURSE_TITLE, courseTitle)
            }
            startActivity(intent)
        }

        binding.coursesRecycler.layoutManager = LinearLayoutManager(this)
        binding.coursesRecycler.adapter = adapter

        binding.fetchButton.setOnClickListener {
            loadAllDepartmentsAndCourses()
        }
    }

    private fun loadAllDepartmentsAndCourses() {
        binding.progressBar.visibility = View.VISIBLE
        binding.statusText.text = "Loading departments..."
        adapter.updateItems(emptyList())

        Thread {
            val baseUrl = "https://www.sfu.ca/bin/wcm/course-outlines"

            val result: Triple<List<AllCoursesItem>, Int, Int> = try {
                //get all departments for current term
                val deptJson = httpGet("$baseUrl?current/current")
                val deptArray = JSONArray(deptJson)

                val items = mutableListOf<AllCoursesItem>()
                var totalCourses = 0

                for (i in 0 until deptArray.length()) {
                    val deptObj = deptArray.getJSONObject(i)
                    val deptText = deptObj.optString("text")   // e.g. "CMPT"
                    val deptValue = deptObj.optString("value") // e.g. "cmpt"

                    if (deptText.isBlank() || deptValue.isBlank()) continue

                    //add department header
                    items.add(
                        AllCoursesItem(
                            departmentCode = deptValue,
                            departmentName = deptText,
                            courseNumber = null,
                            courseTitle = null,
                            isHeader = true
                        )
                    )

                    //for each department, get list of courses
                    try {
                        val courseJson = httpGet("$baseUrl?current/current/$deptValue")
                        val courseArray = JSONArray(courseJson)

                        for (j in 0 until courseArray.length()) {
                            val courseObj = courseArray.getJSONObject(j)
                            val number = courseObj.optString("text")
                            val title = courseObj.optString("title")

                            if (number.isBlank()) continue

                            items.add(
                                AllCoursesItem(
                                    departmentCode = deptValue,
                                    departmentName = deptText,
                                    courseNumber = number,
                                    courseTitle = title,
                                    isHeader = false
                                )
                            )
                            totalCourses++
                        }
                    } catch (_: Exception) {
                        //ignore this dept's course load failure; keep header
                    }
                }

                Triple(items.toList(), deptArray.length(), totalCourses)
            } catch (_: Exception) {
                Triple(emptyList<AllCoursesItem>(), 0, 0)
            }

            runOnUiThread {
                binding.progressBar.visibility = View.GONE

                val (items, deptCount, courseCount) = result
                if (items.isEmpty()) {
                    binding.statusText.text =
                        "Failed to load courses. Check network or API."
                } else {
                    adapter.updateItems(items)
                    binding.statusText.text =
                        "Loaded $deptCount departments and $courseCount courses."
                }
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
}
