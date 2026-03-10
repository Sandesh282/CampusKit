package com.example.campuskit.domain.attendance

import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max

/**
 * Pure domain logic for attendance calculations.
 *
 * Stateless use case that computes attendance percentages, safe bunk counts,
 * and recovery class counts, then classifies the result as an [AttendanceStatus].
 */
class CalculateSafeBunksUseCase @Inject constructor() {
    
    /** Returns the attendance percentage (0–100). Returns 100 if [total] is 0. */
    fun calculatePercentage(attended: Int, total: Int): Float {
        if (total == 0) return 100f
        return (attended.toFloat() / total.toFloat()) * 100f
    }

    /** Returns how many classes can be safely skipped while staying at or above [minPercentage]. */
    fun calculateSafeBunks(attended: Int, total: Int, minPercentage: Float): Int {
        if (total == 0) return 0
        val minFraction = minPercentage / 100f
        val safeBunks = floor((attended.toFloat() - minFraction * total.toFloat()) / minFraction).toInt()
        return max(0, safeBunks)
    }

    /** Returns the number of consecutive classes to attend to reach [minPercentage]. */
    fun calculateClassesNeeded(attended: Int, total: Int, minPercentage: Float): Int {
        if (total == 0) return 0
        val minFraction = minPercentage / 100f
        val currentPercentage = attended.toFloat() / total.toFloat()
        if (currentPercentage >= minFraction) return 0
        var needed = 0
        var att = attended
        var tot = total
        while (att.toFloat() / tot.toFloat() < minFraction) {
            att++
            tot++
            needed++
        }
        return needed
    }

    /** Evaluates overall attendance health and returns the appropriate [AttendanceStatus]. */
    operator fun invoke(attended: Int, total: Int, minPercentage: Float): AttendanceStatus {
        val percentage = calculatePercentage(attended, total)
        val safeBunks = calculateSafeBunks(attended, total, minPercentage)
        val needed = calculateClassesNeeded(attended, total, minPercentage)

        return when {
            total == 0 -> AttendanceStatus.NoData
            percentage >= minPercentage + 10f -> AttendanceStatus.Safe(safeBunks)
            percentage >= minPercentage -> AttendanceStatus.Warning(safeBunks)
            else -> AttendanceStatus.Critical(needed)
        }
    }
}
