package com.example.ui.screens.providerselection

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.LumiAvatar
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiHeader
import com.example.data.model.ProviderEntity
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiGold
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory

@Composable
fun ProviderSelectionScreen(
    serviceId: String,
    repository: LumiRepository,
    onBack: () -> Unit,
    onProviderSelected: (serviceId: String, providerId: String) -> Unit
) {
    val service by repository.getService(serviceId).collectAsState(initial = null)
    val providers by repository.allProviders.collectAsState(initial = emptyList())

    var selectedProviderId by remember(service) {
        mutableStateOf(service?.providerId ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("provider_selection_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Select Specialist",
                subtitle = service?.name ?: "Choose your preferred specialist",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Available specialists for this ritual:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LumiMutedGray
                    )
                }

                items(providers) { provider ->
                    val isSelected = provider.id == selectedProviderId

                    ProviderCard(
                        provider = provider,
                        isSelected = isSelected,
                        onSelect = { selectedProviderId = provider.id }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    LumiDemoNotice(text = "Specialist profiles & availability are simulated in demo mode")
                }
            }

            // Bottom Continue CTA
            Surface(
                color = LumiSurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    LumiButton(
                        text = "Continue to schedule",
                        enabled = selectedProviderId.isNotEmpty(),
                        onClick = {
                            if (selectedProviderId.isNotEmpty()) {
                                onProviderSelected(serviceId, selectedProviderId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "provider_selection_continue_cta"
                    )
                }
            }
        }
    }
}

@Composable
fun ProviderCard(
    provider: ProviderEntity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = LumiCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LumiLavenderSubtle else LumiSurfaceWhite
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) LumiPlum else LumiBorderDivider
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(LumiCardShape)
            .clickable(onClick = onSelect)
            .testTag("provider_card_${provider.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LumiAvatar(
                name = provider.name,
                size = 54.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = LumiGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${provider.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = LumiCharcoal
                        )
                    }
                }

                Text(
                    text = provider.specialty,
                    style = MaterialTheme.typography.bodySmall,
                    color = LumiMutedGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = provider.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = LumiCharcoal.copy(alpha = 0.8f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• Available this week",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = LumiPlum
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = LumiPlum,
                    unselectedColor = LumiMutedGray
                )
            )
        }
    }
}
