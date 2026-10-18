package com.example.ui.screens.servicedetails

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.CurrencyFormatter
import com.example.core.designsystem.LumiAvatar
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiDrawableImage
import com.example.core.designsystem.LumiFavoriteButton
import com.example.core.designsystem.LumiRatingBadge
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiButtonShape
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiCoral
import com.example.ui.theme.LumiGold
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSuccess
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    repository: LumiRepository,
    onBack: () -> Unit,
    onBookAppointment: (serviceId: String, providerId: String) -> Unit
) {
    val service by repository.getService(serviceId).collectAsState(initial = null)
    val favoriteIds by repository.favoriteIds.collectAsState(initial = emptySet())
    val provider by repository.getProvider(service?.providerId ?: "").collectAsState(initial = null)

    val currentService = service
    if (currentService == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LumiWarmIvory),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading service details...", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val isFavorite = favoriteIds.contains(currentService.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("service_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // Hero Image with Overlays
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    LumiDrawableImage(
                        imageName = currentService.imageDrawableName,
                        contentDescription = currentService.name,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Top navigation row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LumiSurfaceWhite.copy(alpha = 0.92f),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onBack)
                                .testTag("service_detail_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = LumiCharcoal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        LumiFavoriteButton(
                            isFavorite = isFavorite,
                            onToggle = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    repository.toggleFavorite(currentService.id)
                                }
                            },
                            size = 42.dp,
                            testTag = "service_detail_fav_button"
                        )
                    }

                    // Category tag
                    Surface(
                        shape = LumiPillShape,
                        color = LumiCharcoal.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = currentService.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LumiSurfaceWhite,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Main Info Block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentService.name,
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Experience crafted by ${provider?.name ?: "LUMI Specialist"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LumiMutedGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Duration and Price banner
                    Surface(
                        shape = LumiCardShape,
                        color = LumiWarmGray,
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = LumiPlum,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${currentService.durationMinutes} Minutes",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = LumiCharcoal
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LumiRatingBadge(
                                    rating = currentService.rating,
                                    reviewCount = currentService.reviewCount
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = CurrencyFormatter.formatCents(currentService.priceCents, currentService.currency),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LumiPlum
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    Text(
                        text = "About this ritual",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentService.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LumiCharcoal.copy(alpha = 0.85f),
                        lineHeight = 24.sp
                    )
                }
            }

            // What's Included
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "What's included",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            currentService.getInclusionsList().forEach { inclusion ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LumiLavenderSubtle,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = LumiPlum,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = inclusion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LumiCharcoal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Provider Summary Card
            item {
                if (provider != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Your specialist",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LumiCharcoal
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            shape = LumiCardShape,
                            colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                            border = BorderStroke(1.dp, LumiBorderDivider),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumiAvatar(
                                    name = provider!!.name,
                                    size = 56.dp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = provider!!.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LumiCharcoal
                                    )
                                    Text(
                                        text = provider!!.specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LumiMutedGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${provider!!.experienceYears} years experience • ★ ${provider!!.rating} (${provider!!.reviewCount})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                        color = LumiPlum
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location & Policy
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiWarmGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = LumiPlum,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentService.location,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = LumiCharcoal
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = LumiSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentService.cancellationPolicy,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LumiMutedGray
                                )
                            }
                        }
                    }
                }
            }

            // Demonstration Disclaimer
            item {
                Spacer(modifier = Modifier.height(8.dp))
                LumiDemoNotice(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Sample demonstration ratings & reviews • Offline local booking"
                )
            }
        }

        // Sticky Bottom Booking Action Area
        Surface(
            color = LumiSurfaceWhite,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, LumiBorderDivider),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
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
                        text = "Total Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = LumiMutedGray
                    )
                    Text(
                        text = CurrencyFormatter.formatCents(currentService.priceCents, currentService.currency),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = LumiPlum
                    )
                }

                LumiButton(
                    text = "Book appointment",
                    onClick = {
                        onBookAppointment(currentService.id, currentService.providerId)
                    },
                    modifier = Modifier.width(200.dp),
                    testTag = "service_detail_book_cta"
                )
            }
        }
    }
}
