package com.example.ui.screens.confirmation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiOutlinedButton
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSuccess
import com.example.ui.theme.LumiSuccessContainer
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmIvory

@Composable
fun BookingConfirmationScreen(
    referenceCode: String,
    repository: LumiRepository,
    onViewBookings: () -> Unit,
    onBackToHome: () -> Unit
) {
    val appointments by repository.upcomingAppointments.collectAsState(initial = emptyList())
    val services by repository.allServices.collectAsState(initial = emptyList())
    val providers by repository.allProviders.collectAsState(initial = emptyList())

    val appointment = remember(appointments, referenceCode) {
        appointments.find { it.referenceCode == referenceCode }
    }
    val service = remember(services, appointment) {
        services.find { it.id == appointment?.serviceId }
    }
    val provider = remember(providers, appointment) {
        providers.find { it.id == appointment?.providerId }
    }

    val checkScale = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        checkScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("booking_confirmation_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Success Icon
        Surface(
            shape = CircleShape,
            color = LumiSuccessContainer,
            border = BorderStroke(2.dp, LumiSuccess),
            modifier = Modifier
                .size(84.dp)
                .scale(checkScale.value)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = LumiSuccess,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = LumiCharcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your appointment details are ready and saved locally.",
            style = MaterialTheme.typography.bodyLarge,
            color = LumiMutedGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Confirmation Card
        Card(
            shape = LumiCardShape,
            colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
            border = BorderStroke(1.dp, LumiBorderDivider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Reference Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Confirmation Reference",
                            style = MaterialTheme.typography.labelSmall,
                            color = LumiMutedGray
                        )
                        Text(
                            text = referenceCode,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = LumiPlum
                        )
                    }

                    Surface(
                        shape = LumiPillShape,
                        color = LumiSuccessContainer
                    ) {
                        Text(
                            text = "Confirmed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LumiSuccess
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LumiBorderDivider)
                Spacer(modifier = Modifier.height(16.dp))

                // Service Name & Provider
                Text(
                    text = service?.name ?: "Ritual Appointment",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LumiCharcoal
                )
                Text(
                    text = "With ${provider?.name ?: "Specialist"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LumiMutedGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Date & Time
                if (appointment != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = LumiPlum,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${DateTimeUtils.formatShortDate(appointment.startTimestamp)} at ${DateTimeUtils.formatTime(appointment.startTimestamp)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = LumiCharcoal
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount to pay at visit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LumiMutedGray
                        )
                        Text(
                            text = CurrencyFormatter.formatCents(appointment.priceCents, appointment.currency),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LumiPlum
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demo disclaimer
        LumiDemoNotice(
            text = "Offline demonstration: this appointment is stored locally on this device"
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        LumiButton(
            text = "View my bookings",
            onClick = onViewBookings,
            modifier = Modifier.fillMaxWidth(),
            testTag = "confirmation_view_bookings_button"
        )

        Spacer(modifier = Modifier.height(12.dp))

        LumiOutlinedButton(
            text = "Back to home",
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth(),
            testTag = "confirmation_back_home_button"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
