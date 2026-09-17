package org.globalmentorship.portal.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationCategory {
    PROGRAM_CHECKIN,
    GMI_BROADCAST,
    CALENDAR_UPDATE,
    SESSION_COMPLETION
}

@Serializable
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timestampIso: String,
    val relativeTime: String,
    val isRead: Boolean,
    val category: NotificationCategory,
    val externalUrl: String? = null,
    val bannerImageUrl: String? = null,
    val allowsReplyToGmi: Boolean = true
)
