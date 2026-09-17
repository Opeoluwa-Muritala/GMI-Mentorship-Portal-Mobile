package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.formatArgs
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@Composable
fun DashboardScreen(
    user: UserProfile?,
    programOverview: ProgramOverview,
    currentSession: MentorshipSession?,
    recentMessages: List<MessageThread>,
    recentNotifications: List<NotificationItem>,
    onNavigateToSession: (Int) -> Unit,
    onNavigateToProgram: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToThread: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSendMailToGmi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GmiBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Greeting & Pause Notice
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strings.welcomeBack.formatArgs(user?.firstName ?: "there"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Pause notice card with mailto link
                Surface(
                    color = GmiBlueLight,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = GmiPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.pauseNotice,
                            style = MaterialTheme.typography.bodySmall,
                            color = GmiNavy,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSendMailToGmi("info@globalmentorship.org") }
                        )
                    }
                }
            }
        }

        // 2. My Mentor / My Student(s) Card (Top Card 1)
        item {
            val partner = user?.partner
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (user?.role == UserRole.STUDENT) strings.myMentor else strings.myStudent,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (partner != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GmiPrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = partner.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = partner.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = GmiTextPrimary
                                )
                                Text(
                                    text = "${partner.titleOrMajor} • ${partner.organizationOrUniversity}",
                                    fontSize = 12.sp,
                                    color = GmiTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = partner.email,
                                    fontSize = 12.sp,
                                    color = GmiPrimaryBlue
                                )
                            }

                            // Deep link message icon
                            IconButton(
                                onClick = onNavigateToMessages,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GmiBlueLight)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Message Partner",
                                    tint = GmiPrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No partner currently assigned.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary
                        )
                    }
                }
            }
        }

        // 3. Program Progress Card (Top Card 2)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.programProgress,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Text(
                            text = "${programOverview.progressPercentage}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiPrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { programOverview.completedSessions.toFloat() / programOverview.totalSessions.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GmiGreenCompleted,
                        trackColor = GmiGrayLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.sessionsCompleted.formatArgs(programOverview.completedSessions),
                            style = MaterialTheme.typography.bodySmall,
                            color = GmiTextSecondary
                        )
                        Text(
                            text = strings.gmiMentorship,
                            style = MaterialTheme.typography.bodySmall,
                            color = GmiNavy,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Upcoming Sessions Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.upcomingSessions,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentSession != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSession.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = GmiTextPrimary
                                )
                                Text(
                                    text = "Session ${currentSession.sessionNumber} • ${currentSession.programName}",
                                    fontSize = 12.sp,
                                    color = GmiTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onNavigateToSession(currentSession.sessionNumber) },
                                colors = ButtonDefaults.buttonColors(containerColor = GmiOrangeCurrent),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = strings.goToSession,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "All sessions completed!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary
                        )
                    }
                }
            }
        }

        // 5. Recent Messages Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.recentMessages,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        TextButton(onClick = onNavigateToMessages) {
                            Text(
                                text = strings.viewAll,
                                color = GmiPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (recentMessages.isEmpty()) {
                        Text(
                            text = strings.noRecentMessages,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        recentMessages.take(2).forEach { thread ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToThread(thread.id) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GmiBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        tint = GmiNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = thread.recipient.displayName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = GmiTextPrimary
                                    )
                                    Text(
                                        text = thread.lastSnippet,
                                        fontSize = 12.sp,
                                        color = GmiTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = thread.relativeTime,
                                    fontSize = 11.sp,
                                    color = GmiTextSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = GmiBorder)

                    // Request Help Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSendMailToGmi("info@globalmentorship.org") }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = null,
                            tint = GmiPrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.requestHelp,
                            color = GmiPrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 6. Recent Notifications (Last 4 items)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.recentNotifications,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        TextButton(onClick = onNavigateToNotifications) {
                            Text(
                                text = strings.viewAll,
                                color = GmiPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (recentNotifications.isEmpty()) {
                        Text(
                            text = strings.noNotifications,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        recentNotifications.take(4).forEach { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToNotifications() }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Unread dot
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (!notif.isRead) Color(0xFF1E88E5) else Color.Transparent)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = GmiTextPrimary
                                    )
                                    Text(
                                        text = notif.body,
                                        fontSize = 12.sp,
                                        color = GmiTextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = notif.relativeTime,
                                    fontSize = 10.sp,
                                    color = GmiTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
