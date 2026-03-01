package com.example.campuskit.data.academic.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectOffering(
    @Embedded val offering: OfferingEntity,
    @Relation(
        parentColumn = "subjectCode",
        entityColumn = "code"
    )
    val subject: SubjectEntity
)
