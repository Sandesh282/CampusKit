package com.example.campuskit.data.academic.remote.dto

import com.example.campuskit.data.academic.local.entity.ResourceEntity
import com.example.campuskit.domain.academic.model.ResourceType

fun PaperDTO.toEntity(): ResourceEntity {
    return ResourceEntity(
        id = this._id,
        subjectCode = this.subject ?: "UNKNOWN",
        title = this.title ?: this.file_name,
        type = ResourceType.PYP.name,
        url = this.document_url,
        year = this.year.toIntOrNull(),
        examType = this.term.replace("_", " ").replaceFirstChar { it.uppercase() },
        author = this.facultyName
    )
}

fun List<PaperDTO>.toEntityList(): List<ResourceEntity> {
    return this.map { it.toEntity() }
}
