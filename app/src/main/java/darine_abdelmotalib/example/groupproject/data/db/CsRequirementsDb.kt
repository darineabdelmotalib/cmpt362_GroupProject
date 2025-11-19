package darine_abdelmotalib.example.groupproject.data.db

import darine_abdelmotalib.example.groupproject.R

data class RequirementCourse(
    val rowId: Int,
    val dept: String,
    val number: String,
    val label: String
)

object CsRequirementsDb {

    val lowerDivision: List<RequirementCourse> = listOf(
        RequirementCourse(
            rowId = R.id.row1,
            dept = "cmpt",
            number = "105w",
            label = "CMPT 105W - Social Issues and Communication Strategies in Computing Science (3)"
        ),
        RequirementCourse(
            rowId = R.id.row2,
            dept = "cmpt",
            number = "120",
            label = "CMPT 120 - Introduction to Computing Science and Programming I (3)"
        ),
        RequirementCourse(
            rowId = R.id.row3,
            dept = "cmpt",
            number = "125",
            label = "CMPT 125 - Introduction to Computing Science and Programming II (3)"
        ),
        RequirementCourse(
            rowId = R.id.row4,
            dept = "cmpt",
            number = "201",
            label = "CMPT 201 - Systems Programming (4)"
        ),
        RequirementCourse(
            rowId = R.id.row5,
            dept = "cmpt",
            number = "210",
            label = "CMPT 210 - Probability and Computing (3)"
        ),
        RequirementCourse(
            rowId = R.id.row6,
            dept = "cmpt",
            number = "225",
            label = "CMPT 225 - Data Structures and Programming (3)"
        ),
        RequirementCourse(
            rowId = R.id.row7,
            dept = "cmpt",
            number = "276",
            label = "CMPT 276 - Introduction to Software Engineering (3)"
        ),
        RequirementCourse(
            rowId = R.id.row8,
            dept = "cmpt",
            number = "295",
            label = "CMPT 295 - Introduction to Computer Systems (4)"
        ),
        RequirementCourse(
            rowId = R.id.row9,
            dept = "macm",
            number = "101",
            label = "MACM 101 - Discrete Mathematics I (3)"
        ),
        RequirementCourse(
            rowId = R.id.row10,
            dept = "stat",
            number = "271",
            label = "STAT 271 - Probability and Statistics for Computing Science (3)"
        )
    )

    val upperDivisionCore: List<RequirementCourse> = listOf(
        RequirementCourse(
            rowId = R.id.row_ud_1,
            dept = "cmpt",
            number = "376w",
            label = "CMPT 376W - Professional Responsibility and Technical Writing (3)"
        )
    )

    fun allCourses(): List<RequirementCourse> = lowerDivision + upperDivisionCore
}
