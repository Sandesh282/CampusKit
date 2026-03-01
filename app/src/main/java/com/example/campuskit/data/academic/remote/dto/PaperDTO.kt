package com.example.campuskit.data.academic.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaperRequestDTO(
    @Json(name = "limit") val limit: Int = 1000,
    @Json(name = "papers") val papers: List<PaperDTO>
)

@JsonClass(generateAdapter = true)
data class PaperResponseDTO(
    @Json(name = "papers") val papers: PaperRequestDTO
)

@JsonClass(generateAdapter = true)
data class PaperDTO(
    @Json(name = "_id") val _id: String,
    @Json(name = "subject") val subject: String?,
    @Json(name = "facultyName") val facultyName: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "file_name") val file_name: String,
    @Json(name = "document_url") val document_url: String,
    @Json(name = "year") val year: String, // Year comes as string in frontend, e.g. "2024"
    @Json(name = "semester") val semester: Int?,
    @Json(name = "term") val term: String // e.g. "Mid", "Class_test_1"
)
