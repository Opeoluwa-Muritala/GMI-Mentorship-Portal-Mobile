package org.globalmentorship.portal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.UserProfile
import org.globalmentorship.portal.domain.models.UserRole
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@Composable
fun GmiTopAppBar(
    user: UserProfile?,
    onUserChipClicked: () -> Unit,
    onRoleSwitchDemo: () -> Unit,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GmiNavy,
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Portal Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GMI",
                        color = GmiNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "GMI Mentorship Portal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Global Mentorship Initiative",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            // User Chip with Avatar, Name, and Role Badge
            if (user != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onUserChipClicked() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Circular Avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GmiPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = user.firstName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = if (user.role == UserRole.STUDENT) "Student" else "Mentor",
                            color = if (user.role == UserRole.STUDENT) Color(0xFFFFD54F) else Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = onRoleSwitchDemo,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Student/Mentor",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GmiBottomBar(
    currentRoute: String,
    unreadNotificationsCount: Int,
    onNavigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = GmiNavy,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val strings = LocalStrings.current

        val navItems = listOf(
            Triple("dashboard", strings.dashboard, Icons.Default.Dashboard),
            Triple("my-program", strings.mentorshipProgram, Icons.Default.School),
            Triple("my-program/calendar", strings.calendar, Icons.Default.CalendarMonth),
            Triple("messages", strings.messages, Icons.AutoMirrored.Filled.Chat),
            Triple("notifications", strings.notifications, Icons.Default.Notifications)
        )

        navItems.forEach { (route, label, icon) ->
            val isSelected = currentRoute.startsWith(route)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToRoute(route) },
                icon = {
                    if (route == "notifications" && unreadNotificationsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFE53935),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(imageVector = icon, contentDescription = label)
                        }
                    } else {
                        Icon(imageVector = icon, contentDescription = label)
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GmiNavy,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.65f),
                    unselectedTextColor = Color.White.copy(alpha = 0.65f)
                )
            )
        }
    }
}

@Composable
fun OfflineNoticeBanner(
    isOffline: Boolean,
    outboundQueueCount: Int,
    onRetryClicked: () -> Unit
) {
    if (isOffline) {
        val strings = LocalStrings.current
        Surface(
            color = Color(0xFFFFF3CD),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Offline",
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (outboundQueueCount > 0)
                            "${strings.offlineBanner} ($outboundQueueCount pending)"
                        else
                            strings.offlineBanner,
                        color = Color(0xFF856404),
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = onRetryClicked) {
                    Text(
                        text = strings.retry,
                        color = Color(0xFF856404),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
