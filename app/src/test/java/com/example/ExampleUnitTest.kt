package com.example

import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.data.local.InitialDemoData
import com.example.ui.screens.explore.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ExampleUnitTest {

    @Test
    fun `currency formatting handles whole and fractional dollars correctly`() {
        assertEquals("$45", CurrencyFormatter.formatCents(4500L))
        assertEquals("$30", CurrencyFormatter.formatCents(3000L))
        assertEquals("$38.50", CurrencyFormatter.formatCents(3850L))
        assertEquals("$75", CurrencyFormatter.formatDollars(75))
    }

    @Test
    fun `date time utils generates consecutive upcoming days`() {
        val days = DateTimeUtils.getUpcomingDays(14)
        assertEquals(14, days.size)
        assertEquals(LocalDate.now(), days.first())
        assertEquals(LocalDate.now().plusDays(1), days[1])
    }

    @Test
    fun `date time utils formats time slot cleanly`() {
        val time = LocalTime.of(10, 0)
        val formatted = DateTimeUtils.formatTimeSlot(time)
        assertEquals("10:00 AM", formatted)

        val afternoonTime = LocalTime.of(14, 30)
        val afternoonFormatted = DateTimeUtils.formatTimeSlot(afternoonTime)
        assertEquals("02:30 PM", afternoonFormatted)
    }

    @Test
    fun `service inclusions split correctly from entity`() {
        val haircut = InitialDemoData.services.first { it.id == "srv_haircut" }
        val inclusions = haircut.getInclusionsList()
        assertEquals(4, inclusions.size)
        assertTrue(inclusions.contains("Tailored consultation"))
    }

    @Test
    fun `service filtering by category and search query works as expected`() {
        val services = InitialDemoData.services

        // Category filter: Nails
        val nailsServices = services.filter { it.category.equals("Nails", ignoreCase = true) }
        assertEquals(1, nailsServices.size)
        assertEquals("srv_manicure", nailsServices.first().id)

        // Search query: "massage"
        val massageMatches = services.filter {
            it.name.contains("massage", ignoreCase = true) || it.description.contains("massage", ignoreCase = true)
        }
        assertTrue(massageMatches.any { it.id == "srv_massage" })

        // Sorting: Price low to high
        val sortedByPrice = services.sortedBy { it.priceCents }
        assertEquals("srv_manicure", sortedByPrice.first().id) // $30
        assertEquals("srv_massage", sortedByPrice.last().id) // $75
    }
}
