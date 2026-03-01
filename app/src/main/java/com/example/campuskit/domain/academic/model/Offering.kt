package com.example.campuskit.domain.academic.model

/**
 * Placement Layer (Defines when/where a subject is offered)
 */
data class Offering(
    val id: String,         // Synthetic ID, e.g., "IT-3-CS101"
    val subjectCode: String,
    val program: Program,
    val semester: Semester,
    val isElective: Boolean = false
)
