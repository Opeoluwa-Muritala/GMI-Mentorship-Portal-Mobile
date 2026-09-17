package org.globalmentorship.portal.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.NotificationCategory
import org.globalmentorship.portal.domain.models.NotificationItem
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.localization.formatArgs
import org.globalmentorship.portal.ui.theme.*

enum class NotificationFilter {
    ALL,
    UNREAD,
    READ
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    unreadCount: Int,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (String) -> Unit,
    onReplyToGmi: () -> Unit,
    onOpenExternalLink: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var currentFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    val filteredList = remember(notifications, currentFilter) {
        when (currentFilter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.READ -> notifications.filter { it.isRead }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GmiBackground)
    ) {
        // Top Header
        Surface(
            color = GmiSurface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.notifications,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Text(
                            text = strings.unreadNotificationsCount.formatArgs(unreadCount),
                            fontSize = 12.sp,
                            color = if (unreadCount > 0) GmiPrimaryBlue else GmiTextSecondary
                        )
                    }

                    if (unreadCount > 0) {
                        TextButton(onClick = onMarkAllAsRead) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = GmiPrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.markAllRead,
                                color = GmiPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips: All / Unread (N) / Read
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = currentFilter == NotificationFilter.ALL,
                        onClick = { currentFilter = NotificationFilter.ALL },
                        label = { Text(strings.filterAll) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GmiNavy,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = currentFilter == NotificationFilter.UNREAD,
                        onClick = { currentFilter = NotificationFilter.UNREAD },
                        label = { Text(strings.filterUnread.formatArgs(unreadCount)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GmiNavy,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = currentFilter == NotificationFilter.READ,
                        onClick = { currentFilter = NotificationFilter.READ },
                        label = { Text(strings.filterRead) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GmiNavy,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Notification Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.noNotifications,
                            color = GmiTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { notif ->
                    var isExpanded by remember { mutableStateOf(false) }

                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteNotification(notif.id)
                                    true
                                } else if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    onMarkAsRead(notif.id)
                                    false
                                } else false
                            }
                        ),
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFC62828)
                                )
                            }
                        }
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GmiSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Unread indicator dot
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 5.dp)
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (!notif.isRead) Color(0xFF1E88E5) else Color.Transparent)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = GmiNavy
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = notif.body,
                                            fontSize = 13.sp,
                                            color = GmiTextSecondary,
                                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.animateContentSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = notif.relativeTime,
                                        fontSize = 11.sp,
                                        color = GmiTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Row: External Link / Reply to GMI / Mark Read / Delete
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (notif.allowsReplyToGmi) {
                                            Text(
                                                text = strings.replyToGmi,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GmiPrimaryBlue,
                                                modifier = Modifier
                                                    .clickable { onReplyToGmi() }
                                                    .padding(end = 12.dp)
                                            )
                                        }
                                        if (notif.externalUrl != null) {
                                            Text(
                                                text = "External Link ↗",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = GmiOrangeCurrent,
                                                modifier = Modifier.clickable { onOpenExternalLink(notif.externalUrl) }
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (!notif.isRead) {
                                            IconButton(
                                                onClick = { onMarkAsRead(notif.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Mark as read",
                                                    tint = GmiGreenCompleted,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { onDeleteNotification(notif.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFC62828),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
