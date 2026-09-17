package org.globalmentorship.portal.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class MessageTab {
    INBOX,
    ARCHIVED,
    DELETED
}

@Serializable
data class MessageRecipient(
    val id: String,
    val displayName: String,
    val roleTitle: String,
    val email: String,
    val isGmiSupport: Boolean = false
)

@Serializable
data class MessageThread(
    val id: String,
    val subject: String,
    val recipient: MessageRecipient,
    val lastSnippet: String,
    val lastTimestamp: String,
    val relativeTime: String,
    val unreadCount: Int = 0,
    val tab: MessageTab = MessageTab.INBOX,
    val messages: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val isFromSelf: Boolean,
    val contentHtml: String,
    val timestampIso: String,
    val displayTime: String
)
