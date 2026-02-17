package com.example.campuskit.domain.attendance

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSafeBunksUseCaseTest {

    private val useCase = CalculateSafeBunksUseCase()

    @Test
    fun `calculatePercentage returns 100 when total is 0`() {
        val result = useCase.calculatePercentage(0, 0)
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `calculatePercentage returns correct value`() {
        val result = useCase.calculatePercentage(3, 4)
        assertEquals(75f, result, 0.01f)
    }

    @Test
    fun `calculateSafeBunks returns 0 when total is 0`() {
        val result = useCase.calculateSafeBunks(0, 0, 75f)
        assertEquals(0, result)
    }

    @Test
    fun `calculateSafeBunks returns correct value when over limit`() {
        // 10/10 = 100%. Min 75%.
        // (10 - 0.75 * 10) / 0.75 = (10 - 7.5) / 0.75 = 2.5 / 0.75 = 3.33 -> 3
        val result = useCase.calculateSafeBunks(10, 10, 75f)
        assertEquals(3, result)
    }

    @Test
    fun `calculateSafeBunks returns 0 when at limit`() {
        // 3/4 = 75%. Min 75%.
        val result = useCase.calculateSafeBunks(3, 4, 75f)
        assertEquals(0, result)
    }

    @Test
    fun `calculateClassesNeeded returns correct value when below limit`() {
        // 2/4 = 50%. Min 75%.
        // Need to attend 4 more classes: (2+4)/(4+4) = 6/8 = 75%
        val result = useCase.calculateClassesNeeded(2, 4, 75f)
        assertEquals(4, result)
    }

    @Test
    fun `invoke returns Safe status when percentage is high`() {
        // 10/10 = 100%. Min 75%. 100 >= 75+10.
        val result = useCase(10, 10, 75f)
        assert(result is AttendanceStatus.Safe)
        assertEquals(3, (result as AttendanceStatus.Safe).canBunk)
    }

    @Test
    fun `invoke returns Warning status when percentage is close to limit`() {
        // 8/10 = 80%. Min 75%. 80 < 75+10 but 80 >= 75.
        val result = useCase(8, 10, 75f)
        assert(result is AttendanceStatus.Warning)
        assertEquals(0, (result as AttendanceStatus.Warning).canBunk)
    }

    @Test
    fun `invoke returns Critical status when percentage is below limit`() {
        // 7/10 = 70%. Min 75%.
        val result = useCase(7, 10, 75f)
        assert(result is AttendanceStatus.Critical)
        assertEquals(2, (result as AttendanceStatus.Critical).needToAttend)
    }
}
