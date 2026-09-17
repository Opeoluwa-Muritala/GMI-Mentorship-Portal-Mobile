package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@Composable
fun ProgramScreen(
    programOverview: ProgramOverview,
    sessions: List<MentorshipSession>,
    onSelectSession: (Int) -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GmiBackground)
    ) {
        // Pinned Header & Progress Bar
        Surface(
            color = GmiSurface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.gmiMentorship,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Text(
                            text = programOverview.progressDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = GmiPrimaryBlue
                        )
                    }

                    // "Next: Session N" button that jumps to current session
                    Button(
                        onClick = { onSelectSession(programOverview.currentSessionNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = GmiOrangeCurrent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = strings.nextSession.format(programOverview.currentSessionNumber),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pinned Progress Bar
                LinearProgressIndicator(
                    progress = { programOverview.completedSessions.toFloat() / programOverview.totalSessions.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GmiGreenCompleted,
                    trackColor = GmiGrayLight
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instruction panel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Alert",
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.programInstructionTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.programInstructions,
                            fontSize = 12.sp,
                            color = Color(0xFF4E342E),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.programIntegrityWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB71C1C),
                            lineHeight = 15.sp,
                            modifier = Modifier.clickable { onContactSupport() }
                        )
                    }
                }
            }

            // 10 Sessions List
            items(sessions, key = { it.sessionNumber }) { session ->
                SessionCard(
                    session = session,
                    onCardClick = {
                        if (session.status != SessionStatus.LOCKED) {
                            onSelectSession(session.sessionNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: MentorshipSession,
    onCardClick: () -> Unit
) {
    val strings = LocalStrings.current
    val isLocked = session.status == SessionStatus.LOCKED
    val isCurrent = session.status == SessionStatus.CURRENT
    val isCompleted = session.status == SessionStatus.COMPLETED

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFFF5F5F5) else GmiSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session ${session.sessionNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isLocked) GmiGrayLocked else GmiNavy
                )

                // Status Chip
                when (session.status) {
                    SessionStatus.COMPLETED -> {
                        Surface(
                            color = GmiGreenLight,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = GmiGreenCompleted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.completed,
                                    color = GmiGreenCompleted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    SessionStatus.CURRENT -> {
                        Surface(
                            color = GmiOrangeLight,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Current",
                                    tint = GmiOrangeCurrent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.current,
                                    color = GmiOrangeCurrent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    SessionStatus.LOCKED -> {
                        Surface(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = GmiGrayLocked,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.locked,
                                    color = GmiGrayLocked,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = session.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isLocked) GmiGrayLocked else GmiTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = session.objectivesExcerpt,
                fontSize = 13.sp,
                color = if (isLocked) GmiGrayLocked else GmiTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isCompleted) {
                    OutlinedButton(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = strings.review,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GmiNavy
                        )
                    }
                } else if (isCurrent) {
                    Button(
                        onClick = onCardClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GmiOrangeCurrent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Start Session",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Locked until previous session completes",
                        fontSize = 11.sp,
                        color = GmiGrayLocked,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}
