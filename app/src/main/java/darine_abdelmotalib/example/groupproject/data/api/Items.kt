package darine_abdelmotalib.example.groupproject.data.api

data class SemesterItem(
    val term: String, //eg. "Fall 2025"
    val totalUnits: String, //eg. "9" (total units)
    val courseList: MutableList<CourseSection>
)

data class CourseSection(
    val code: String, //eg. "cmpt-362"
    val courseOutline: CourseOutline,
    val instructor: String, //eg. "Xingdong Yang"
    val section: String, //eg. "D100"
    val classNumber: String //eg. "5508"
)

data class CourseOutline(
    val code: String, //eg. "CMPT 362"
    val title: String,
    val units: String?,
    val description: String,
    val prerequisites: String?
)