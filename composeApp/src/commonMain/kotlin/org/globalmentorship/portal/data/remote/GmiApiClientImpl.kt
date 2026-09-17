package org.globalmentorship.portal.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.globalmentorship.portal.domain.models.*

class GmiApiClientImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://www.gmiportal.org/api/v1"
) : GmiApiClient {

    override suspend fun login(request: LoginRequest): AuthResponse {
        return httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(refreshToken: String): AuthResponse {
        return httpClient.post("$baseUrl/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refreshToken" to refreshToken))
        }.body()
    }

    override suspend fun requestPasswordReset(email: String): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }.body()
    }

    override suspend fun verifyCaptcha(token: String): CaptchaVerificationResponse {
        return httpClient.post("$baseUrl/auth/verify-captcha") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("token" to token))
        }.body()
    }

    override suspend fun logout(): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/auth/logout").body()
    }

    override suspend fun getProfile(): UserProfile {
        return httpClient.get("$baseUrl/account/profile").body()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserProfile {
        return httpClient.put("$baseUrl/account/profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun uploadAvatar(imageData: ByteArray, mimeType: String): AvatarUploadResponse {
        return httpClient.post("$baseUrl/account/avatar") {
            contentType(ContentType.parse(mimeType))
            setBody(imageData)
        }.body()
    }

    override suspend fun getNotificationPreferences(): NotificationPreferences {
        return httpClient.get("$baseUrl/account/notifications/preferences").body()
    }

    override suspend fun updateNotificationPreferences(preferences: NotificationPreferences): NotificationPreferences {
        return httpClient.put("$baseUrl/account/notifications/preferences") {
            contentType(ContentType.Application.Json)
            setBody(preferences)
        }.body()
    }

    override suspend fun getMentorshipHistory(): List<MentorshipHistoryItem> {
        return httpClient.get("$baseUrl/account/mentorship-history").body()
    }

    override suspend fun getResumes(): List<ResumeItem> {
        return httpClient.get("$baseUrl/account/resumes").body()
    }

    override suspend fun uploadResume(request: UploadResumeRequest): ResumeItem {
        return httpClient.post("$baseUrl/account/resumes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteResume(resumeId: String): ApiResponse<Unit> {
        return httpClient.delete("$baseUrl/account/resumes/$resumeId").body()
    }

    override suspend fun getProgramOverview(): ProgramOverview {
        return httpClient.get("$baseUrl/program/overview").body()
    }

    override suspend fun getSessions(): List<MentorshipSession> {
        return httpClient.get("$baseUrl/program/sessions").body()
    }

    override suspend fun getSessionDetail(sessionNumber: Int): MentorshipSession {
        return httpClient.get("$baseUrl/program/sessions/$sessionNumber").body()
    }

    override suspend fun markSessionComplete(sessionNumber: Int): SessionCompletionResponse {
        return httpClient.post("$baseUrl/program/sessions/$sessionNumber/complete").body()
    }

    override suspend fun getMonthAvailability(year: Int, month: Int, timezone: String): MonthAvailabilityResponse {
        return httpClient.get("$baseUrl/calendar/month") {
            parameter("year", year)
            parameter("month", month)
            parameter("timezone", timezone)
        }.body()
    }

    override suspend fun saveAvailability(request: SaveAvailabilityRequest): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/calendar/availability") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun suggestMeeting(request: SuggestMeetingRequest): SuggestedMeeting {
        return httpClient.post("$baseUrl/calendar/meetings/suggest") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun confirmMeeting(meetingId: String): SuggestedMeeting {
        return httpClient.post("$baseUrl/calendar/meetings/$meetingId/confirm").body()
    }

    override suspend fun declineMeeting(meetingId: String): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/calendar/meetings/$meetingId/decline").body()
    }

    override suspend fun getNotifications(): NotificationsResponse {
        return httpClient.get("$baseUrl/notifications").body()
    }

    override suspend fun markNotificationRead(id: String): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/notifications/$id/read").body()
    }

    override suspend fun markAllNotificationsRead(): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/notifications/mark-all-read").body()
    }

    override suspend fun deleteNotification(id: String): ApiResponse<Unit> {
        return httpClient.delete("$baseUrl/notifications/$id").body()
    }

    override suspend fun getThreads(tab: MessageTab, searchQuery: String?): List<MessageThread> {
        return httpClient.get("$baseUrl/messages/threads") {
            parameter("tab", tab.name)
            if (searchQuery != null) parameter("search", searchQuery)
        }.body()
    }

    override suspend fun getThreadMessages(threadId: String): List<ChatMessage> {
        return httpClient.get("$baseUrl/messages/threads/$threadId").body()
    }

    override suspend fun createThread(request: CreateThreadRequest): MessageThread {
        return httpClient.post("$baseUrl/messages/threads") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun sendMessage(threadId: String, request: SendMessageRequest): ChatMessage {
        return httpClient.post("$baseUrl/messages/threads/$threadId/messages") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun setThreadArchived(threadId: String, archived: Boolean): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/messages/threads/$threadId/archive") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("archived" to archived))
        }.body()
    }

    override suspend fun setThreadDeleted(threadId: String, deleted: Boolean): ApiResponse<Unit> {
        return httpClient.post("$baseUrl/messages/threads/$threadId/trash") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("deleted" to deleted))
        }.body()
    }
}
