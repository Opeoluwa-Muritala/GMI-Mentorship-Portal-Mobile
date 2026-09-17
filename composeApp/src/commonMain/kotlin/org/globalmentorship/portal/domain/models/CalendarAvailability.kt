package org.globalmentorship.portal.domain.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class SlotAvailabilityType {
    MENTOR_AVAILABLE,
    STUDENT_AVAILABLE,
    SUGGESTED,
    CONFIRMED
}

@Serializable
data class TimeSlot(
    val id: String,
    val dateIso: String,
    val startTimeIso: String,
    val endTimeIso: String,
    val type: SlotAvailabilityType,
    val label: String,
    val isUserSlot: Boolean = false,
    val isPartnerSlot: Boolean = false
)

@Serializable
data class SuggestedMeeting(
    val id: String,
    val dateIso: String,
    val startTimeIso: String,
    val endTimeIso: String,
    val displayTime: String,
    val sessionTitle: String,
    val suggestedByName: String,
    val suggestedByRole: UserRole,
    val status: MeetingStatus = MeetingStatus.PENDING,
    val meetingLink: String? = null
)

@Serializable
enum class MeetingStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}
