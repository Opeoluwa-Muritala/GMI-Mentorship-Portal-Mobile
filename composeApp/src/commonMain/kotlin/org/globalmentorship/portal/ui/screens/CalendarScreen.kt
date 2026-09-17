package org.globalmentorship.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    userTimezone: String?,
    slots: List<TimeSlot>,
    suggestedMeetings: List<SuggestedMeeting>,
    onRouteToTimezoneSettings: () -> Unit,
    onAddAvailabilitySlot: (dateIso: String, startHour: Int, endHour: Int) -> Unit,
    onConfirmMeeting: (String) -> Unit,
    onDeclineMeeting: (String) -> Unit,
    onExportMeetingToCalendar: (SuggestedMeeting) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    // If timezone is unset, BLOCK the screen and prompt the user to route to Settings
    if (userTimezone.isNullOrBlank()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(GmiBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Timezone Required",
                        tint = GmiPrimaryBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.timezoneRequiredTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = strings.timezoneRequiredMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GmiTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onRouteToTimezoneSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.configureTimezone,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    var selectedDay by remember { mutableStateOf(18) }
    var selectedMonthName by remember { mutableStateOf("September 2026") }
    var showSlotBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GmiBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Timezone Header & Instructions Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = GmiPrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Timezone: $userTimezone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GmiNavy
                            )
                        }
                        Text(
                            text = strings.fullCalendarInstructions,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GmiPrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = strings.calendarInstructions,
                        fontSize = 12.sp,
                        color = GmiTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Action: Add Availability
                    Button(
                        onClick = { showSlotBottomSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.addAvailability,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. 4-Color Mutual Availability Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegendChip("Mentor Available", Color(0xFF1976D2), Color(0xFFE3F2FD))
                LegendChip("Student Available", Color(0xFF7B1FA2), Color(0xFFF3E5F5))
                LegendChip("Suggested (Orange)", GmiOrangeCurrent, GmiOrangeLight)
                LegendChip("Confirmed (Green)", GmiGreenCompleted, GmiGreenLight)
            }
        }

        // 3. Month Grid with Previous / Next Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Month", tint = GmiNavy)
                        }
                        Text(
                            text = selectedMonthName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = GmiNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Days of Week Header
                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysOfWeek.forEach { d ->
                            Text(
                                text = d,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GmiTextSecondary,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simplified 30-day Month Grid
                    val days = (1..30).toList()
                    val rows = days.chunked(7)
                    rows.forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            week.forEach { dayNum ->
                                val isToday = dayNum == 17
                                val isPast = dayNum < 17
                                val isSelected = dayNum == selectedDay

                                // Check if slots exist for this date
                                val dateStr = "2026-09-${dayNum.toString().padStart(2, '0')}"
                                val daySlots = slots.filter { it.dateIso == dateStr }

                                val hasConfirmed = daySlots.any { it.type == SlotAvailabilityType.CONFIRMED }
                                val hasSuggested = daySlots.any { it.type == SlotAvailabilityType.SUGGESTED }
                                val hasStudent = daySlots.any { it.type == SlotAvailabilityType.STUDENT_AVAILABLE }
                                val hasMentor = daySlots.any { it.type == SlotAvailabilityType.MENTOR_AVAILABLE }

                                val dotColor = when {
                                    hasConfirmed -> GmiGreenCompleted
                                    hasSuggested -> GmiOrangeCurrent
                                    hasStudent -> Color(0xFF7B1FA2)
                                    hasMentor -> Color(0xFF1976D2)
                                    else -> null
                                }

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) GmiNavy
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = if (isToday) 2.dp else 0.dp,
                                            color = if (isToday) GmiPrimaryBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedDay = dayNum
                                            showSlotBottomSheet = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isSelected -> Color.White
                                                isPast -> GmiGrayLocked.copy(alpha = 0.5f)
                                                else -> GmiTextPrimary
                                            }
                                        )
                                        if (dotColor != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else dotColor)
                                            )
                                        }
                                    }
                                }
                            }
                            // Fill remaining row if week size < 7
                            repeat(7 - week.size) {
                                Spacer(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }
        }

        // 4. Suggested Meeting Times Panel
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
                            text = strings.suggestedMeetingTimes,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GmiNavy
                        )
                        Surface(
                            color = GmiOrangeLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${suggestedMeetings.size} pending",
                                color = GmiOrangeCurrent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (suggestedMeetings.isEmpty()) {
                        Text(
                            text = strings.noSuggestedTimes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GmiTextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        suggestedMeetings.forEach { meeting ->
                            Surface(
                                color = GmiBackground,
                                shape = RoundedCornerShape(10.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = meeting.sessionTitle,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = GmiNavy
                                            )
                                            Text(
                                                text = meeting.displayTime,
                                                fontSize = 12.sp,
                                                color = GmiTextSecondary
                                            )
                                            Text(
                                                text = "Proposed by ${meeting.suggestedByName} (${if (meeting.suggestedByRole == UserRole.MENTOR) "Mentor" else "Student"})",
                                                fontSize = 11.sp,
                                                color = GmiPrimaryBlue
                                            )
                                        }

                                        if (meeting.status == MeetingStatus.CONFIRMED) {
                                            IconButton(onClick = { onExportMeetingToCalendar(meeting) }) {
                                                Icon(
                                                    imageVector = Icons.Default.EventAvailable,
                                                    contentDescription = "Export to System Calendar",
                                                    tint = GmiGreenCompleted
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (meeting.status == MeetingStatus.PENDING) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedButton(
                                                onClick = { onDeclineMeeting(meeting.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = strings.declineMeeting,
                                                    color = Color(0xFFC62828),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { onConfirmMeeting(meeting.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = GmiGreenCompleted),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = strings.acceptMeeting,
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            color = GmiGreenLight,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "✓ Confirmed & Meeting link active",
                                                color = GmiGreenCompleted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

    // Day-Detail Slot Selection Bottom Sheet
    if (showSlotBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSlotBottomSheet = false },
            sheetState = sheetState,
            containerColor = GmiSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "${strings.slotSelectionTitle} • Sep $selectedDay, 2026",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap a time slot to mark your availability for your partner:",
                    fontSize = 12.sp,
                    color = GmiTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val sampleHourRanges = listOf(
                    Pair(9, 10),
                    Pair(10, 11),
                    Pair(14, 15),
                    Pair(15, 16),
                    Pair(16, 17)
                )

                sampleHourRanges.forEach { (start, end) ->
                    val dateIso = "2026-09-${selectedDay.toString().padStart(2, '0')}"
                    Button(
                        onClick = {
                            onAddAvailabilitySlot(dateIso, start, end)
                            showSlotBottomSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GmiBlueLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${start}:00 - ${end}:00 ($userTimezone)",
                                color = GmiNavy,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+ Select Slot",
                                color = GmiPrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LegendChip(
    text: String,
    textColor: Color,
    containerColor: Color
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
