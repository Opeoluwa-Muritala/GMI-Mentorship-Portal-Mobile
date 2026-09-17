package org.globalmentorship.portal.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationChannel(val displayName: String) {
    IN_PORTAL("In-portal notifications"),
    EMAIL("Email notifications"),
    PUSH("Push notifications")
}

@Serializable
enum class NotificationEventType(
    val title: String,
    val helperText: String
) {
    PARTNER_CREATED_CALENDAR(
        "Partner created a calendar selection",
        "when your partner suggests a new meeting time"
    ),
    PARTNER_UPDATED_CALENDAR(
        "Partner updated a calendar selection",
        "when your partner changes the meeting time"
    ),
    PARTNER_CANCELLED_CALENDAR(
        "Partner cancelled a calendar selection",
        "when your partner cancels a scheduled meeting"
    ),
    PARTNER_CONFIRMED_CALENDAR(
        "Partner confirmed a calendar selection",
        "when your partner confirms a meeting time"
    ),
    SESSION_COMPLETION_PARTNER(
        "Session completion – partner notification",
        "when your partner marks a session complete; includes a link to the next session"
    )
}

@Serializable
data class NotificationPreferences(
    val settings: Map<String, Boolean> = defaultSettings()
) {
    fun isEnabled(channel: NotificationChannel, event: NotificationEventType): Boolean {
        val key = makeKey(channel, event)
        return settings[key] ?: true
    }

    fun withToggled(channel: NotificationChannel, event: NotificationEventType, enabled: Boolean): NotificationPreferences {
        val newMap = settings.toMutableMap()
        newMap[makeKey(channel, event)] = enabled
        return copy(settings = newMap)
    }

    companion object {
        fun makeKey(channel: NotificationChannel, event: NotificationEventType): String =
            "${channel.name}_${event.name}"

        fun defaultSettings(): Map<String, Boolean> {
            val map = mutableMapOf<String, Boolean>()
            NotificationChannel.entries.forEach { ch ->
                NotificationEventType.entries.forEach { ev ->
                    map[makeKey(ch, ev)] = true
                }
            }
            return map
        }
    }
}

@Serializable
data class MentorshipHistoryItem(
    val id: String,
    val partnerName: String,
    val partnerEmail: String,
    val status: String = "Active",
    val programName: String = "GMI Mentorship",
    val createdDate: String
)

@Serializable
enum class ResumeResourceType {
    FILE_UPLOAD,
    EXTERNAL_LINK
}

@Serializable
data class ResumeItem(
    val id: String,
    val name: String,
    val resourceType: ResumeResourceType,
    val fileUrlOrPath: String,
    val uploadedAt: String,
    val sizeDisplay: String? = null
)
