package com.example.ui.screens.scheduling

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiHeader
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiButtonShape
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiChipShape
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class TimeSlotItem(
    val time: LocalTime,
    val formatted: String,
    val epochMillis: Long,
    val isPast: Boolean,
    val isBooked: Boolean
)

@Composable
fun SchedulingScreen(
    serviceId: String,
    providerId: String,
    repository: LumiRepository,
    onBack: () -> Unit,
    onProceedToSummary: (serviceId: String, providerId: String, timestamp: Long) -> Unit
) {
    val service by repository.getService(serviceId).collectAsState(initial = null)
    val provider by repository.getProvider(providerId).collectAsState(initial = null)

    val upcomingDays = remember { DateTimeUtils.getUpcomingDays(14) }
    var selectedDate by remember { mutableStateOf(upcomingDays.first()) }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlotItem?>(null) }
    var bookedSlotsForDay by remember { mutableStateOf<List<Long>>(emptyList()) }

    // Fetch occupied slots for selected day & provider
    LaunchedEffect(selectedDate, providerId) {
        val zone = ZoneId.systemDefault()
        val dayStart = selectedDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = selectedDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        bookedSlotsForDay = repository.getBookedSlots(providerId, dayStart, dayEnd)
        // Reset selected slot if not valid on new day
        selectedTimeSlot = null
    }

    val standardSlots = remember {
        listOf(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            LocalTime.of(13, 0),
            LocalTime.of(14, 30),
            LocalTime.of(16, 0),
            LocalTime.of(17, 30)
        )
    }

    val availableSlots = remember(selectedDate, bookedSlotsForDay) {
        val now = LocalDateTime.now()
        val zone = ZoneId.systemDefault()

        standardSlots.map { slotTime ->
            val slotDateTime = LocalDateTime.of(selectedDate, slotTime)
            val epoch = slotDateTime.atZone(zone).toInstant().toEpochMilli()
            val isPast = slotDateTime.isBefore(now.plusMinutes(15))
            val isBooked = bookedSlotsForDay.any { Math.abs(it - epoch) < 30 * 60 * 1000L }

            TimeSlotItem(
                time = slotTime,
                formatted = DateTimeUtils.formatTimeSlot(slotTime),
                epochMillis = epoch,
                isPast = isPast,
                isBooked = isBooked
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("scheduling_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Choose your time",
                subtitle = "Select date and available time slot",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Service Summary Pill
                item {
                    if (service != null && provider != null) {
                        Card(
                            shape = LumiCardShape,
                            colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                            border = BorderStroke(1.dp, LumiBorderDivider),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service!!.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LumiCharcoal
                                    )
                                    Text(
                                        text = "With ${provider!!.name} • ${service!!.durationMinutes} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LumiMutedGray
                                    )
                                }
                                Text(
                                    text = CurrencyFormatter.formatCents(service!!.priceCents, service!!.currency),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LumiPlum
                                    )
                                )
                            }
                        }
                    }
                }

                // Month header
                item {
                    Text(
                        text = DateTimeUtils.formatMonthYear(selectedDate),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Horizontal Date Selector
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(upcomingDays) { date ->
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()

                            Surface(
                                shape = LumiCardShape,
                                color = if (isSelected) LumiPlum else LumiSurfaceWhite,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) LumiPlum else LumiBorderDivider
                                ),
                                modifier = Modifier
                                    .size(width = 64.dp, height = 82.dp)
                                    .clip(LumiCardShape)
                                    .clickable {
                                        selectedDate = date
                                    }
                                    .testTag("date_item_${date.dayOfMonth}")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Text(
                                        text = DateTimeUtils.formatDayOfWeek(date).uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = if (isSelected) LumiSurfaceWhite.copy(alpha = 0.8f) else LumiMutedGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = DateTimeUtils.formatDayNumber(date),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isSelected) LumiSurfaceWhite else LumiCharcoal
                                    )
                                    if (isToday) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) LumiSurfaceWhite else LumiPlum)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Time Slots Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available time slots",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LumiCharcoal
                        )
                        Text(
                            text = "${availableSlots.count { !it.isPast && !it.isBooked }} slots open",
                            style = MaterialTheme.typography.bodySmall,
                            color = LumiMutedGray
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Time Slots Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val chunked = availableSlots.chunked(2)
                        chunked.forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    val isSelected = selectedTimeSlot?.epochMillis == slot.epochMillis
                                    val isAvailable = !slot.isPast && !slot.isBooked

                                    Surface(
                                        shape = LumiChipShape,
                                        color = when {
                                            isSelected -> LumiPlum
                                            !isAvailable -> LumiWarmGray.copy(alpha = 0.6f)
                                            else -> LumiSurfaceWhite
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            when {
                                                isSelected -> LumiPlum
                                                !isAvailable -> LumiBorderDivider.copy(alpha = 0.4f)
                                                else -> LumiBorderDivider
                                            }
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .clip(LumiChipShape)
                                            .clickable(enabled = isAvailable) {
                                                selectedTimeSlot = slot
                                            }
                                            .testTag("time_slot_${slot.formatted.replace(" ", "_")}")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = when {
                                                    isSelected -> LumiSurfaceWhite
                                                    !isAvailable -> LumiMutedGray.copy(alpha = 0.5f)
                                                    else -> LumiPlum
                                                },
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = slot.formatted,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                ),
                                                color = when {
                                                    isSelected -> LumiSurfaceWhite
                                                    !isAvailable -> LumiMutedGray.copy(alpha = 0.5f)
                                                    else -> LumiCharcoal
                                                }
                                            )
                                            if (slot.isBooked) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "(Booked)",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = LumiMutedGray.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowSlots.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Demo Notice
                item {
                    LumiDemoNotice(
                        text = "Demonstration availability: duplicate bookings on the same slot are strictly prevented"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Continue CTA
            Surface(
                color = LumiSurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected slot",
                            style = MaterialTheme.typography.labelSmall,
                            color = LumiMutedGray
                        )
                        Text(
                            text = if (selectedTimeSlot != null) {
                                "${DateTimeUtils.formatShortDate(selectedTimeSlot!!.epochMillis)} • ${selectedTimeSlot!!.formatted}"
                            } else {
                                "Pick a time"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTimeSlot != null) LumiPlum else LumiMutedGray
                        )
                    }

                    LumiButton(
                        text = "Continue",
                        enabled = selectedTimeSlot != null,
                        onClick = {
                            if (selectedTimeSlot != null) {
                                onProceedToSummary(serviceId, providerId, selectedTimeSlot!!.epochMillis)
                            }
                        },
                        modifier = Modifier.width(160.dp),
                        testTag = "scheduling_continue_cta"
                    )
                }
            }
        }
    }
}
