package org.globalmentorship.portal.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class SessionStatus {
    COMPLETED,
    CURRENT,
    LOCKED
}

@Serializable
data class SessionMaterial(
    val id: String,
    val title: String,
    val fileType: String,
    val downloadUrl: String,
    val sizeDisplay: String,
    val isCachedLocally: Boolean = false,
    val localUri: String? = null
)

@Serializable
data class MentorshipSession(
    val sessionNumber: Int,
    val title: String,
    val programName: String = "GMI Mentorship",
    val status: SessionStatus,
    val objectivesExcerpt: String,
    val fullObjectives: List<String>,
    val materials: List<SessionMaterial>,
    val completedAt: Instant? = null,
    val partnerNotified: Boolean = false
)

@Serializable
data class ProgramOverview(
    val programName: String = "GMI Mentorship",
    val completedSessions: Int,
    val totalSessions: Int = 10,
    val currentSessionNumber: Int
) {
    val progressPercentage: Int get() = if (totalSessions > 0) (completedSessions * 100) / totalSessions else 0
    val progressDisplay: String get() = "$completedSessions/$totalSessions – $progressPercentage%"
}
