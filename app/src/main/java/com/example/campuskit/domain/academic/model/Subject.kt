package com.example.campuskit.domain.academic.model

/**
 * Core Subject Identity (Agnostic to where/when it is taught)
 */
data class Subject(
    val code: String,       // e.g., "CS101"
    val name: String,       // e.g., "Data Structures"
    val description: String? = null
)
