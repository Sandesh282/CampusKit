package com.example.campuskit.data.mess

import java.time.DayOfWeek

data class DayMenu(
    val day: DayOfWeek,
    val breakfast: List<String>,
    val lunch: List<String>,
    val dinner: List<String>,
)

object MessMenuData {
    fun getWeeklyMenu(): List<DayMenu> = listOf(
        DayMenu(
            day = DayOfWeek.MONDAY,
            breakfast = listOf("Aloo Paratha", "Curd", "Tea", "Bread & Butter"),
            lunch = listOf("Rice", "Dal Tadka", "Aloo Gobi", "Roti", "Salad"),
            dinner = listOf("Rice", "Rajma", "Tinda", "Roti", "Kheer"),
        ),
        DayMenu(
            day = DayOfWeek.TUESDAY,
            breakfast = listOf("Poha", "Boiled Eggs", "Tea", "Banana"),
            lunch = listOf("Rice", "Sambar", "Bhindi Fry", "Roti", "Raita"),
            dinner = listOf("Rice", "Kadhi Pakora", "Lauki", "Roti", "Gulab Jamun"),
        ),
        DayMenu(
            day = DayOfWeek.WEDNESDAY,
            breakfast = listOf("Chole Bhature", "Tea", "Sprouts"),
            lunch = listOf("Rice", "Dal Makhani", "Mix Veg", "Roti", "Salad"),
            dinner = listOf("Rice", "Paneer Butter Masala", "Torai", "Roti", "Ice Cream"),
        ),
        DayMenu(
            day = DayOfWeek.THURSDAY,
            breakfast = listOf("Idli Sambar", "Vada", "Tea", "Bread & Jam"),
            lunch = listOf("Rice", "Arhar Dal", "Aloo Matar", "Roti", "Pickle"),
            dinner = listOf("Rice", "Chicken Curry / Soya Curry", "Jeera Aloo", "Roti", "Fruit"),
        ),
        DayMenu(
            day = DayOfWeek.FRIDAY,
            breakfast = listOf("Puri Sabzi", "Tea", "Cornflakes"),
            lunch = listOf("Rice", "Chana Dal", "Cabbage Sabzi", "Roti", "Salad"),
            dinner = listOf("Rice", "Egg Curry / Paneer Do Pyaza", "Tinda", "Roti", "Halwa"),
        ),
        DayMenu(
            day = DayOfWeek.SATURDAY,
            breakfast = listOf("Moong Dal Cheela", "Chutney", "Tea", "Milk"),
            lunch = listOf("Biryani / Veg Pulao", "Raita", "Salan", "Roti"),
            dinner = listOf("Rice", "Dal Fry", "Lauki Chana", "Roti", "Custard"),
        ),
        DayMenu(
            day = DayOfWeek.SUNDAY,
            breakfast = listOf("Aloo Paratha", "Curd", "Tea", "Juice"),
            lunch = listOf("Rice", "Rajma", "Matar Paneer", "Roti", "Sweet"),
            dinner = listOf("Rice", "Dal Tadka", "Mixed Veg", "Roti", "Rasgulla"),
        ),
    )

    fun getTodayMenu(): DayMenu {
        val today = java.time.LocalDate.now().dayOfWeek
        return getWeeklyMenu().first { it.day == today }
    }
}
