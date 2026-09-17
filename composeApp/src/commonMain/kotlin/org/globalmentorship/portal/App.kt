package org.globalmentorship.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.globalmentorship.portal.data.repository.*
import org.globalmentorship.portal.data.local.GmiLocalDatabase
import org.globalmentorship.portal.data.sync.OnlineSyncManager
import org.globalmentorship.portal.domain.models.*
import org.globalmentorship.portal.localization.AppLanguage
import org.globalmentorship.portal.localization.LocalizationManager
import org.globalmentorship.portal.localization.ProvideLocalizedStrings
import org.globalmentorship.portal.ui.components.GmiBottomBar
import org.globalmentorship.portal.ui.components.GmiTopAppBar
import org.globalmentorship.portal.ui.components.OfflineNoticeBanner
import org.globalmentorship.portal.ui.navigation.Screen
import org.globalmentorship.portal.ui.screens.*
import org.globalmentorship.portal.ui.theme.GmiBackground
import org.globalmentorship.portal.ui.theme.GmiTheme

@Composable
fun App(
    localDb: GmiLocalDatabase = remember { GmiLocalDatabase() },
    authRepo: AuthRepository = remember(localDb) { PersistentAuthRepository(localDb) },
    programRepo: ProgramRepository = remember(localDb) { PersistentProgramRepository(localDb) },
    calendarRepo: CalendarRepository = remember(localDb) { PersistentCalendarRepository(localDb) },
    notificationRepo: NotificationRepository = remember(localDb) { PersistentNotificationRepository(localDb) },
    messageRepo: MessageRepository = remember(localDb) { PersistentMessageRepository(localDb) },
    settingsRepo: SettingsRepository = remember(authRepo) { FakeSettingsRepository(authRepo) },
    syncManager: OnlineSyncManager = remember(localDb) { OnlineSyncManager(localDb) }
) {
    val coroutineScope = rememberCoroutineScope()

    // State collections
    val user by authRepo.currentUser.collectAsState()
    val isAuthenticated by authRepo.isAuthenticated.collectAsState()
    val isBiometricEnabled by authRepo.isBiometricEnabled.collectAsState()

    val programOverview by programRepo.programOverview.collectAsState()
    val sessions by programRepo.sessions.collectAsState()
    val outboundQueueCount by programRepo.outboundOfflineQueueCount.collectAsState()

    val currentMonthSlots by calendarRepo.currentMonthSlots.collectAsState()
    val suggestedMeetings by calendarRepo.suggestedMeetings.collectAsState()
    val userTimezone by calendarRepo.selectedTimezone.collectAsState()

    val notifications by notificationRepo.notifications.collectAsState()
    val unreadNotificationsCount by notificationRepo.unreadCount.collectAsState()

    val inboxThreads by messageRepo.inboxThreads.collectAsState()
    val archivedThreads by messageRepo.archivedThreads.collectAsState()
    val deletedThreads by messageRepo.deletedThreads.collectAsState()
    val availableRecipients by messageRepo.availableRecipients.collectAsState()

    val notificationPreferences by settingsRepo.notificationPreferences.collectAsState()
    val mentorshipHistory by settingsRepo.mentorshipHistory.collectAsState()
    val resumes by settingsRepo.resumes.collectAsState()
    val availableTimezones = settingsRepo.availableTimezones

    val currentLanguage by LocalizationManager.currentLanguage.collectAsState()
    val isSyncing by syncManager.isSyncing.collectAsState()
    val syncStatusMessage by syncManager.lastSyncStatus.collectAsState()

    // Navigation Stack State
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var activeThreadId by remember { mutableStateOf<String?>(null) }
    var activeSessionNumber by remember { mutableStateOf<Int?>(null) }
    var isOfflineSimulated by remember { mutableStateOf(false) }

    ProvideLocalizedStrings(language = currentLanguage) {
        GmiTheme {
            if (!isAuthenticated) {
                LoginScreen(
                    isBiometricAvailable = isBiometricEnabled,
                    onLoginSubmit = { email, password, rememberMe ->
                        coroutineScope.launch {
                            authRepo.login(email, password, rememberMe)
                        }
                    },
                    onBiometricUnlock = {
                        coroutineScope.launch {
                            authRepo.loginWithBiometrics()
                        }
                    },
                    onForgotPassword = { _ -> },
                    onOpenGmiWebsite = { _ -> }
                )
            } else {
                Scaffold(
                    topBar = {
                        Column {
                            GmiTopAppBar(
                                user = user,
                                onUserChipClicked = { currentScreen = Screen.Settings },
                                onLogoutClicked = {
                                    coroutineScope.launch {
                                        authRepo.logout()
                                    }
                                }
                            )
                            OfflineNoticeBanner(
                                isOffline = isOfflineSimulated,
                                outboundQueueCount = outboundQueueCount,
                                onRetryClicked = {
                                    coroutineScope.launch {
                                        isOfflineSimulated = false
                                        programRepo.syncOutboundQueue()
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        // Show bottom navigation on top-level tabs
                        if (currentScreen !is Screen.SessionDetail && currentScreen !is Screen.MessageThread) {
                            GmiBottomBar(
                                currentRoute = currentScreen.route,
                                unreadNotificationsCount = unreadNotificationsCount,
                                onNavigateToRoute = { route ->
                                    currentScreen = when (route) {
                                        "dashboard" -> Screen.Dashboard
                                        "my-program" -> Screen.Program
                                        "my-program/calendar" -> Screen.Calendar
                                        "messages" -> Screen.Messages
                                        "notifications" -> Screen.Notifications
                                        else -> Screen.Dashboard
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GmiBackground)
                            .padding(innerPadding)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Dashboard -> {
                                val currentSessionObj = sessions.find { it.status == SessionStatus.CURRENT }
                                DashboardScreen(
                                    user = user,
                                    programOverview = programOverview,
                                    currentSession = currentSessionObj,
                                    recentMessages = inboxThreads,
                                    recentNotifications = notifications,
                                    onNavigateToSession = { sessionNum ->
                                        activeSessionNumber = sessionNum
                                        currentScreen = Screen.SessionDetail(sessionNum)
                                    },
                                    onNavigateToProgram = { currentScreen = Screen.Program },
                                    onNavigateToMessages = { currentScreen = Screen.Messages },
                                    onNavigateToThread = { threadId ->
                                        activeThreadId = threadId
                                        currentScreen = Screen.MessageThread(threadId)
                                    },
                                    onNavigateToNotifications = { currentScreen = Screen.Notifications },
                                    onSendMailToGmi = { _ -> }
                                )
                            }
                            is Screen.Program -> {
                                ProgramScreen(
                                    programOverview = programOverview,
                                    sessions = sessions,
                                    onSelectSession = { sessionNum ->
                                        activeSessionNumber = sessionNum
                                        currentScreen = Screen.SessionDetail(sessionNum)
                                    },
                                    onContactSupport = { currentScreen = Screen.Messages }
                                )
                            }
                            is Screen.SessionDetail -> {
                                val sessionObj = sessions.find { it.sessionNumber == screen.sessionNumber }
                                SessionDetailScreen(
                                    session = sessionObj,
                                    onBackClicked = { currentScreen = Screen.Program },
                                    onOpenMaterial = { _ -> },
                                    onMarkCompleteConfirmed = { sessionNum ->
                                        coroutineScope.launch {
                                            programRepo.markSessionComplete(sessionNum)
                                            currentScreen = Screen.Program
                                        }
                                    }
                                )
                            }
                            is Screen.Calendar -> {
                                CalendarScreen(
                                    userTimezone = userTimezone,
                                    slots = currentMonthSlots,
                                    suggestedMeetings = suggestedMeetings,
                                    onRouteToTimezoneSettings = { currentScreen = Screen.Settings },
                                    onAddAvailabilitySlot = { dateIso, startHour, endHour ->
                                        coroutineScope.launch {
                                            calendarRepo.addAvailabilitySlot(dateIso, startHour, endHour)
                                        }
                                    },
                                    onConfirmMeeting = { meetingId ->
                                        coroutineScope.launch {
                                            calendarRepo.confirmMeeting(meetingId)
                                        }
                                    },
                                    onDeclineMeeting = { meetingId ->
                                        coroutineScope.launch {
                                            calendarRepo.declineMeeting(meetingId)
                                        }
                                    },
                                    onExportMeetingToCalendar = { _ -> }
                                )
                            }
                            is Screen.Notifications -> {
                                NotificationsScreen(
                                    notifications = notifications,
                                    unreadCount = unreadNotificationsCount,
                                    onMarkAsRead = { notifId ->
                                        coroutineScope.launch {
                                            notificationRepo.markAsRead(notifId)
                                        }
                                    },
                                    onMarkAllAsRead = {
                                        coroutineScope.launch {
                                            notificationRepo.markAllAsRead()
                                        }
                                    },
                                    onDeleteNotification = { notifId ->
                                        coroutineScope.launch {
                                            notificationRepo.deleteNotification(notifId)
                                        }
                                    },
                                    onReplyToGmi = {
                                        currentScreen = Screen.Messages
                                    },
                                    onOpenExternalLink = { _ -> },
                                    onRefresh = {
                                        coroutineScope.launch {
                                            notificationRepo.refresh()
                                        }
                                    }
                                )
                            }
                            is Screen.Messages -> {
                                MessagesScreen(
                                    inboxThreads = inboxThreads,
                                    archivedThreads = archivedThreads,
                                    deletedThreads = deletedThreads,
                                    availableRecipients = availableRecipients,
                                    onThreadClicked = { threadId ->
                                        activeThreadId = threadId
                                        currentScreen = Screen.MessageThread(threadId)
                                    },
                                    onCreateThread = { recipientId, subject, message ->
                                        coroutineScope.launch {
                                            val result = messageRepo.createThread(recipientId, subject, message)
                                            result.getOrNull()?.let { newThread ->
                                                activeThreadId = newThread.id
                                                currentScreen = Screen.MessageThread(newThread.id)
                                            }
                                        }
                                    }
                                )
                            }
                            is Screen.MessageThread -> {
                                val threadObj = (inboxThreads + archivedThreads + deletedThreads).find { it.id == screen.threadId }
                                MessageThreadScreen(
                                    thread = threadObj,
                                    messages = threadObj?.messages ?: emptyList(),
                                    onBackClicked = { currentScreen = Screen.Messages },
                                    onSendMessage = { content ->
                                        coroutineScope.launch {
                                            messageRepo.sendMessage(screen.threadId, content)
                                        }
                                    },
                                    onArchiveThread = {
                                        coroutineScope.launch {
                                            messageRepo.archiveThread(screen.threadId)
                                            currentScreen = Screen.Messages
                                        }
                                    },
                                    onDeleteThread = {
                                        coroutineScope.launch {
                                            messageRepo.deleteThread(screen.threadId)
                                            currentScreen = Screen.Messages
                                        }
                                    }
                                )
                            }
                            is Screen.Settings -> {
                                SettingsScreen(
                                    user = user,
                                    notificationPrefs = notificationPreferences,
                                    mentorshipHistory = mentorshipHistory,
                                    resumes = resumes,
                                    availableTimezones = availableTimezones,
                                    currentLanguage = currentLanguage,
                                    onSaveProfile = { firstName, lastName, email, tz ->
                                        coroutineScope.launch {
                                            settingsRepo.updateProfile(firstName, lastName, email, tz)
                                            if (tz != null) calendarRepo.setTimezone(tz)
                                        }
                                    },
                                    onUploadAvatar = {
                                        coroutineScope.launch {
                                            settingsRepo.updateAvatar(ByteArray(1024), "image/png")
                                        }
                                    },
                                    onToggleNotificationPref = { channel, event, enabled ->
                                        coroutineScope.launch {
                                            settingsRepo.updateNotificationPreference(channel, event, enabled)
                                        }
                                    },
                                    onUploadResume = { name, type, externalUrl ->
                                        coroutineScope.launch {
                                            settingsRepo.uploadResume(name, type, externalUrl, null)
                                        }
                                    },
                                    onDeleteResume = { resumeId ->
                                        coroutineScope.launch {
                                            settingsRepo.deleteResume(resumeId)
                                        }
                                    },
                                    onSelectLanguage = { lang ->
                                        LocalizationManager.setLanguage(lang)
                                    },
                                    onLogout = {
                                        coroutineScope.launch {
                                            authRepo.logout()
                                        }
                                    },
                                    isOnlineSyncEnabled = localDb.isOnlineApiSyncEnabled,
                                    apiBaseUrl = localDb.apiBaseUrl,
                                    lastSyncTimestamp = localDb.lastSyncTimestamp,
                                    pendingOutboundCount = outboundQueueCount,
                                    isSyncing = isSyncing,
                                    syncStatusMessage = syncStatusMessage,
                                    onToggleOnlineSync = { localDb.isOnlineApiSyncEnabled = it },
                                    onUpdateApiBaseUrl = { localDb.apiBaseUrl = it },
                                    onTriggerSyncNow = {
                                        coroutineScope.launch {
                                            syncManager.syncNow()
                                            programRepo.refreshSessions()
                                            messageRepo.refreshThreads()
                                            notificationRepo.refresh()
                                        }
                                    },
                                    onResetDatabase = {
                                        coroutineScope.launch {
                                            localDb.clearAllData()
                                            (programRepo as? PersistentProgramRepository)?.resetToDefaults()
                                            programRepo.refreshSessions()
                                            messageRepo.refreshThreads()
                                            notificationRepo.refresh()
                                        }
                                    }
                                )
                            }
                            is Screen.Login -> {
                                // Handled above
                            }
                        }
                    }
                }
            }
        }
    }
}
