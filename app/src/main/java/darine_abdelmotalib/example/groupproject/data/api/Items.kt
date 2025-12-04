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
    val classNumber: String, //eg. "5508"
    val scheduleInfo: ScheduleInfo? = null
)

data class CourseOutline(
    val code: String, //eg. "CMPT 362"
    val title: String,
    val units: String?,
    val description: String,
    val prerequisites: String?,
    val sections: List<SectionInfo> = emptyList()
)

data class SectionInfo(
    val sectionCode: String, //eg. "D100", "D101"
    val sectionType: String, //eg. "LEC", "LAB", "TUT"
    val instructor: String,
    val schedule: String, //eg. "Mon, Wed 10:30-11:20"
    val location: String, //eg. "AQ 3181"
    val classNumber: String
)

data class ScheduleInfo(
    val lectureSection: String,
    val lectureInstructor: String,
    val lectureSchedule: String,
    val lectureLocation: String,
    val labSection: String?,
    val labInstructor: String?,
    val labSchedule: String?,
    val labLocation: String?
)
