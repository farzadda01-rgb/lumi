package com.example.ui.screens.bookings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiEmptyState
import com.example.core.designsystem.LumiOutlinedButton
import com.example.data.model.AppointmentEntity
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiError
import com.example.ui.theme.LumiErrorContainer
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSuccess
import com.example.ui.theme.LumiSuccessContainer
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

@Composable
fun BookingsScreen(
    repository: LumiRepository,
    onNavigateToExplore: () -> Unit
) {
    val upcomingAppointments by repository.upcomingAppointments.collectAsState(initial = emptyList())
    val pastAppointments by repository.pastAppointments.collectAsState(initial = emptyList())
    val services by repository.allServices.collectAsState(initial = emptyList())
    val providers by repository.allProviders.collectAsState(initial = emptyList())

    val serviceMap = remember(services) { services.associateBy { it.id } }
    val providerMap = remember(providers) { providers.associateBy { it.id } }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past")

    var appointmentToCancel by remember { mutableStateOf<AppointmentEntity?>(null) }
    val scope = rememberCoroutineScope()

    val currentList = if (selectedTabIndex == 0) upcomingAppointments else pastAppointments

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("bookings_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "My Bookings",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = LumiCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage and track your wellness rituals.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LumiMutedGray
                )
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = LumiWarmIvory,
                contentColor = LumiPlum,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = LumiPlum,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(color = LumiBorderDivider) },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) LumiPlum else LumiMutedGray
                            )
                        },
                        modifier = Modifier.testTag("tab_$title")
                    )
                }
            }

            // Bookings List or Empty State
            if (currentList.isEmpty()) {
                LumiEmptyState(
                    title = if (selectedTabIndex == 0) "No upcoming bookings" else "No past appointments",
                    description = if (selectedTabIndex == 0) {
                        "You don't have any upcoming rituals scheduled. Find a restorative experience that feels right for you."
                    } else {
                        "Past completed or cancelled rituals will appear here."
                    },
                    icon = Icons.Default.EventBusy,
                    actionText = if (selectedTabIndex == 0) "Explore rituals" else null,
                    onActionClick = if (selectedTabIndex == 0) onNavigateToExplore else null,
                    modifier = Modifier.padding(top = 40.dp),
                    testTag = "bookings_empty_state"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(currentList, key = { it.id }) { appointment ->
                        val service = serviceMap[appointment.serviceId]
                        val provider = providerMap[appointment.providerId]

                        BookingCard(
                            appointment = appointment,
                            serviceName = service?.name ?: "Ritual Appointment",
                            providerName = provider?.name ?: "Specialist",
                            durationMinutes = service?.durationMinutes ?: 45,
                            onCancelClick = {
                                appointmentToCancel = appointment
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        LumiDemoNotice(
                            text = "Appointments are managed strictly inside local demonstration storage"
                        )
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Cancel Confirmation Dialog
        if (appointmentToCancel != null) {
            val appt = appointmentToCancel!!
            val apptService = serviceMap[appt.serviceId]?.name ?: "Ritual"

            AlertDialog(
                onDismissRequest = { appointmentToCancel = null },
                title = {
                    Text(
                        text = "Cancel Appointment?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Are you sure you want to cancel $apptService (${appt.referenceCode}) scheduled for ${DateTimeUtils.formatShortDate(appt.startTimestamp)}?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Note: This demonstration cancellation updates only your local device record.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LumiMutedGray
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.cancelAppointment(appt.id)
                                appointmentToCancel = null
                            }
                        },
                        modifier = Modifier.testTag("confirm_cancel_button")
                    ) {
                        Text("Yes, Cancel", color = LumiError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { appointmentToCancel = null }) {
                        Text("Keep Appointment", color = LumiCharcoal)
                    }
                }
            )
        }
    }
}

@Composable
fun BookingCard(
    appointment: AppointmentEntity,
    serviceName: String,
    providerName: String,
    durationMinutes: Int,
    onCancelClick: () -> Unit
) {
    Card(
        shape = LumiCardShape,
        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
        border = BorderStroke(1.dp, LumiBorderDivider),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${appointment.referenceCode}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Reference & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.referenceCode,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = LumiPlum
                )

                val statusColor = when (appointment.status) {
                    "Confirmed" -> LumiSuccess
                    "Completed" -> LumiPlum
                    else -> LumiError
                }
                val statusContainerColor = when (appointment.status) {
                    "Confirmed" -> LumiSuccessContainer
                    "Completed" -> LumiWarmGray
                    else -> LumiErrorContainer
                }

                Surface(
                    shape = LumiPillShape,
                    color = statusContainerColor
                ) {
                    Text(
                        text = appointment.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Service and Provider
            Text(
                text = serviceName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = LumiCharcoal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Specialist: $providerName",
                style = MaterialTheme.typography.bodySmall,
                color = LumiMutedGray
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LumiBorderDivider)
            Spacer(modifier = Modifier.height(12.dp))

            // Date, Time, Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = LumiPlum,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${DateTimeUtils.formatShortDate(appointment.startTimestamp)} • ${DateTimeUtils.formatTime(appointment.startTimestamp)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = LumiCharcoal
                    )
                }

                Text(
                    text = CurrencyFormatter.formatCents(appointment.priceCents, appointment.currency),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LumiPlum
                    )
                )
            }

            // Customer Note if any
            if (appointment.customerNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: \"${appointment.customerNote}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = LumiMutedGray,
                    maxLines = 2
                )
            }

            // Cancel action if Confirmed and in upcoming state
            if (appointment.status == "Confirmed") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LumiOutlinedButton(
                        text = "Cancel Booking",
                        onClick = onCancelClick,
                        modifier = Modifier.testTag("cancel_booking_btn_${appointment.referenceCode}")
                    )
                }
            }
        }
    }
}
