package darine_abdelmotalib.example.groupproject.ui.adapter

data class SemItem(
    val term: String, //eg. "Fall 2025"
    val credits: String, //eg. "9 units"
    val courseList: MutableList<CourseItem>
)

data class CourseItem(
    val code: String, //eg. "CMPT 362"
    val credits: String, //eg. "(3)"
    val title: String, //eg. "Mobile Appli..."
    val instructor: String, //eg. "Xingdong Yang"
)