package com.example.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.common.DataStoreManager
import com.example.core.designsystem.LumiAvatar
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiOutlinedButton
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: LumiRepository,
    dataStoreManager: DataStoreManager,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val isDarkModePref by dataStoreManager.isDarkMode.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogBody by remember { mutableStateOf<String?>(null) }

    val fullName = "${userProfile?.firstName ?: "Alex"} ${userProfile?.lastName ?: "Morgan"}"
    val email = userProfile?.email ?: "alex@example.com"
    val phone = userProfile?.phoneNumber ?: "+1 (555) 234-8901"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Profile Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LumiAvatar(name = fullName, size = 80.dp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = LumiCharcoal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LumiMutedGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LumiOutlinedButton(
                        text = "Edit Profile",
                        onClick = onNavigateToEditProfile,
                        testTag = "profile_edit_btn"
                    )
                }
            }

            // Quick Stats / Demo Notice
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    LumiDemoNotice(text = "Local Profile • Demonstration Identity")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Section: Activity & Rituals
            item {
                ProfileSectionGroup(title = "Rituals & Activity") {
                    ProfileMenuItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "My Bookings",
                        subtitle = "Track and manage appointments",
                        onClick = onNavigateToBookings,
                        testTag = "menu_my_bookings"
                    )
                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Bookmark,
                        title = "Saved Favorites",
                        subtitle = "Your preferred services",
                        onClick = onNavigateToFavorites,
                        testTag = "menu_favorites"
                    )
                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Reminders and booking alerts",
                        onClick = onNavigateToNotifications,
                        testTag = "menu_notifications"
                    )
                }
            }

            // Section: Preferences
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSectionGroup(title = "Preferences") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = LumiLavenderSubtle,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = LumiPlum,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Dark Appearance",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = LumiCharcoal
                                )
                                Text(
                                    text = if (isDarkModePref == true) "Enabled" else "Standard warm ivory",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LumiMutedGray
                                )
                            }
                        }

                        Switch(
                            checked = isDarkModePref == true,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    dataStoreManager.setDarkMode(checked)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LumiSurfaceWhite,
                                checkedTrackColor = LumiPlum,
                                uncheckedThumbColor = LumiSurfaceWhite,
                                uncheckedTrackColor = LumiWarmGray
                            ),
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))

                    ProfileMenuItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "English (US)",
                        onClick = {
                            activeDialogTitle = "Language"
                            activeDialogBody = "English (US) is the default language for this release. Multi-language localization architecture is supported."
                        },
                        testTag = "menu_language"
                    )
                }
            }

            // Section: Support & Legal
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSectionGroup(title = "Support & Legal") {
                    ProfileMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help & Support",
                        subtitle = "Sanctuary hours & FAQs",
                        onClick = {
                            activeDialogTitle = "Help & Support"
                            activeDialogBody = "LUMI Sanctuary Concierge\n\nHours: Mon–Sat 9:00 AM – 7:00 PM\nContact: concierge@lumi-sanctuary.example\nPhone: +1 (800) 555-LUMI\n\nFor questions about bookings, adjustments, or accessibility accommodations, our team is at your disposal."
                        },
                        testTag = "menu_help"
                    )
                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Security,
                        title = "Privacy Policy",
                        subtitle = "How your data is protected",
                        onClick = {
                            activeDialogTitle = "Privacy Policy"
                            activeDialogBody = "LUMI Privacy Promise\n\nIn this demonstration release, all your preferences, profile details, and ritual reservations are stored strictly in local device memory (Room & DataStore). No personal information is transmitted to external advertising networks or servers."
                        },
                        testTag = "menu_privacy"
                    )
                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Policy,
                        title = "Terms of Service",
                        subtitle = "Demonstration booking conditions",
                        onClick = {
                            activeDialogTitle = "Terms of Service"
                            activeDialogBody = "LUMI Service Terms\n\n1. This application serves as an interactive demonstration of a luxury wellness booking experience.\n2. Appointments created in this demo do not imply a confirmed external transaction.\n3. Cancellations in demo mode affect local records only."
                        },
                        testTag = "menu_terms"
                    )
                    HorizontalDivider(color = LumiBorderDivider, modifier = Modifier.padding(start = 56.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "About LUMI",
                        subtitle = "Your time. Your ritual. v1.0.0",
                        onClick = onNavigateToAbout,
                        testTag = "menu_about"
                    )
                }
            }
        }

        // Info Dialog for Support / Legal
        if (activeDialogTitle != null && activeDialogBody != null) {
            AlertDialog(
                onDismissRequest = {
                    activeDialogTitle = null
                    activeDialogBody = null
                },
                title = {
                    Text(
                        text = activeDialogTitle!!,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = activeDialogBody!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        activeDialogTitle = null
                        activeDialogBody = null
                    }) {
                        Text("Close", color = LumiPlum)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileSectionGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = LumiCharcoal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = LumiCardShape,
            colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
            border = BorderStroke(1.dp, LumiBorderDivider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String = "profile_menu_item"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                shape = CircleShape,
                color = LumiLavenderSubtle,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LumiPlum,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = LumiCharcoal
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LumiMutedGray
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = LumiMutedGray,
            modifier = Modifier.size(14.dp)
        )
    }
}
