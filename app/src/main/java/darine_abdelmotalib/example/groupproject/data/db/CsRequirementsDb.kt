package darine_abdelmotalib.example.groupproject.data.db

import darine_abdelmotalib.example.groupproject.R

data class RequirementCourse(
    val rowId: Int = 0,
    val dept: String,
    val number: String,
    val label: String
)

sealed class RequirementGroup {
    data class CourseList(val title: String, val courses: List<RequirementCourse>) : RequirementGroup()
    data class OrGroup(val title: String, val options: List<CourseList>) : RequirementGroup()
}

object CsRequirementsDb {

    val lowerDivision = listOf(
        RequirementCourse(R.id.row1, "cmpt", "105w", "CMPT 105W - Social Issues and Communication Strategies in Computing Science"),
        RequirementCourse(R.id.row2, "cmpt", "120", "CMPT 120 - Introduction to Computing Science and Programming I"),
        RequirementCourse(R.id.row3, "cmpt", "125", "CMPT 125 - Introduction to Computing Science and Programming II"),
        RequirementCourse(R.id.row4, "cmpt", "201", "CMPT 201 - Systems Programming"),
        RequirementCourse(R.id.row5, "cmpt", "210", "CMPT 210 - Probability and Computing"),
        RequirementCourse(R.id.row6, "cmpt", "225", "CMPT 225 - Data Structures and Programming"),
        RequirementCourse(R.id.row7, "cmpt", "276", "CMPT 276 - Introduction to Software Engineering"),
        RequirementCourse(R.id.row8, "cmpt", "295", "CMPT 295 - Introduction to Computer Systems"),
        RequirementCourse(R.id.row9, "macm", "101", "MACM 101 - Discrete Mathematics I"),
        RequirementCourse(R.id.row10, "stat", "271", "STAT 271 - Probability and Statistics for Computing Science")
    )

    private fun rc(num: String, label: String) =
        RequirementCourse(0, "cmpt", num, label)

    val upperCore = RequirementGroup.CourseList(
        "Upper Division Core (Both Required)",
        listOf(
            rc("307", "CMPT 307 - Data Structures and Algorithms"),
            rc("376W", "CMPT 376W - Technical Writing & Responsibility")
        )
    )

    val systemsReq = RequirementGroup.CourseList(
        "Systems Requirement (Choose 12 units)",
        listOf(
            rc("303", "CMPT 303 - Computing Systems"),
            rc("354", "CMPT 354 - Database Systems I"),
            rc("371", "CMPT 371 - Data Communications and Networking"),
            rc("372", "CMPT 372 - Web II — Server-side Development"),
            rc("431", "CMPT 431 - Distributed Systems"),
            rc("433", "CMPT 433 - Embedded Systems"),
            rc("454", "CMPT 454 - Database Systems II"),
            rc("471", "CMPT 471 - Networking II")
        )
    )

    val softEngReq = RequirementGroup.OrGroup(
        "Software Engineering Requirement (12 units)",
        listOf(
            RequirementGroup.CourseList(
                "Both Required",
                listOf(
                    rc("373", "CMPT 373 - Software Development Methods"),
                    rc("473", "CMPT 473 - Software Testing, Reliability and Security")
                )
            ),
            RequirementGroup.CourseList(
                "Pick At Least Two",
                listOf(
                    rc("379", "CMPT 379 - Principles of Compiler Design"),
                    rc("383", "CMPT 383 - Comparative Programming Languages"),
                    rc("384", "CMPT 384 - Symbolic Computing"),
                    rc("474", "CMPT 474 - Web Systems Architecture"),
                    rc("477", "CMPT 477 - Introduction to Formal Verification")
                )
            )
        )
    )

    val capstone = RequirementGroup.OrGroup(
        "Capstone Requirement",
        listOf(
            RequirementGroup.CourseList(
                "Option 1: CMPT 494 + CMPT 495",
                listOf(
                    rc("494", "CMPT 494 - Capstone I"),
                    rc("495", "CMPT 495 - Capstone II")
                )
            ),
            RequirementGroup.CourseList(
                "Option 2: Pick Two",
                listOf(
                    rc("379", "CMPT 379 - Principles of Compiler Design"),
                    rc("431", "CMPT 431 - Distributed Systems"),
                    rc("433", "CMPT 433 - Embedded Systems")
                )
            )
        )
    )

    val upperGroups = listOf(
        upperCore,
        systemsReq,
        softEngReq,
        capstone
    )

    fun getAllUniqueCourses(): List<RequirementCourse> {
        val allCourses = lowerDivision.toMutableList()

        fun extractFromGroup(group: RequirementGroup) {
            when (group) {
                is RequirementGroup.CourseList -> {
                    allCourses.addAll(group.courses)
                }
                is RequirementGroup.OrGroup -> {
                    // For "Or" groups, we grab ALL potential courses to count them as possible XP
                    group.options.forEach { option ->
                        allCourses.addAll(option.courses)
                    }
                }
            }
        }

        upperGroups.forEach { extractFromGroup(it) }

        // Remove duplicates (e.g. if a course is listed in two different requirement options)
        return allCourses.distinctBy { it.dept.lowercase() + it.number.lowercase() }
    }
}
