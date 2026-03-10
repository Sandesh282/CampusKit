package com.example.campuskit.data.campusguide

/**
 * Represents a nearby location around campus.
 *
 * @param name location name
 * @param category type (Restaurant, Hotel, Shop, Landmarks)
 * @param address street address
 * @param mapLink Google Maps link
 * @param distance approximate distance from campus
 */
data class NearbyPlace(
    val name: String,
    val category: String,
    val address: String,
    val mapLink: String,
    val distance: String,
)

/** Static catalog of nearby locations, sourced from StudentHub's nearby_data.json. */
object CampusGuideCatalog {

    fun getAll(): List<NearbyPlace> = listOf(
        // — Restaurants —
        NearbyPlace("Imperial Hotel", "Restaurant", "123 Main St, City Center", "https://maps.app.goo.gl/snM5v9vSPQ3inEo8A", "16km"),
        NearbyPlace("Moti Mahal", "Restaurant", "Hazratganj", "https://maps.app.goo.gl/8b6JvxfRNngLhy4h6", "13km"),
        NearbyPlace("Mishri Malai", "Restaurant", "Palassio Mall, Ahmamau", "https://maps.app.goo.gl/R2andYqupZE6rUbX8", "2.1km"),
        NearbyPlace("Royal Cafe", "Restaurant", "51, Mahatma Gandhi Marg, Hazratganj", "https://maps.app.goo.gl/qkK8yf8J6JNZYDBV6", "14km"),
        NearbyPlace("Curry Leaf", "Restaurant", "Jaynagar 3rd Block, Hazratganj", "https://maps.app.goo.gl/7doNzvSe5ioiDT5FA", "14km"),

        // — Hotels —
        NearbyPlace("Dastarkhwan", "Hotel", "Aminabad", "https://maps.app.goo.gl/34wZgaFGsfxCeDQj8", "14km"),
        NearbyPlace("Surya Continental", "Hotel", "Ahmamau, Sultanpur Road", "https://maps.app.goo.gl/3RC5Kn8tryYDeXHJA", "4.4km"),
        NearbyPlace("Hotel Meghalaya", "Hotel", "Gomti Nagar, Ardonamau", "https://maps.app.goo.gl/mwG4aFGJVoHDF6ko8", "2.4km"),
        NearbyPlace("Prithvi Resort", "Hotel", "Ahmamau, Sultanpur Road", "https://maps.app.goo.gl/MFwfQeuYd8b8HvX77", "3.5km"),

        // — Shopping —
        NearbyPlace("LuLu Mall", "Shop", "101 Market St, Shopping District", "https://maps.app.goo.gl/rc1bpU9zSCPUpboM6", "5.4km"),
        NearbyPlace("Palassio Mall", "Shop", "Ahmamau, Sultanpur Road", "https://maps.app.goo.gl/yXX3fZZgcqcDWY5N6", "2.6km"),
        NearbyPlace("Janpath Market", "Shop", "Sushanpura, Hazratganj", "https://maps.app.goo.gl/HmWxtLzjUxzojLVz8", "12km"),

        // — Landmarks —
        NearbyPlace("DR BR Ambedkar Park", "Landmark", "789 Heritage Lane, Old Town", "https://maps.app.goo.gl/FXhyBdNQ9C5PQb737", "11km"),
        NearbyPlace("Chhota Imambada", "Landmark", "303 Innovation Blvd, Tech District", "https://maps.app.goo.gl/ojNq7t6LZF3EKg7NA", "21km"),
        NearbyPlace("Janeshwar Mishra Park", "Landmark", "Gomti Nagar, Lucknow", "https://maps.app.goo.gl/2MSJFpfmQ9FMQAYh8", "8km"),
    )

    fun getCategories(): List<String> = getAll().map { it.category }.distinct()
}
