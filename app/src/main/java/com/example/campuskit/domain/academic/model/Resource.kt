package com.example.campuskit.domain.academic.model

enum class ResourceType { NOTES, PYP, SLIDES, SYLLABUS, OTHER }

/**
 * Polymorphic Resource Model
 */
sealed class Resource {
    abstract val id: String
    abstract val subjectCode: String
    abstract val title: String
    abstract val type: ResourceType
    abstract val url: String
    
    data class PastYearPaper(
        override val id: String,
        override val subjectCode: String,
        override val title: String,
        override val url: String,
        val year: Int,
        val examType: String
    ) : Resource() {
        override val type = ResourceType.PYP
    }

    data class Note(
        override val id: String,
        override val subjectCode: String,
        override val title: String,
        override val url: String,
        val author: String? = null
    ) : Resource() {
        override val type = ResourceType.NOTES
    }
}
