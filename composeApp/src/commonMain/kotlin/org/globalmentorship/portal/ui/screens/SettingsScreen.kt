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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.AppLanguage
import org.globalmentorship.portal.localization.LocalStrings
import org.globalmentorship.portal.localization.LocalizationManager
import org.globalmentorship.portal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: UserProfile?,
    notificationPrefs: NotificationPreferences,
    mentorshipHistory: List<MentorshipHistoryItem>,
    resumes: List<ResumeItem>,
    availableTimezones: List<String>,
    currentLanguage: AppLanguage,
    onSaveProfile: (firstName: String, lastName: String, email: String, timezone: String?) -> Unit,
    onUploadAvatar: () -> Unit,
    onToggleNotificationPref: (NotificationChannel, NotificationEventType, Boolean) -> Unit,
    onUploadResume: (name: String, type: ResumeResourceType, externalUrl: String?) -> Unit,
    onDeleteResume: (String) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    isOnlineSyncEnabled: Boolean = true,
    apiBaseUrl: String = "https://www.gmiportal.org/api/v1",
    lastSyncTimestamp: String? = null,
    pendingOutboundCount: Int = 0,
    isSyncing: Boolean = false,
    syncStatusMessage: String? = null,
    onToggleOnlineSync: (Boolean) -> Unit = {},
    onUpdateApiBaseUrl: (String) -> Unit = {},
    onTriggerSyncNow: () -> Unit = {},
    onResetDatabase: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        strings.tabGeneral,
        strings.tabNotifications,
        strings.tabMentorshipHistory,
        strings.tabResume,
        "Database & API"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GmiBackground)
    ) {
        // Header
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
                Text(
                    text = strings.accountDetailsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GmiNavy
                )
                Text(
                    text = strings.manageAccountSubtitle,
                    fontSize = 12.sp,
                    color = GmiTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = GmiSurface,
                    contentColor = GmiNavy,
                    edgePadding = 0.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: GENERAL
                    item {
                        GeneralTab(
                            user = user,
                            availableTimezones = availableTimezones,
                            onSave = onSaveProfile,
                            onUploadPhoto = onUploadAvatar
                        )
                    }
                }
                1 -> {
                    // TAB 2: NOTIFICATIONS (3 Groups x 5 Events)
                    item {
                        NotificationsTab(
                            prefs = notificationPrefs,
                            onToggle = onToggleNotificationPref
                        )
                    }
                }
                2 -> {
                    // TAB 3: MENTORSHIP HISTORY
                    items(mentorshipHistory, key = { it.id }) { item ->
                        MentorshipHistoryCard(item)
                    }
                }
                3 -> {
                    // TAB 4: RESUME
                    item {
                        ResumeTab(
                            resumes = resumes,
                            onUpload = onUploadResume,
                            onDelete = onDeleteResume
                        )
                    }
                }
                4 -> {
                    // TAB 5: DATABASE & ONLINE API SETUP
                    item {
                        DatabaseAndApiTab(
                            isOnlineSyncEnabled = isOnlineSyncEnabled,
                            apiBaseUrl = apiBaseUrl,
                            lastSyncTimestamp = lastSyncTimestamp,
                            pendingOutboundCount = pendingOutboundCount,
                            isSyncing = isSyncing,
                            syncStatusMessage = syncStatusMessage,
                            onToggleOnlineSync = onToggleOnlineSync,
                            onUpdateApiBaseUrl = onUpdateApiBaseUrl,
                            onTriggerSyncNow = onTriggerSyncNow,
                            onResetDatabase = onResetDatabase
                        )
                    }
                }
            }

            // Language Switcher & Logout at end of settings
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GmiSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.languageSwitcher,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = GmiNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                FilterChip(
                                    selected = currentLanguage == lang,
                                    onClick = { onSelectLanguage(lang) },
                                    label = { Text(lang.displayName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GmiNavy,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = GmiBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Logout button
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = strings.logout, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneralTab(
    user: UserProfile?,
    availableTimezones: List<String>,
    onSave: (firstName: String, lastName: String, email: String, timezone: String?) -> Unit,
    onUploadPhoto: () -> Unit
) {
    val strings = LocalStrings.current
    var firstName by remember(user) { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember(user) { mutableStateOf(user?.lastName ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var selectedTimezone by remember(user) { mutableStateOf(user?.timezone ?: "") }
    var timezoneSearchQuery by remember { mutableStateOf("") }
    var timezoneExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = GmiSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Read-Only Account Info
            Text(
                text = "Account Information",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GmiNavy
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${strings.roleLabel}:", fontSize = 12.sp, color = GmiTextSecondary)
                Text(text = if (user?.role == UserRole.STUDENT) "Student" else "Mentor", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GmiNavy)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${strings.createdDate}:", fontSize = 12.sp, color = GmiTextSecondary)
                Text(text = user?.createdAt ?: "2025-01-10", fontSize = 12.sp, color = GmiTextPrimary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${strings.updatedDate}:", fontSize = 12.sp, color = GmiTextSecondary)
                Text(text = user?.updatedAt ?: "2025-02-14", fontSize = 12.sp, color = GmiTextPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GmiBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Picture Section
            Text(
                text = "Profile Picture",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GmiNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GmiPrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.initials ?: "GMI",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.profilePhotoGuidance,
                        fontSize = 11.sp,
                        color = GmiTextSecondary,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onUploadPhoto,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(strings.uploadPhoto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GmiNavy)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GmiBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Update Personal Information
            Text(
                text = "Update Your Information",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GmiNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("${strings.firstName} *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("${strings.lastName} *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("${strings.email} *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Timezone Picker (Required for Calendar)
            Text(
                text = "${strings.timezone} *",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GmiNavy
            )
            Text(
                text = strings.timezoneExplainer,
                fontSize = 11.sp,
                color = GmiOrangeCurrent,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = timezoneExpanded,
                onExpandedChange = { timezoneExpanded = !timezoneExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTimezone,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timezoneExpanded) },
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenu(
                    expanded = timezoneExpanded,
                    onDismissRequest = { timezoneExpanded = false }
                ) {
                    availableTimezones.forEach { tz ->
                        DropdownMenuItem(
                            text = { Text(tz, fontSize = 13.sp) },
                            onClick = {
                                selectedTimezone = tz
                                timezoneExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onSave(firstName, lastName, email, selectedTimezone) },
                colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.saveChanges, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NotificationsTab(
    prefs: NotificationPreferences,
    onToggle: (NotificationChannel, NotificationEventType, Boolean) -> Unit
) {
    val strings = LocalStrings.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            color = GmiBlueLight,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.notificationsDefaultGuidance,
                fontSize = 12.sp,
                color = GmiNavy,
                modifier = Modifier.padding(12.dp)
            )
        }

        NotificationChannel.entries.forEach { channel ->
            Card(
                colors = CardDefaults.cardColors(containerColor = GmiSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = channel.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    NotificationEventType.entries.forEach { event ->
                        val isChecked = prefs.isEnabled(channel, event)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GmiTextPrimary
                                )
                                Text(
                                    text = event.helperText,
                                    fontSize = 11.sp,
                                    color = GmiTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            Switch(
                                checked = isChecked,
                                onCheckedChange = { onToggle(channel, event, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GmiGreenCompleted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MentorshipHistoryCard(item: MentorshipHistoryItem) {
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
                    text = item.partnerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GmiNavy
                )
                Surface(
                    color = GmiGreenLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.status,
                        color = GmiGreenCompleted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.partnerEmail, fontSize = 12.sp, color = GmiPrimaryBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.programName, fontSize = 12.sp, color = GmiTextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Started: ${item.createdDate}", fontSize = 11.sp, color = GmiTextSecondary)
        }
    }
}

@Composable
private fun ResumeTab(
    resumes: List<ResumeItem>,
    onUpload: (name: String, type: ResumeResourceType, externalUrl: String?) -> Unit,
    onDelete: (String) -> Unit
) {
    val strings = LocalStrings.current
    var resourceType by remember { mutableStateOf(ResumeResourceType.FILE_UPLOAD) }
    var resumeName by remember { mutableStateOf("") }
    var externalUrl by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = GmiSurface),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.uploadResume,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Resource Type Radio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { resourceType = ResumeResourceType.FILE_UPLOAD }
                    ) {
                        RadioButton(
                            selected = resourceType == ResumeResourceType.FILE_UPLOAD,
                            onClick = { resourceType = ResumeResourceType.FILE_UPLOAD }
                        )
                        Text(strings.resourceTypeFile, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { resourceType = ResumeResourceType.EXTERNAL_LINK }
                    ) {
                        RadioButton(
                            selected = resourceType == ResumeResourceType.EXTERNAL_LINK,
                            onClick = { resourceType = ResumeResourceType.EXTERNAL_LINK }
                        )
                        Text(strings.resourceTypeLink, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = resumeName,
                    onValueChange = { resumeName = it },
                    label = { Text("${strings.documentName} *") },
                    placeholder = { Text("e.g. Alex_Resume_2026.pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (resourceType == ResumeResourceType.FILE_UPLOAD) {
                    Surface(
                        color = GmiBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = strings.selectFile,
                                fontSize = 12.sp,
                                color = GmiTextSecondary
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = externalUrl,
                        onValueChange = { externalUrl = it },
                        label = { Text("${strings.externalUrl} *") },
                        placeholder = { Text("https://drive.google.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (resumeName.isNotBlank()) {
                            onUpload(resumeName, resourceType, externalUrl.ifBlank { null })
                            resumeName = ""
                            externalUrl = ""
                        }
                    },
                    enabled = resumeName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.uploadResume, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Uploaded Resumes List
        Card(
            colors = CardDefaults.cardColors(containerColor = GmiSurface),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.uploadedResumes,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (resumes.isEmpty()) {
                    Text(
                        text = "No resumes uploaded yet.",
                        fontSize = 12.sp,
                        color = GmiTextSecondary
                    )
                } else {
                    resumes.forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (res.resourceType == ResumeResourceType.FILE_UPLOAD) Icons.Default.Description else Icons.Default.Link,
                                    contentDescription = null,
                                    tint = GmiNavy
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = res.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = GmiNavy
                                    )
                                    Text(
                                        text = "Uploaded ${res.uploadedAt} ${res.sizeDisplay?.let { "• $it" } ?: ""}",
                                        fontSize = 11.sp,
                                        color = GmiTextSecondary
                                    )
                                }
                            }

                            IconButton(onClick = { onDelete(res.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = strings.delete,
                                    tint = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseAndApiTab(
    isOnlineSyncEnabled: Boolean,
    apiBaseUrl: String,
    lastSyncTimestamp: String?,
    pendingOutboundCount: Int,
    isSyncing: Boolean,
    syncStatusMessage: String?,
    onToggleOnlineSync: (Boolean) -> Unit,
    onUpdateApiBaseUrl: (String) -> Unit,
    onTriggerSyncNow: () -> Unit,
    onResetDatabase: () -> Unit
) {
    var editableUrl by remember(apiBaseUrl) { mutableStateOf(apiBaseUrl) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. On-Phone Local Database Status Card
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
                        text = "On-Phone Local Database",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GmiNavy
                    )
                    Surface(
                        color = GmiGreenLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = GmiGreenCompleted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "All updates (session completions, messages, calendar availability slots, and notification states) are persistently stored on this device. Data survives app restarts and offline periods.",
                    fontSize = 12.sp,
                    color = GmiTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = GmiBackground,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Local Storage Engine:", fontSize = 11.sp, color = GmiTextSecondary)
                            Text("Multiplatform Settings / SQLite", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GmiNavy)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pending Outbound Actions:", fontSize = 11.sp, color = GmiTextSecondary)
                            Text("$pendingOutboundCount queued", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (pendingOutboundCount > 0) GmiOrangeCurrent else GmiGreenCompleted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Last Synced With API:", fontSize = 11.sp, color = GmiTextSecondary)
                            Text(lastSyncTimestamp ?: "Never (Local Mode)", fontSize = 11.sp, color = GmiTextPrimary)
                        }
                    }
                }
            }
        }

        // 2. Online API Setup & Synchronization
        Card(
            colors = CardDefaults.cardColors(containerColor = GmiSurface),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Online API Setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GmiNavy
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Online API Synchronization",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GmiTextPrimary
                        )
                        Text(
                            text = "Sync local updates to the remote GmiApiClient endpoints",
                            fontSize = 11.sp,
                            color = GmiTextSecondary
                        )
                    }
                    Switch(
                        checked = isOnlineSyncEnabled,
                        onCheckedChange = onToggleOnlineSync,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GmiGreenCompleted
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "API Base URL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GmiNavy
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = editableUrl,
                    onValueChange = {
                        editableUrl = it
                        onUpdateApiBaseUrl(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Sync Now Action
                Button(
                    onClick = onTriggerSyncNow,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = GmiPrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing with API...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Local DB with Online API", fontWeight = FontWeight.Bold)
                    }
                }

                if (syncStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = syncStatusMessage,
                        fontSize = 11.sp,
                        color = GmiNavy,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = GmiBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Reset DB to clean defaults
                OutlinedButton(
                    onClick = onResetDatabase,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Local DB to Seed Data", color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

