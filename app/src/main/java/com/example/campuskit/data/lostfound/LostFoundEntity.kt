package com.example.campuskit.data.lostfound

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a lost-and-found item posting.
 *
 * Contact fields are nullable since a poster may provide only
 * one or two contact methods. [imageUrls] is stored as a
 * comma-separated string for simplicity.
 */
@Entity(tableName = "lost_found")
data class LostFoundEntity(
    @PrimaryKey val id: String,
    val itemName: String,
    val foundLocation: String,
    /** Comma-separated image URLs. Empty string means no images. */
    val imageUrlsCsv: String = "",
    val timestamp: String,
    val contactWhatsApp: String? = null,
    val contactTelegram: String? = null,
    val contactPhone: String? = null,
    val description: String = "",
)
