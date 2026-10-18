package com.example.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val zoneId = ZoneId.systemDefault()

    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.US)
    private val dayNumberFormatter = DateTimeFormatter.ofPattern("d", Locale.US)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US)
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
    private val timeSlotFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    fun toEpochMillis(dateTime: LocalDateTime): Long {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    fun fromEpochMillis(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), zoneId)
    }

    fun formatFullDate(epochMillis: Long): String {
        return fullDateFormatter.format(fromEpochMillis(epochMillis))
    }

    fun formatShortDate(epochMillis: Long): String {
        return shortDateFormatter.format(fromEpochMillis(epochMillis))
    }

    fun formatTime(epochMillis: Long): String {
        return timeSlotFormatter.format(fromEpochMillis(epochMillis))
    }

    fun formatTimeSlot(time: LocalTime): String {
        return timeSlotFormatter.format(time)
    }

    fun formatMonthYear(date: LocalDate): String {
        return monthYearFormatter.format(date)
    }

    fun formatDayOfWeek(date: LocalDate): String {
        return dayOfWeekFormatter.format(date)
    }

    fun formatDayNumber(date: LocalDate): String {
        return dayNumberFormatter.format(date)
    }

    fun getUpcomingDays(count: Int = 14): List<LocalDate> {
        val today = LocalDate.now(zoneId)
        return (0 until count).map { today.plusDays(it.toLong()) }
    }
}
