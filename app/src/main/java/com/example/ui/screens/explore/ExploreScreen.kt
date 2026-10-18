package com.example.ui.screens.explore

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.LumiEmptyState
import com.example.core.designsystem.LumiFilterChip
import com.example.data.repository.LumiRepository
import com.example.ui.screens.home.ServiceCard
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiInputFieldShape
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    RECOMMENDED("Recommended"),
    HIGHEST_RATED("Highest rated"),
    PRICE_LOW_HIGH("Price: low to high"),
    PRICE_HIGH_LOW("Price: high to low")
}

@Composable
fun ExploreScreen(
    repository: LumiRepository,
    initialCategory: String? = null,
    onNavigateToServiceDetail: (String) -> Unit
) {
    val services by repository.allServices.collectAsState(initial = emptyList())
    val providers by repository.allProviders.collectAsState(initial = emptyList())
    val favoriteIds by repository.favoriteIds.collectAsState(initial = emptySet())

    val providerMap = remember(providers) { providers.associateBy { it.id } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedSort by remember { mutableStateOf(SortOption.RECOMMENDED) }
    var isSortMenuOpen by remember { mutableStateOf(false) }

    val categories = remember {
        listOf("All", "Hair", "Nails", "Skin", "Massage", "Brows", "Wellness")
    }

    val filteredAndSortedServices = remember(services, searchQuery, selectedCategory, selectedSort, providerMap) {
        val filtered = services.filter { service ->
            val matchesCat = selectedCategory == null || selectedCategory == "All" ||
                service.category.equals(selectedCategory, ignoreCase = true)

            val query = searchQuery.trim()
            val matchesQuery = query.isEmpty() ||
                service.name.contains(query, ignoreCase = true) ||
                service.description.contains(query, ignoreCase = true) ||
                service.category.contains(query, ignoreCase = true) ||
                (providerMap[service.providerId]?.name?.contains(query, ignoreCase = true) == true)

            matchesCat && matchesQuery
        }

        when (selectedSort) {
            SortOption.RECOMMENDED -> filtered.sortedByDescending { it.isFeatured }
            SortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating }
            SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.priceCents }
            SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.priceCents }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = LumiCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Find the right service for you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LumiMutedGray
                )
            }
        }

        // Search Field
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search by service, provider, or ritual...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LumiMutedGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = LumiMutedGray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = LumiMutedGray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = LumiInputFieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LumiSurfaceWhite,
                        unfocusedContainerColor = LumiWarmGray,
                        focusedBorderColor = LumiPlum,
                        unfocusedBorderColor = LumiBorderDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input")
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = (category == "All" && selectedCategory == null) ||
                        (selectedCategory.equals(category, ignoreCase = true))

                    LumiFilterChip(
                        label = category,
                        isSelected = isSelected,
                        onClick = {
                            selectedCategory = if (category == "All") null else category
                        },
                        testTag = "explore_chip_$category"
                    )
                }
            }
        }

        // Sort Row and Results Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredAndSortedServices.size} services available",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = LumiMutedGray
                )

                Box {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = LumiWarmGray,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier
                            .clickable { isSortMenuOpen = true }
                            .testTag("sort_menu_trigger")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                tint = LumiPlum,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedSort.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = LumiCharcoal
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isSortMenuOpen,
                        onDismissRequest = { isSortMenuOpen = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedSort == option) LumiPlum else LumiCharcoal,
                                        fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedSort = option
                                    isSortMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Service Results or Empty State
        if (filteredAndSortedServices.isEmpty()) {
            item {
                LumiEmptyState(
                    title = "No services found",
                    description = "We couldn't find any services matching your search criteria. Try adjusting your filters.",
                    actionText = "Clear all filters",
                    onActionClick = {
                        searchQuery = ""
                        selectedCategory = null
                        selectedSort = SortOption.RECOMMENDED
                    },
                    modifier = Modifier.padding(top = 40.dp),
                    testTag = "explore_empty_state"
                )
            }
        } else {
            items(filteredAndSortedServices) { service ->
                val provider = providerMap[service.providerId]
                val isFavorite = favoriteIds.contains(service.id)

                ServiceCard(
                    service = service,
                    providerName = provider?.name ?: "LUMI Specialist",
                    isFavorite = isFavorite,
                    onToggleFavorite = {
                        CoroutineScope(Dispatchers.Main).launch {
                            repository.toggleFavorite(service.id)
                        }
                    },
                    onClick = { onNavigateToServiceDetail(service.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}
