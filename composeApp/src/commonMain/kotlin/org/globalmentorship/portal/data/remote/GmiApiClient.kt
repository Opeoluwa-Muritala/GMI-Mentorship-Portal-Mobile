package org.globalmentorship.portal.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.globalmentorship.portal.domain.models.*

/**
 * GMI Mentorship Portal API Client Specification.
 *
 * This interface defines all REST endpoints consumed by the GMI mobile client,
 * matching the information architecture of https://www.gmiportal.org/.
 */
interface GmiApiClient {

    // ==========================================
    // 1. Authentication & Security Endpoints
    // ==========================================

    /**
     * Authenticates a student or mentor using email and password.
     * Portal note: Requires matching email used during GMI registration.
     * Route: POST /api/v1/auth/login
     */
    suspend fun login(request: LoginRequest): AuthResponse

    /**
     * Refreshes an expired JWT access token using the refresh token.
     * Route: POST /api/v1/auth/refresh
     */
    suspend fun refreshToken(refreshToken: String): AuthResponse

    /**
     * Initiates password reset procedure.
     * Route: POST /api/v1/auth/forgot-password
     */
    suspend fun requestPasswordReset(email: String): ApiResponse<Unit>

    /**
     * Verifies user response for sign-in CAPTCHA verification challenge.
     * Route: POST /api/v1/auth/verify-captcha
     */
    suspend fun verifyCaptcha(token: String): CaptchaVerificationResponse

    /**
     * Terminates active session and invalidates bearer token.
     * Route: POST /api/v1/auth/logout
     */
    suspend fun logout(): ApiResponse<Unit>

    // ==========================================
    // 2. User Profile & Account Settings
    // ==========================================

    /**
     * Fetches current signed-in user's profile, role, partner, and saved timezone.
     * Route: GET /api/v1/account/profile
     */
    suspend fun getProfile(): UserProfile

    /**
     * Updates user's first name, last name, email, and timezone.
     * Route: PUT /api/v1/account/profile
     */
    suspend fun updateProfile(request: UpdateProfileRequest): UserProfile

    /**
     * Uploads avatar image (max 500KB, JPEG/PNG/GIF/WebP).
     * Route: POST /api/v1/account/avatar
     */
    suspend fun uploadAvatar(imageData: ByteArray, mimeType: String): AvatarUploadResponse

    /**
     * Retrieves the notification preference matrix (channel x event).
     * Route: GET /api/v1/account/notifications/preferences
     */
    suspend fun getNotificationPreferences(): NotificationPreferences

    /**
     * Updates notification preferences.
     * Route: PUT /api/v1/account/notifications/preferences
     */
    suspend fun updateNotificationPreferences(preferences: NotificationPreferences): NotificationPreferences

    /**
     * Returns the user's mentorship history.
     * Route: GET /api/v1/account/mentorship-history
     */
    suspend fun getMentorshipHistory(): List<MentorshipHistoryItem>

    /**
     * Retrieves list of uploaded resumes.
     * Route: GET /api/v1/account/resumes
     */
    suspend fun getResumes(): List<ResumeItem>

    /**
     * Uploads a resume document (PDF/DOC/DOCX up to 10MB) or external link.
     * Route: POST /api/v1/account/resumes
     */
    suspend fun uploadResume(request: UploadResumeRequest): ResumeItem

    /**
     * Deletes a previously uploaded resume.
     * Route: DELETE /api/v1/account/resumes/{id}
     */
    suspend fun deleteResume(resumeId: String): ApiResponse<Unit>

    // ==========================================
    // 3. Mentorship Program & Sessions
    // ==========================================

    /**
     * Returns overall progress, completed sessions count, and current active session.
     * Route: GET /api/v1/program/overview
     */
    suspend fun getProgramOverview(): ProgramOverview

    /**
     * Returns all 10 program sessions with unlock states.
     * Route: GET /api/v1/program/sessions
     */
    suspend fun getSessions(): List<MentorshipSession>

    /**
     * Fetches detailed objectives and downloadable worksheets for a single session.
     * Route: GET /api/v1/program/sessions/{sessionNumber}
     */
    suspend fun getSessionDetail(sessionNumber: Int): MentorshipSession

    /**
     * Marks a session complete after confirmation.
     * Automatically unlocks the subsequent session and triggers partner notification.
     * Route: POST /api/v1/program/sessions/{sessionNumber}/complete
     */
    suspend fun markSessionComplete(sessionNumber: Int): SessionCompletionResponse

    // ==========================================
    // 4. Mutual-Availability Calendar Scheduler
    // ==========================================

    /**
     * Fetches mutual availability slots, partner slots, and suggested/confirmed meetings for a given month.
     * Route: GET /api/v1/calendar/month?year={year}&month={month}&timezone={timezone}
     */
    suspend fun getMonthAvailability(year: Int, month: Int, timezone: String): MonthAvailabilityResponse

    /**
     * Saves user's submitted available time slots for a specific date.
     * Route: POST /api/v1/calendar/availability
     */
    suspend fun saveAvailability(request: SaveAvailabilityRequest): ApiResponse<Unit>

    /**
     * Proposes a meeting time to the partner.
     * Route: POST /api/v1/calendar/meetings/suggest
     */
    suspend fun suggestMeeting(request: SuggestMeetingRequest): SuggestedMeeting

    /**
     * Confirms a proposed meeting time.
     * Route: POST /api/v1/calendar/meetings/{meetingId}/confirm
     */
    suspend fun confirmMeeting(meetingId: String): SuggestedMeeting

    /**
     * Declines a proposed meeting time.
     * Route: POST /api/v1/calendar/meetings/{meetingId}/decline
     */
    suspend fun declineMeeting(meetingId: String): ApiResponse<Unit>

    // ==========================================
    // 5. Notifications
    // ==========================================

    /**
     * Fetches paginated/filtered notifications list with unread counter.
     * Route: GET /api/v1/notifications
     */
    suspend fun getNotifications(): NotificationsResponse

    /**
     * Marks a single notification as read.
     * Route: POST /api/v1/notifications/{id}/read
     */
    suspend fun markNotificationRead(id: String): ApiResponse<Unit>

    /**
     * Marks all notifications as read.
     * Route: POST /api/v1/notifications/mark-all-read
     */
    suspend fun markAllNotificationsRead(): ApiResponse<Unit>

    /**
     * Deletes a notification.
     * Route: DELETE /api/v1/notifications/{id}
     */
    suspend fun deleteNotification(id: String): ApiResponse<Unit>

    // ==========================================
    // 6. Messages & Conversations
    // ==========================================

    /**
     * Retrieves conversations categorized by tab (Inbox, Archived, Deleted).
     * Route: GET /api/v1/messages/threads?tab={tab}&search={search}
     */
    suspend fun getThreads(tab: MessageTab, searchQuery: String? = null): List<MessageThread>

    /**
     * Retrieves full chronological chat history for a thread.
     * Route: GET /api/v1/messages/threads/{threadId}
     */
    suspend fun getThreadMessages(threadId: String): List<ChatMessage>

    /**
     * Creates a new conversation thread with fixed recipient (Partner or GMI Support).
     * Route: POST /api/v1/messages/threads
     */
    suspend fun createThread(request: CreateThreadRequest): MessageThread

    /**
     * Sends a reply message within an existing thread.
     * Route: POST /api/v1/messages/threads/{threadId}/messages
     */
    suspend fun sendMessage(threadId: String, request: SendMessageRequest): ChatMessage

    /**
     * Archives or unarchives a thread.
     * Route: POST /api/v1/messages/threads/{threadId}/archive
     */
    suspend fun setThreadArchived(threadId: String, archived: Boolean): ApiResponse<Unit>

    /**
     * Moves a thread to deleted or restores it.
     * Route: POST /api/v1/messages/threads/{threadId}/trash
     */
    suspend fun setThreadDeleted(threadId: String, deleted: Boolean): ApiResponse<Unit>
}

// ==========================================
// DTOs & Request/Response Models
// ==========================================

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false,
    val captchaToken: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: UserProfile
)

@Serializable
data class CaptchaVerificationResponse(
    val success: Boolean,
    val challengeTimestamp: String? = null
)

@Serializable
data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val timezone: String?
)

@Serializable
data class AvatarUploadResponse(
    val avatarUrl: String
)

@Serializable
data class UploadResumeRequest(
    val name: String,
    val resourceType: ResumeResourceType,
    val externalUrl: String? = null,
    val fileBase64: String? = null,
    val fileName: String? = null
)

@Serializable
data class SessionCompletionResponse(
    val sessionNumber: Int,
    val nextUnlockedSessionNumber: Int?,
    val partnerNotified: Boolean,
    val certificateUnlocked: Boolean
)

@Serializable
data class MonthAvailabilityResponse(
    val year: Int,
    val month: Int,
    val timezone: String,
    val slots: List<TimeSlot>,
    val suggestedMeetings: List<SuggestedMeeting>
)

@Serializable
data class SaveAvailabilityRequest(
    val dateIso: String,
    val timeSlotRanges: List<String> // e.g. ["09:00-10:00", "14:00-15:00"]
)

@Serializable
data class SuggestMeetingRequest(
    val dateIso: String,
    val startTimeIso: String,
    val endTimeIso: String,
    val sessionNumber: Int,
    val note: String? = null
)

@Serializable
data class NotificationsResponse(
    val unreadCount: Int,
    val items: List<NotificationItem>
)

@Serializable
data class CreateThreadRequest(
    val recipientId: String,
    val subject: String,
    val contentHtml: String
)

@Serializable
data class SendMessageRequest(
    val contentHtml: String
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)
