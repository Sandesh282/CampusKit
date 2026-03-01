package com.example.campuskit.domain.academic.repository

import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Resource
import com.example.campuskit.domain.academic.model.Subject
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    /**
     * Emits a locally cached list of subjects for a given program and semester.
     * Triggers a background network sync if data is stale.
     */
    fun getSubjects(program: Program, semester: Int): Flow<List<Subject>>
    
    /**
     * Emits locally cached resources for a subject.
     * Does not trigger network fetch (handled by getSubjects batch sync).
     */
    fun getResources(subjectCode: String): Flow<List<Resource>>
    
    /**
     * Forces a pull-to-refresh sync for the given program and semester.
     */
    suspend fun syncResources(program: Program, semester: Int)
}
