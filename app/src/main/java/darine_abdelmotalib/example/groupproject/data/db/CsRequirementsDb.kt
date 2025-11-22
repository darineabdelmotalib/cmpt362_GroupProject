package darine_abdelmotalib.example.groupproject.data.db

import darine_abdelmotalib.example.groupproject.R

data class RequirementCourse(
    val rowId: Int,
    val dept: String,
    val number: String,
    val label: String
)

sealed class RequirementGroup {
    data class CourseList(val title: String, val courses: List<RequirementCourse>) : RequirementGroup()
    data class OrGroup(val title: String, val options: List<CourseList>) : RequirementGroup()
}

object CsRequirementsDb {

    val lowerDivision: List<RequirementCourse> = listOf(
        RequirementCourse(R.id.row1, "cmpt", "105w", "CMPT 105W - Social Issues and Communication Strategies in Computing Science (3)"),
        RequirementCourse(R.id.row2, "cmpt", "120", "CMPT 120 - Introduction to Computing Science and Programming I (3)"),
        RequirementCourse(R.id.row3, "cmpt", "125", "CMPT 125 - Introduction to Computing Science and Programming II (3)"),
        RequirementCourse(R.id.row4, "cmpt", "201", "CMPT 201 - Systems Programming (4)"),
        RequirementCourse(R.id.row5, "cmpt", "210", "CMPT 210 - Probability and Computing (3)"),
        RequirementCourse(R.id.row6, "cmpt", "225", "CMPT 225 - Data Structures and Programming (3)"),
        RequirementCourse(R.id.row7, "cmpt", "276", "CMPT 276 - Introduction to Software Engineering (3)"),
        RequirementCourse(R.id.row8, "cmpt", "295", "CMPT 295 - Introduction to Computer Systems (4)"),
        RequirementCourse(R.id.row9, "macm", "101", "MACM 101 - Discrete Mathematics I (3)"),
        RequirementCourse(R.id.row10, "stat", "271", "STAT 271 - Probability and Statistics for Computing Science (3)")
    )

    private fun rc(num: String, label: String) =
        RequirementCourse(0, "cmpt", num, label)

    val upperCore = RequirementGroup.CourseList(
        "Upper Division Core (Both Required)",
        listOf(
            rc("307", "CMPT 307 - Data Structures and Algorithms (3)"),
            rc("376W", "CMPT 376W - Professional Responsibility and Technical Writing (3)")
        )
    )

    val systemsReq = RequirementGroup.CourseList(
        "Systems Requirement (Choose 12 units)",
        listOf(
            rc("303", "CMPT 303 - Computing Systems (3)"),
            rc("354", "CMPT 354 - Database Systems I (3)"),
            rc("371", "CMPT 371 - Data Communications and Networking (3)"),
            rc("372", "CMPT 372 - Web II — Server-side Development (3)"),
            rc("431", "CMPT 431 - Distributed Systems (3)"),
            rc("433", "CMPT 433 - Embedded Systems (3)"),
            rc("454", "CMPT 454 - Database Systems II (3)"),
            rc("471", "CMPT 471 - Networking II (3)")
        )
    )

    val softEngReq = RequirementGroup.OrGroup(
        "Software Engineering Requirement (12 units)",
        options = listOf(
            RequirementGroup.CourseList(
                "Both Required",
                listOf(
                    rc("373", "CMPT 373 - Software Development Methods (3)"),
                    rc("473", "CMPT 473 - Software Testing, Reliability and Security (3)")
                )
            ),
            RequirementGroup.CourseList(
                "Pick At Least Two",
                listOf(
                    rc("379", "CMPT 379 - Principles of Compiler Design (3)"),
                    rc("383", "CMPT 383 - Comparative Programming Languages (3)"),
                    rc("384", "CMPT 384 - Symbolic Computing (3)"),
                    rc("474", "CMPT 474 - Web Systems Architecture (3)"),
                    rc("477", "CMPT 477 - Introduction to Formal Verification (3)")
                )
            )
        )
    )

    val capstone = RequirementGroup.OrGroup(
        "Capstone Requirement",
        options = listOf(
            RequirementGroup.CourseList(
                "Option 1: CMPT 494 + CMPT 495",
                listOf(
                    rc("494", "CMPT 494 - Capstone I (3)"),
                    rc("495", "CMPT 495 - Capstone II (3)")
                )
            ),
            RequirementGroup.CourseList(
                "Option 2: Pick Two",
                listOf(
                    rc("379", "CMPT 379 - Principles of Compiler Design (3)"),
                    rc("431", "CMPT 431 - Distributed Systems (3)"),
                    rc("433", "CMPT 433 - Embedded Systems (3)")
                )
            )
        )
    )

    val upperGroups = listOf(upperCore, systemsReq, softEngReq, capstone)
}
