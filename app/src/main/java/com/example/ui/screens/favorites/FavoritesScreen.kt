package com.example.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.LumiEmptyState
import com.example.core.designsystem.LumiHeader
import com.example.data.repository.LumiRepository
import com.example.ui.screens.home.ServiceCard
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    repository: LumiRepository,
    onBack: () -> Unit,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val services by repository.allServices.collectAsState(initial = emptyList())
    val providers by repository.allProviders.collectAsState(initial = emptyList())
    val favoriteIds by repository.favoriteIds.collectAsState(initial = emptySet())

    val providerMap = remember(providers) { providers.associateBy { it.id } }
    val favoriteServices = remember(services, favoriteIds) {
        services.filter { favoriteIds.contains(it.id) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("favorites_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Saved Favorites",
                subtitle = "${favoriteServices.size} rituals saved",
                onBack = onBack
            )

            if (favoriteServices.isEmpty()) {
                LumiEmptyState(
                    title = "No favorites yet",
                    description = "When you discover services that resonate with you, tap the heart icon to save them for easy access.",
                    icon = Icons.Default.BookmarkBorder,
                    actionText = "Explore rituals",
                    onActionClick = onNavigateToExplore,
                    modifier = Modifier.padding(top = 40.dp),
                    testTag = "favorites_empty_state"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(favoriteServices, key = { it.id }) { service ->
                        val provider = providerMap[service.providerId]

                        ServiceCard(
                            service = service,
                            providerName = provider?.name ?: "LUMI Specialist",
                            isFavorite = true,
                            onToggleFavorite = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    repository.toggleFavorite(service.id)
                                }
                            },
                            onClick = { onNavigateToServiceDetail(service.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
