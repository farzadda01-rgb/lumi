package com.example.ui.screens.notifications

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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.DateTimeUtils
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiEmptyState
import com.example.core.designsystem.LumiHeader
import com.example.data.model.NotificationEntity
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiCoral
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    repository: LumiRepository,
    onBack: () -> Unit
) {
    val notifications by repository.notifications.collectAsState(initial = emptyList())
    val unreadCount by repository.unreadNotificationsCount.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("notifications_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Notifications",
                subtitle = if (unreadCount > 0) "$unreadCount unread notices" else "All notices read",
                onBack = onBack,
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    repository.markAllNotificationsAsRead()
                                }
                            },
                            modifier = Modifier.testTag("mark_all_read_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = LumiPlum,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mark all read",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = LumiPlum
                            )
                        }
                    }
                }
            )

            if (notifications.isEmpty()) {
                LumiEmptyState(
                    title = "No notifications",
                    description = "Your booking updates, reminders, and wellness notes will be stored here.",
                    icon = Icons.Default.NotificationsNone,
                    modifier = Modifier.padding(top = 40.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                if (!item.isRead) {
                                    scope.launch {
                                        repository.markNotificationAsRead(item.id)
                                    }
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        LumiDemoNotice(
                            text = "In-app demonstration notification center • Offline only"
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationEntity,
    onClick: () -> Unit
) {
    Card(
        shape = LumiCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) LumiSurfaceWhite else LumiLavenderSubtle.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, if (item.isRead) LumiBorderDivider else LumiPlum.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(LumiCardShape)
            .clickable(onClick = onClick)
            .testTag("notification_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = if (item.isRead) LumiSurfaceWhite else LumiPlum,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = if (item.isRead) LumiMutedGray else LumiSurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (item.isRead) FontWeight.Medium else FontWeight.Bold
                        ),
                        color = LumiCharcoal
                    )

                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LumiCoral)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isRead) LumiMutedGray else LumiCharcoal.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = DateTimeUtils.formatShortDate(item.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = LumiMutedGray
                )
            }
        }
    }
}
