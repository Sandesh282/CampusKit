package com.example.campuskit.data.mess

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessRepository @Inject constructor(
    private val yuckDao: YuckItemDao
) {
    fun getAllYuckItems(): Flow<List<YuckItemEntity>> = yuckDao.getAll()

    suspend fun insertYuckItem(item: YuckItemEntity) = yuckDao.insert(item)

    suspend fun deleteYuckItemByName(name: String) = yuckDao.deleteByName(name)
}
