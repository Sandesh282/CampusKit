package com.example.campuskit.data.academic.local.entity

import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Resource
import com.example.campuskit.domain.academic.model.ResourceType
import com.example.campuskit.domain.academic.model.Subject
import com.example.campuskit.domain.academic.model.Offering

fun SubjectEntity.toDomain() = Subject(
    code = this.code,
    name = this.name,
    description = this.description
)

fun OfferingEntity.toDomain() = Offering(
    id = this.id,
    subjectCode = this.subjectCode,
    program = try { Program.valueOf(this.program) } catch (e: Exception) { Program.UNKNOWN },
    semester = this.semester,
    isElective = this.isElective
)

fun ResourceEntity.toDomain(): Resource {
    return if (this.type == ResourceType.PYP.name) {
        Resource.PastYearPaper(
            id = this.id,
            subjectCode = this.subjectCode,
            title = this.title,
            url = this.url,
            year = this.year ?: 0,
            examType = this.examType ?: "Unknown"
        )
    } else {
        Resource.Note(
            id = this.id,
            subjectCode = this.subjectCode,
            title = this.title,
            url = this.url,
            author = this.author
        )
    }
}
