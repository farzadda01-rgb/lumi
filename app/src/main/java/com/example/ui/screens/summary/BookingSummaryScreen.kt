package com.example.ui.screens.summary

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiAvatar
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiDrawableImage
import com.example.core.designsystem.LumiHeader
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiButtonShape
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiInputFieldShape
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSuccess
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

@Composable
fun BookingSummaryScreen(
    serviceId: String,
    providerId: String,
    timestamp: Long,
    repository: LumiRepository,
    onBack: () -> Unit,
    onBookingConfirmed: (referenceCode: String) -> Unit
) {
    val service by repository.getService(serviceId).collectAsState(initial = null)
    val provider by repository.getProvider(providerId).collectAsState(initial = null)

    var customerNote by remember { mutableStateOf("") }
    var isBookingInProgress by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val currentService = service
    val currentProvider = provider

    if (currentService == null || currentProvider == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LumiWarmIvory),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LumiPlum)
        }
        return
    }

    val maxNoteLength = 500

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("booking_summary_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Review & Confirm",
                subtitle = "Review your ritual details before booking",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Service Card Review
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(LumiCardShape)
                            ) {
                                LumiDrawableImage(
                                    imageName = currentService.imageDrawableName,
                                    contentDescription = currentService.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentService.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LumiCharcoal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${currentService.category} • ${currentService.durationMinutes} Minutes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LumiMutedGray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = CurrencyFormatter.formatCents(currentService.priceCents, currentService.currency),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LumiPlum
                                    )
                                )
                            }
                        }
                    }
                }

                // Appointment Details
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Appointment Details",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Date & Time
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = LumiLavenderSubtle,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = LumiPlum,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = DateTimeUtils.formatFullDate(timestamp),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = LumiCharcoal
                                    )
                                    Text(
                                        text = "${DateTimeUtils.formatTime(timestamp)} (${currentService.durationMinutes} min)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LumiMutedGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = LumiBorderDivider)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Specialist
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LumiAvatar(name = currentProvider.name, size = 36.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentProvider.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = LumiCharcoal
                                    )
                                    Text(
                                        text = currentProvider.specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LumiMutedGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = LumiBorderDivider)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Location
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = LumiLavenderSubtle,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = LumiPlum,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "LUMI Sanctuary",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = LumiCharcoal
                                    )
                                    Text(
                                        text = currentService.location,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LumiMutedGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Pricing Summary
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Price Summary",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentService.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LumiMutedGray
                                )
                                Text(
                                    text = CurrencyFormatter.formatCents(currentService.priceCents, currentService.currency),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LumiCharcoal
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Demo Booking Fee",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LumiMutedGray
                                )
                                Text(
                                    text = "$0.00",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = LumiSuccess),
                                    color = LumiSuccess
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = LumiBorderDivider)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total (Pay at Sanctuary)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = LumiCharcoal
                                )
                                Text(
                                    text = CurrencyFormatter.formatCents(currentService.priceCents, currentService.currency),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LumiPlum
                                    )
                                )
                            }
                        }
                    }
                }

                // Customer Note Field
                item {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Special requests (Optional)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LumiCharcoal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Anything your specialist should know ahead of your ritual?",
                            style = MaterialTheme.typography.bodySmall,
                            color = LumiMutedGray
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customerNote,
                            onValueChange = {
                                if (it.length <= maxNoteLength) customerNote = it
                            },
                            placeholder = {
                                Text(
                                    text = "e.g., Sensitive scalp, prefer quiet session, etc.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LumiMutedGray
                                )
                            },
                            shape = LumiInputFieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LumiSurfaceWhite,
                                unfocusedContainerColor = LumiSurfaceWhite,
                                focusedBorderColor = LumiPlum,
                                unfocusedBorderColor = LumiBorderDivider
                            ),
                            minLines = 3,
                            maxLines = 5,
                            supportingText = {
                                Text(
                                    text = "${customerNote.length}/$maxNoteLength",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LumiMutedGray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("booking_note_input")
                        )
                    }
                }

                // Cancellation policy & demo notice
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiWarmGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = LumiSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentService.cancellationPolicy,
                                style = MaterialTheme.typography.bodySmall,
                                color = LumiCharcoal.copy(alpha = 0.85f)
                            )
                        }
                    }

                    LumiDemoNotice(
                        text = "No real financial charge will occur. Stored locally as a demo booking."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Confirm CTA
            Surface(
                color = LumiSurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    LumiButton(
                        text = if (isBookingInProgress) "Confirming..." else "Confirm booking",
                        enabled = !isBookingInProgress,
                        onClick = {
                            isBookingInProgress = true
                            errorMessage = null
                            scope.launch {
                                val result = repository.bookAppointment(
                                    serviceId = currentService.id,
                                    providerId = currentProvider.id,
                                    startTimestamp = timestamp,
                                    customerNote = customerNote
                                )
                                isBookingInProgress = false
                                result.onSuccess { appointment ->
                                    onBookingConfirmed(appointment.referenceCode)
                                }.onFailure { error ->
                                    errorMessage = error.localizedMessage ?: "Unable to confirm booking. Please try another time."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "confirm_booking_button"
                    )
                }
            }
        }

        // Error Dialog
        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = {
                    Text(
                        text = "Booking Notice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(text = errorMessage!!)
                },
                confirmButton = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text("OK", color = LumiPlum)
                    }
                }
            )
        }
    }
}
