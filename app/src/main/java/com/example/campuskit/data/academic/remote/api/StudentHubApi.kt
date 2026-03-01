package com.example.campuskit.data.academic.remote.api

import com.example.campuskit.data.academic.remote.dto.PaperResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StudentHubApi {
    // Fetch all papers for a specific program and semester layout
    @GET("/api/papers")
    suspend fun getPapersBySemester(
        @Query("semester") semester: Int,
        @Query("program") program: String,
        @Query("limit") limit: Int = 1000
    ): Response<PaperResponseDTO>
    
    // Notes endpoint placeholder
    @GET("/api/notes")
    suspend fun getNotesBySemester(
        @Query("semester") semester: Int,
        @Query("program") program: String
    ): Response<Any> // Replace Any with NotesResponseDTO when defined
}
