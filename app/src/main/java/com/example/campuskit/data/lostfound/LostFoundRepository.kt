package com.example.campuskit.data.lostfound

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LostFoundRepository @Inject constructor(
    private val lostFoundDao: LostFoundDao,
) {

    /** Returns all items as domain models, newest first. */
    fun getItems(): Flow<List<LostFoundItem>> = lostFoundDao.getAllItems().map { entities ->
        entities.map { it.toDomain() }
    }

    /** Adds a new lost-and-found item to the database. */
    suspend fun addItem(item: LostFoundItem) {
        lostFoundDao.insert(item.toEntity())
    }

    /** Seeds mock items into the database if it's empty (first launch). */
    suspend fun seedIfEmpty() {
        if (lostFoundDao.getCount() == 0) {
            val seedEntities = MockLostFound.getItems().map { it.toEntity() }
            lostFoundDao.insertAll(seedEntities)
        }
    }

    /** Deletes the item with the given [itemId]. */
    suspend fun deleteItem(itemId: String) {
        lostFoundDao.deleteById(itemId)
    }
}

/** Converts a Room [LostFoundEntity] → domain [LostFoundItem]. */
private fun LostFoundEntity.toDomain() = LostFoundItem(
    id = id,
    itemName = itemName,
    foundLocation = foundLocation,
    imageUrls = if (imageUrlsCsv.isBlank()) emptyList() else imageUrlsCsv.split(","),
    timestamp = timestamp,
    contactWhatsApp = contactWhatsApp,
    contactTelegram = contactTelegram,
    contactPhone = contactPhone,
    description = description,
)

/** Converts a domain [LostFoundItem] into a Room [LostFoundEntity] for persistence. → [LostFoundEntity]. */
private fun LostFoundItem.toEntity() = LostFoundEntity(
    id = id,
    itemName = itemName,
    foundLocation = foundLocation,
    imageUrlsCsv = imageUrls.joinToString(","),
    timestamp = timestamp,
    contactWhatsApp = contactWhatsApp,
    contactTelegram = contactTelegram,
    contactPhone = contactPhone,
    description = description,
)
