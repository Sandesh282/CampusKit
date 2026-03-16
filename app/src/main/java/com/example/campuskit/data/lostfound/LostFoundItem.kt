package com.example.campuskit.data.lostfound

/**
 * Domain model for a lost-and-found item posting.
 *
 * Contact fields are nullable since a poster may only
 * provide one or two contact methods.
 *
 * @param id unique identifier
 * @param itemName name of the lost/found item
 * @param foundLocation where the item was found
 * @param imageUrls optional image URLs
 * @param timestamp human-readable time string
 */
/**
 * Domain model for a lost-and-found item posting.
 *
 * Contact fields are nullable since a poster may only
 * provide one or two contact methods.
 */
data class LostFoundItem(
    val id: String,
    val itemName: String,
    val foundLocation: String,
    val imageUrls: List<String>,
    val timestamp: String,
    val contactWhatsApp: String? = null,
    val contactTelegram: String? = null,
    val contactPhone: String? = null,
    val description: String = "",
)

/** Provides hardcoded lost-and-found items for first-launch demo and seeding. */
object MockLostFound {
    fun getItems(): List<LostFoundItem> = listOf(
        LostFoundItem(
            id = "1",
            itemName = "Blue Water Bottle",
            foundLocation = "Library, 2nd Floor",
            imageUrls = emptyList(),
            timestamp = "2 hours ago",
            contactWhatsApp = "919876543210",
            description = "Milton blue bottle, found near the reading section.",
        ),
        LostFoundItem(
            id = "2",
            itemName = "Scientific Calculator",
            foundLocation = "LHC Room 101",
            imageUrls = emptyList(),
            timestamp = "5 hours ago",
            contactTelegram = "rahul_iiitl",
            contactPhone = "9876543211",
            description = "Casio fx-991EX, found after Maths lecture.",
        ),
        LostFoundItem(
            id = "3",
            itemName = "Black Umbrella",
            foundLocation = "Mess Entrance",
            imageUrls = emptyList(),
            timestamp = "Yesterday",
            contactWhatsApp = "919876543212",
            description = "Found near the shoe rack outside mess.",
        ),
        LostFoundItem(
            id = "4",
            itemName = "Wired Earphones",
            foundLocation = "Computer Center",
            imageUrls = emptyList(),
            timestamp = "Yesterday",
            contactPhone = "9876543213",
            description = "Black earphones found at workstation 15.",
        ),
        LostFoundItem(
            id = "5",
            itemName = "ID Card – Arjun Mehta",
            foundLocation = "Sports Ground",
            imageUrls = emptyList(),
            timestamp = "2 days ago",
            contactWhatsApp = "919876543214",
            contactTelegram = "arjun_m",
            description = "IIITL student ID found near the basketball court.",
        ),
    )
}
