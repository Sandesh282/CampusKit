package com.example.campuskit.data.mess

import java.time.DayOfWeek

data class DayMenu(
    val day: DayOfWeek,
    val breakfast: List<String>,
    val lunch: List<String>,
    val snacks: List<String>,
    val dinner: List<String>,
)

object MessMenuData {
    fun getWeeklyMenu(): List<DayMenu> = listOf(
        DayMenu(
            day = DayOfWeek.MONDAY,
            breakfast = listOf("Gobhi Paratha", "Curd", "Milk", "Tea", "Banana/Egg (1pc)", "Bread Butter/jam"),
            lunch = listOf("Spicy Chhola", "Poori", "Curd", "Jeera Rice", "Salad"),
            snacks = listOf("Maggi", "Tea"),
            dinner = listOf("Arahar Dal Fry", "Aaloo Patta Gobhi", "Roti", "Rice", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.TUESDAY,
            breakfast = listOf("Medu Vada", "Coconut Chutney", "Sambhar", "Milk", "Tea", "Banana/Egg", "Bread", "Butter/jam"),
            lunch = listOf("Rajma Masala", "Boondi Raita", "Roti", "Jeera Rice", "Salad"),
            snacks = listOf("Poha", "Sauce", "Tea"),
            dinner = listOf("Chana Tadka Dal", "Aloo Gobhi Matar", "Roti", "Rice", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.WEDNESDAY,
            breakfast = listOf("Aloo Bhaji", "Puri", "Milk", "Tea", "Banana/Egg", "Bread", "Butter/jam"),
            lunch = listOf("Mix Veg", "Roti", "Rice", "Kala Chana Aaloo", "Salad"),
            snacks = listOf("Dabeli", "Chutney", "Tea"),
            dinner = listOf("Mushroom", "Roti", "Zeera Rice", "Mix Dal Fry", "Jalebi/Kheer", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.THURSDAY,
            breakfast = listOf("Aloo Pyaz Paratha", "Curd", "Banana/Egg", "Milk", "Tea", "Bread", "Butter/Jam"),
            lunch = listOf("Aaloo Pyaz Bhujia", "Dal Makhani", "Rice", "Roti", "Salad"),
            snacks = listOf("Chola Samosa", "Imli Chutney", "Tea"),
            dinner = listOf("Rajma Masala", "Rice", "Roti", "Raita", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.FRIDAY,
            breakfast = listOf("Idli Sambhar", "Coconut Chutney", "Banana/Egg (1pc)", "Bread Butter Jam", "Milk", "Tea"),
            lunch = listOf("Aloo Palak", "Arahar Dal Fry", "Roti", "Rice", "Salad"),
            snacks = listOf("Aloo Tikki Matar Chaat", "Dahi", "Chutney"),
            dinner = listOf("Bhandara style Sabji", "Poori", "Black Masoor Dal", "Rice", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.SATURDAY,
            breakfast = listOf("Methi Paratha", "White Matar", "Milk", "Tea", "Banana/Egg", "Bread", "Butter/jam"),
            lunch = listOf("Pindi Choley", "Bathure", "Rice", "Boondi Raita", "Salad"),
            snacks = listOf("50-50 Biscuit", "Tea"),
            dinner = listOf("Kala Chana Aloo", "Arhar Dal Fry", "Roti", "Jeera Rice", "Salad"),
        ),
        DayMenu(
            day = DayOfWeek.SUNDAY,
            breakfast = listOf("Utappam", "Sambhar", "Coconut Chutney", "Banana/Egg", "Milk", "Tea", "Bread", "Butter/Jam"),
            lunch = listOf("Vegetable Biryani", "Dal Makhni", "Tawa Paratha", "Raita", "Salad"),
            snacks = listOf("Sandwich (2 Pcs)", "Sauce", "Tea"),
            dinner = listOf("Fruit Custurd", "Jeera Rice", "Roti", "Kadhai Paneer", "Punjabi Dal Tadka", "Salad"),
        ),
    )

    fun getTodayMenu(): DayMenu {
        val today = java.time.LocalDate.now().dayOfWeek
        return getWeeklyMenu().first { it.day == today }
    }
}
