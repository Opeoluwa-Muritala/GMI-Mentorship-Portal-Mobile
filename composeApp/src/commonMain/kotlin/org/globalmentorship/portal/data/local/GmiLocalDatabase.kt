package org.globalmentorship.portal.data.local

import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.globalmentorship.portal.domain.models.*

/**
 * On-device persistent database for Android and iOS.
 *
 * Stores all sessions, conversations, calendar slots, notifications, and user profile
 * locally on the device (Android SharedPreferences / SQLite, iOS NSUserDefaults / Keychain).
 * Survives app restarts and maintains an outbound queue for syncing with the online API.
 */
class GmiLocalDatabase(
    private val settings: Settings = Settings()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    // ==========================================
    // API & Sync Configuration
    // ==========================================
    var isOnlineApiSyncEnabled: Boolean
        get() = settings.getBoolean(KEY_API_SYNC_ENABLED, true)
        set(value) = settings.putBoolean(KEY_API_SYNC_ENABLED, value)

    var apiBaseUrl: String
        get() = settings.getString(KEY_API_BASE_URL, "https://www.gmiportal.org/api/v1")
        set(value) = settings.putString(KEY_API_BASE_URL, value)

    var lastSyncTimestamp: String?
        get() = settings.getStringOrNull(KEY_LAST_SYNC_TIME)
        set(value) {
            if (value != null) settings.putString(KEY_LAST_SYNC_TIME, value)
            else settings.remove(KEY_LAST_SYNC_TIME)
        }

    var authToken: String?
        get() = settings.getStringOrNull(KEY_AUTH_TOKEN)
        set(value) {
            if (value != null) settings.putString(KEY_AUTH_TOKEN, value)
            else settings.remove(KEY_AUTH_TOKEN)
        }

    // ==========================================
    // User Profile
    // ==========================================
    fun saveUserProfile(user: UserProfile) {
        val raw = json.encodeToString(user)
        settings.putString(KEY_USER_PROFILE, raw)
    }

    fun getUserProfile(): UserProfile? {
        val raw = settings.getStringOrNull(KEY_USER_PROFILE) ?: return null
        return try {
            json.decodeFromString<UserProfile>(raw)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // Sessions
    // ==========================================
    fun saveSessions(sessions: List<MentorshipSession>) {
        val raw = json.encodeToString(sessions)
        settings.putString(KEY_SESSIONS, raw)
    }

    fun getSessions(): List<MentorshipSession>? {
        val raw = settings.getStringOrNull(KEY_SESSIONS) ?: return null
        return try {
            json.decodeFromString<List<MentorshipSession>>(raw)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // Messages & Conversations
    // ==========================================
    fun saveThreads(threads: List<MessageThread>) {
        val raw = json.encodeToString(threads)
        settings.putString(KEY_THREADS, raw)
    }

    fun getThreads(): List<MessageThread>? {
        val raw = settings.getStringOrNull(KEY_THREADS) ?: return null
        return try {
            json.decodeFromString<List<MessageThread>>(raw)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // Calendar Availability Slots & Meetings
    // ==========================================
    fun saveCalendarSlots(slots: List<TimeSlot>) {
        val raw = json.encodeToString(slots)
        settings.putString(KEY_CALENDAR_SLOTS, raw)
    }

    fun getCalendarSlots(): List<TimeSlot>? {
        val raw = settings.getStringOrNull(KEY_CALENDAR_SLOTS) ?: return null
        return try {
            json.decodeFromString<List<TimeSlot>>(raw)
        } catch (_: Exception) {
            null
        }
    }

    fun saveSuggestedMeetings(meetings: List<SuggestedMeeting>) {
        val raw = json.encodeToString(meetings)
        settings.putString(KEY_SUGGESTED_MEETINGS, raw)
    }

    fun getSuggestedMeetings(): List<SuggestedMeeting>? {
        val raw = settings.getStringOrNull(KEY_SUGGESTED_MEETINGS) ?: return null
        return try {
            json.decodeFromString<List<SuggestedMeeting>>(raw)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // Notifications
    // ==========================================
    fun saveNotifications(notifications: List<NotificationItem>) {
        val raw = json.encodeToString(notifications)
        settings.putString(KEY_NOTIFICATIONS, raw)
    }

    fun getNotifications(): List<NotificationItem>? {
        val raw = settings.getStringOrNull(KEY_NOTIFICATIONS) ?: return null
        return try {
            json.decodeFromString<List<NotificationItem>>(raw)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // Outbound Online Sync Queue
    // ==========================================
    fun getOutboundQueue(): List<String> {
        val raw = settings.getStringOrNull(KEY_OUTBOUND_QUEUE) ?: return emptyList()
        return try {
            json.decodeFromString<List<String>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun enqueueOutboundAction(actionDescription: String) {
        val queue = getOutboundQueue().toMutableList()
        queue.add(actionDescription)
        settings.putString(KEY_OUTBOUND_QUEUE, json.encodeToString(queue))
    }

    fun clearOutboundQueue() {
        settings.remove(KEY_OUTBOUND_QUEUE)
    }

    fun clearAllData() {
        settings.clear()
    }

    companion object {
        private const val KEY_API_SYNC_ENABLED = "gmi_db_api_sync_enabled"
        private const val KEY_API_BASE_URL = "gmi_db_api_base_url"
        private const val KEY_LAST_SYNC_TIME = "gmi_db_last_sync_time"
        private const val KEY_AUTH_TOKEN = "gmi_db_auth_token"
        private const val KEY_USER_PROFILE = "gmi_db_user_profile"
        private const val KEY_SESSIONS = "gmi_db_sessions"
        private const val KEY_THREADS = "gmi_db_threads"
        private const val KEY_CALENDAR_SLOTS = "gmi_db_calendar_slots"
        private const val KEY_SUGGESTED_MEETINGS = "gmi_db_suggested_meetings"
        private const val KEY_NOTIFICATIONS = "gmi_db_notifications"
        private const val KEY_OUTBOUND_QUEUE = "gmi_db_outbound_queue"
    }
}
