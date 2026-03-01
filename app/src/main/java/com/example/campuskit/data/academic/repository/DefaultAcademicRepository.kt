package com.example.campuskit.data.academic.repository

import com.example.campuskit.data.academic.local.dao.OfferingDao
import com.example.campuskit.data.academic.local.dao.ResourceDao
import com.example.campuskit.data.academic.local.dao.SyncMetadataDao
import com.example.campuskit.data.academic.local.entity.SubjectEntity
import com.example.campuskit.data.academic.local.entity.OfferingEntity
import com.example.campuskit.data.academic.local.entity.SyncMetadataEntity
import com.example.campuskit.data.academic.local.entity.toDomain
import com.example.campuskit.data.academic.remote.api.StudentHubApi
import com.example.campuskit.data.academic.remote.dto.toEntityList
import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Resource
import com.example.campuskit.domain.academic.model.Subject
import com.example.campuskit.domain.academic.repository.AcademicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAcademicRepository @Inject constructor(
    private val offeringDao: OfferingDao,
    private val resourceDao: ResourceDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val api: StudentHubApi
) : AcademicRepository {
    
    // Default TTL is 24 hours
    private val defaultTtlMillis: Long = 24 * 60 * 60 * 1000L

    override fun getSubjects(program: Program, semester: Int): Flow<List<Subject>> = flow {
        // Trigger background sync if stale
        try {
            triggerBatchSyncIfStale(program, semester, force = false)
        } catch (e: Exception) {
            // Ignore network errors, emit cache
        }
        
        // Emit from Room
        emitAll(
            offeringDao.getOfferingsWithSubjectsFlow(program.name, semester).map { list ->
                list.map { subjectOffering ->
                    subjectOffering.subject.toDomain()
                }
            }
        )
    }

    override fun getResources(subjectCode: String): Flow<List<Resource>> {
        return resourceDao.getResourcesForSubjectFlow(subjectCode).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun syncResources(program: Program, semester: Int) {
        triggerBatchSyncIfStale(program, semester, force = true)
    }
    
    private suspend fun triggerBatchSyncIfStale(program: Program, semester: Int, force: Boolean) {
        val syncKey = "sync_${program.name}_$semester"
        val lastSyncInfo = syncMetadataDao.getLastSync(syncKey)
        val now = System.currentTimeMillis()
        
        val isStale = lastSyncInfo == null || (now - lastSyncInfo.lastSyncedAt > defaultTtlMillis)
        
        if (force || isStale) {
            val response = api.getPapersBySemester(semester = semester, program = program.name)
            
            if (response.isSuccessful) {
                val papersDto = response.body()?.papers?.papers ?: emptyList()
                val resourceEntities = papersDto.toEntityList()
                
                // Extract implicit subjects and offerings from the resources to populate DB
                val subjectMap = mutableMapOf<String, SubjectEntity>()
                val offeringMap = mutableMapOf<String, OfferingEntity>()
                
                papersDto.forEach { dto ->
                    val subjectCodeStr = dto.subject ?: "UNKNOWN"
                    // Add to subjects
                    subjectMap[subjectCodeStr] = SubjectEntity(
                        code = subjectCodeStr,
                        name = dto.subject ?: "Unknown Subject",
                        description = null
                    )
                    
                    // Add to offerings
                    val offeringId = "${program.name}-$semester-$subjectCodeStr"
                    offeringMap[offeringId] = OfferingEntity(
                        id = offeringId,
                        subjectCode = subjectCodeStr,
                        program = program.name,
                        semester = semester,
                        isElective = false // Simplified
                    )
                }
                
                // Clear old resources for this semester mapping
                resourceDao.deleteResourcesForSemester(program.name, semester)
                
                // Upsert Subjects & Offerings
                offeringDao.upsertSubjects(subjectMap.values.toList())
                offeringDao.upsertOfferings(offeringMap.values.toList())
                
                // Upsert Resources
                resourceDao.upsert(resourceEntities)
                
                // Update Sync TTL metadata
                syncMetadataDao.upsert(SyncMetadataEntity(syncKey, now))
            } else {
                throw Exception("Failed to fetch resources: ${response.code()}")
            }
        }
    }
}
