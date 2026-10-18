package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.common.CurrencyFormatter
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiAvatar
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiDrawableImage
import com.example.core.designsystem.LumiFavoriteButton
import com.example.core.designsystem.LumiFilterChip
import com.example.core.designsystem.LumiRatingBadge
import com.example.data.model.AppointmentEntity
import com.example.data.model.ServiceEntity
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiCoral
import com.example.ui.theme.LumiHeroCardShape
import com.example.ui.theme.LumiInputFieldShape
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiPlumDark
import com.example.ui.theme.LumiSuccess
import com.example.ui.theme.LumiSuccessContainer
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

data class CategoryItem(val name: String, val icon: ImageVector)

@Composable
fun HomeScreen(
    repository: LumiRepository,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToExplore: (category: String?) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val services by repository.allServices.collectAsState(initial = emptyList())
    val upcomingAppointments by repository.upcomingAppointments.collectAsState(initial = emptyList())
    val favoriteIds by repository.favoriteIds.collectAsState(initial = emptySet())
    val unreadCount by repository.unreadNotificationsCount.collectAsState(initial = 0)
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val providers by repository.allProviders.collectAsState(initial = emptyList())

    val providerMap = remember(providers) { providers.associateBy { it.id } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        listOf(
            CategoryItem("Hair", Icons.Outlined.ContentCut),
            CategoryItem("Nails", Icons.Outlined.Fingerprint),
            CategoryItem("Skin", Icons.Outlined.FaceRetouchingNatural),
            CategoryItem("Massage", Icons.Outlined.SelfImprovement),
            CategoryItem("Brows", Icons.Default.Face),
            CategoryItem("Wellness", Icons.Default.Spa)
        )
    }

    val timeGreeting = remember {
        val currentHour = LocalTime.now().hour
        when {
            currentHour < 12 -> "GOOD MORNING"
            currentHour < 17 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }
    }

    val filteredServices = remember(services, searchQuery, selectedCategory) {
        services.filter { service ->
            val matchesCategory = selectedCategory == null || service.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                service.name.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true) ||
                (providerMap[service.providerId]?.name?.contains(searchQuery, ignoreCase = true) == true)
            matchesCategory && matchesQuery
        }
    }

    val nextUpcomingAppointment = upcomingAppointments.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Bar Greeting
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = timeGreeting,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = LumiLavender
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hello, ${userProfile?.firstName ?: "Alex"}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("home_notification_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = LumiCoral,
                                        contentColor = LumiCharcoal
                                    ) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = LumiCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clickable { onNavigateToProfile() }
                            .testTag("home_profile_avatar")
                    ) {
                        LumiAvatar(
                            name = "${userProfile?.firstName ?: "Alex"} ${userProfile?.lastName ?: "Morgan"}",
                            size = 40.dp
                        )
                    }
                }
            }
        }

        // Hero Feature Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(LumiHeroCardShape)
                    .background(LumiPlumDark)
                    .testTag("home_hero_card")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lumi_hero_banner),
                    contentDescription = "LUMI Wellness Sanctuary",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                // Dark gradient overlay for editorial elegance & readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    LumiPlumDark.copy(alpha = 0.5f),
                                    LumiPlumDark.copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Your next little moment of care.",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = LumiSurfaceWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find something that feels good.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LumiLavenderSubtle
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LumiButton(
                        text = "Explore services",
                        onClick = { onNavigateToExplore(null) },
                        testTag = "home_explore_services_cta"
                    )
                }
            }
        }

        // Search Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search services or providers",
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
                        .testTag("home_search_input")
                )
            }
        }

        // Category Section
        item {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    if (selectedCategory != null) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelMedium,
                            color = LumiPlum,
                            modifier = Modifier
                                .clickable { selectedCategory = null }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category.name
                        LumiFilterChip(
                            label = category.name,
                            icon = category.icon,
                            isSelected = isSelected,
                            onClick = {
                                selectedCategory = if (isSelected) null else category.name
                            },
                            testTag = "category_chip_${category.name}"
                        )
                    }
                }
            }
        }

        // Upcoming Appointment Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Appointment",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    if (nextUpcomingAppointment != null) {
                        Text(
                            text = "View all",
                            style = MaterialTheme.typography.labelMedium,
                            color = LumiPlum,
                            modifier = Modifier
                                .clickable { onNavigateToBookings() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (nextUpcomingAppointment != null) {
                    val appointmentService = services.find { it.id == nextUpcomingAppointment.serviceId }
                    val appointmentProvider = providerMap[nextUpcomingAppointment.providerId]

                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToBookings() }
                            .testTag("upcoming_appointment_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = LumiCardShape,
                                color = LumiLavenderSubtle,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = LumiPlum,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = appointmentService?.name ?: "Appointment",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LumiCharcoal
                                    )
                                    Surface(
                                        shape = LumiPillShape,
                                        color = LumiSuccessContainer
                                    ) {
                                        Text(
                                            text = nextUpcomingAppointment.status,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LumiSuccess,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "With ${appointmentProvider?.name ?: "Provider"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LumiMutedGray
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${DateTimeUtils.formatShortDate(nextUpcomingAppointment.startTimestamp)} • ${DateTimeUtils.formatTime(nextUpcomingAppointment.startTimestamp)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = LumiPlum
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiWarmGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "No upcoming rituals",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = LumiCharcoal
                                )
                                Text(
                                    text = "Reserve your next calm moment of care.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LumiMutedGray
                                )
                            }
                            LumiButton(
                                text = "Book now",
                                onClick = { onNavigateToExplore(null) },
                                testTag = "home_book_empty_cta"
                            )
                        }
                    }
                }
            }
        }

        // Popular Services Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Services",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    Text(
                        text = "See all",
                        style = MaterialTheme.typography.labelMedium,
                        color = LumiPlum,
                        modifier = Modifier
                            .clickable { onNavigateToExplore(null) }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Service items
        items(filteredServices) { service ->
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

        // Demo Notice Footer
        item {
            Spacer(modifier = Modifier.height(16.dp))
            LumiDemoNotice(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Demonstration Mode • Appointments stored locally on device"
            )
        }
    }
}

@Composable
fun ServiceCard(
    service: ServiceEntity,
    providerName: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = LumiCardShape,
        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
        border = BorderStroke(1.dp, LumiBorderDivider),
        modifier = modifier
            .clip(LumiCardShape)
            .clickable(onClick = onClick)
            .testTag("service_card_${service.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                LumiDrawableImage(
                    imageName = service.imageDrawableName,
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize()
                )

                // Category pill
                Surface(
                    shape = LumiPillShape,
                    color = LumiCharcoal.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = service.category,
                        style = MaterialTheme.typography.labelSmall.copy(color = LumiSurfaceWhite),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Favorite button
                LumiFavoriteButton(
                    isFavorite = isFavorite,
                    onToggle = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    testTag = "service_fav_btn_${service.id}"
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LumiCharcoal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "by $providerName",
                            style = MaterialTheme.typography.bodySmall,
                            color = LumiMutedGray
                        )
                    }

                    LumiRatingBadge(
                        rating = service.rating,
                        reviewCount = service.reviewCount
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${service.durationMinutes} minutes",
                        style = MaterialTheme.typography.labelMedium,
                        color = LumiMutedGray
                    )

                    Text(
                        text = CurrencyFormatter.formatCents(service.priceCents, service.currency),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LumiPlum
                        )
                    )
                }
            }
        }
    }
}
