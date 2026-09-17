package org.globalmentorship.portal.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.globalmentorship.portal.domain.models.*

interface AuthRepository {
    val currentUser: StateFlow<UserProfile?>
    val isAuthenticated: StateFlow<Boolean>
    val isBiometricEnabled: StateFlow<Boolean>

    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<UserProfile>
    suspend fun loginWithBiometrics(): Result<UserProfile>
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun verifyCaptcha(token: String): Boolean
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun logout()
    suspend fun switchDemoRole() // Allows testing student vs mentor perspectives seamlessly
}

interface ProgramRepository {
    val programOverview: StateFlow<ProgramOverview>
    val sessions: StateFlow<List<MentorshipSession>>
    val outboundOfflineQueueCount: StateFlow<Int>

    suspend fun refreshSessions()
    suspend fun getSessionDetail(sessionNumber: Int): MentorshipSession?
    suspend fun markSessionComplete(sessionNumber: Int): Result<MentorshipSession>
    suspend fun syncOutboundQueue()
}

interface CalendarRepository {
    val selectedTimezone: StateFlow<String?>
    val currentMonthSlots: StateFlow<List<TimeSlot>>
    val suggestedMeetings: StateFlow<List<SuggestedMeeting>>
    val isTimezoneConfigured: StateFlow<Boolean>

    suspend fun loadMonth(year: Int, month: Int)
    suspend fun setTimezone(timezone: String)
    suspend fun addAvailabilitySlot(dateIso: String, startHour: Int, endHour: Int)
    suspend fun removeAvailabilitySlot(slotId: String)
    suspend fun confirmMeeting(meetingId: String): Result<Unit>
    suspend fun declineMeeting(meetingId: String): Result<Unit>
    suspend fun suggestMeeting(dateIso: String, startTime: String, endTime: String, sessionNumber: Int): Result<SuggestedMeeting>
}

interface NotificationRepository {
    val notifications: StateFlow<List<NotificationItem>>
    val unreadCount: StateFlow<Int>

    suspend fun refresh()
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(id: String)
}

interface MessageRepository {
    val inboxThreads: StateFlow<List<MessageThread>>
    val archivedThreads: StateFlow<List<MessageThread>>
    val deletedThreads: StateFlow<List<MessageThread>>
    val availableRecipients: StateFlow<List<MessageRecipient>>

    suspend fun refreshThreads()
    suspend fun getThreadMessages(threadId: String): List<ChatMessage>
    suspend fun sendMessage(threadId: String, contentHtml: String): Result<ChatMessage>
    suspend fun createThread(recipientId: String, subject: String, contentHtml: String): Result<MessageThread>
    suspend fun archiveThread(threadId: String)
    suspend fun unarchiveThread(threadId: String)
    suspend fun deleteThread(threadId: String)
    suspend fun restoreThread(threadId: String)
}

interface SettingsRepository {
    val userProfile: StateFlow<UserProfile?>
    val notificationPreferences: StateFlow<NotificationPreferences>
    val mentorshipHistory: StateFlow<List<MentorshipHistoryItem>>
    val resumes: StateFlow<List<ResumeItem>>
    val availableTimezones: List<String>

    suspend fun updateProfile(firstName: String, lastName: String, email: String, timezone: String?): Result<UserProfile>
    suspend fun updateAvatar(imageBytes: ByteArray, mimeType: String): Result<String>
    suspend fun updateNotificationPreference(channel: NotificationChannel, event: NotificationEventType, enabled: Boolean)
    suspend fun uploadResume(name: String, resourceType: ResumeResourceType, externalUrl: String?, fileUri: String?): Result<ResumeItem>
    suspend fun deleteResume(resumeId: String): Result<Unit>
}
