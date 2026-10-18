package com.example.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.DataStoreManager
import com.example.data.repository.LumiRepository
import com.example.ui.navigation.BottomBarDestination
import com.example.ui.screens.bookings.BookingsScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiCoral
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmIvory

data class NavItem(
    val destination: BottomBarDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainScaffold(
    initialTab: String = "home",
    repository: LumiRepository,
    dataStoreManager: DataStoreManager,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    var currentTab by rememberSaveable {
        mutableStateOf(
            BottomBarDestination.values().find { it.route == initialTab } ?: BottomBarDestination.HOME
        )
    }

    var exploreInitialCategory by remember { mutableStateOf<String?>(null) }

    // If not on Home, back button returns to Home
    BackHandler(enabled = currentTab != BottomBarDestination.HOME) {
        currentTab = BottomBarDestination.HOME
    }

    val upcomingCount by repository.upcomingAppointments.collectAsState(initial = emptyList())
    val unreadCount by repository.unreadNotificationsCount.collectAsState(initial = 0)

    val navItems = remember {
        listOf(
            NavItem(
                destination = BottomBarDestination.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_item_home"
            ),
            NavItem(
                destination = BottomBarDestination.EXPLORE,
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                testTag = "nav_item_explore"
            ),
            NavItem(
                destination = BottomBarDestination.BOOKINGS,
                selectedIcon = Icons.Filled.CalendarMonth,
                unselectedIcon = Icons.Outlined.CalendarMonth,
                testTag = "nav_item_bookings"
            ),
            NavItem(
                destination = BottomBarDestination.PROFILE,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                testTag = "nav_item_profile"
            )
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = LumiSurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lumi_bottom_navigation")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentTab == item.destination
                        val badgeCount = when (item.destination) {
                            BottomBarDestination.BOOKINGS -> upcomingCount.size
                            BottomBarDestination.PROFILE -> unreadCount
                            else -> 0
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(LumiPillShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    currentTab = item.destination
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag(item.testTag)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0 && item.destination == BottomBarDestination.BOOKINGS) {
                                        Badge(
                                            containerColor = LumiPlum,
                                            contentColor = LumiSurfaceWhite
                                        ) {
                                            Text("$badgeCount")
                                        }
                                    } else if (badgeCount > 0 && item.destination == BottomBarDestination.PROFILE) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(LumiCoral)
                                        )
                                    }
                                }
                            ) {
                                Surface(
                                    shape = LumiPillShape,
                                    color = if (isSelected) LumiLavenderSubtle else LumiSurfaceWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.destination.title,
                                        tint = if (isSelected) LumiPlum else LumiMutedGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = item.destination.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) LumiPlum else LumiMutedGray
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LumiWarmIvory)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "main_screen_tab_crossfade"
            ) { tab ->
                when (tab) {
                    BottomBarDestination.HOME -> {
                        HomeScreen(
                            repository = repository,
                            onNavigateToServiceDetail = onNavigateToServiceDetail,
                            onNavigateToExplore = { category ->
                                exploreInitialCategory = category
                                currentTab = BottomBarDestination.EXPLORE
                            },
                            onNavigateToBookings = {
                                currentTab = BottomBarDestination.BOOKINGS
                            },
                            onNavigateToNotifications = onNavigateToNotifications,
                            onNavigateToProfile = {
                                currentTab = BottomBarDestination.PROFILE
                            }
                        )
                    }

                    BottomBarDestination.EXPLORE -> {
                        ExploreScreen(
                            repository = repository,
                            initialCategory = exploreInitialCategory,
                            onNavigateToServiceDetail = onNavigateToServiceDetail
                        )
                    }

                    BottomBarDestination.BOOKINGS -> {
                        BookingsScreen(
                            repository = repository,
                            onNavigateToExplore = {
                                currentTab = BottomBarDestination.EXPLORE
                            }
                        )
                    }

                    BottomBarDestination.PROFILE -> {
                        ProfileScreen(
                            repository = repository,
                            dataStoreManager = dataStoreManager,
                            onNavigateToEditProfile = onNavigateToEditProfile,
                            onNavigateToBookings = {
                                currentTab = BottomBarDestination.BOOKINGS
                            },
                            onNavigateToFavorites = onNavigateToFavorites,
                            onNavigateToNotifications = onNavigateToNotifications,
                            onNavigateToAbout = onNavigateToAbout
                        )
                    }
                }
            }
        }
    }
}
