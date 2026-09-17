package org.globalmentorship.portal.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    STUDENT,
    MENTOR
}

@Serializable
data class PartnerInfo(
    val id: String,
    val name: String,
    val titleOrMajor: String,
    val organizationOrUniversity: String,
    val email: String,
    val avatarUrl: String? = null,
    val role: UserRole
)

@Serializable
data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val timezone: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val partner: PartnerInfo? = null,
    val biometricEnabled: Boolean = false
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val initials: String get() = "${firstName.take(1)}${lastName.take(1)}".uppercase()
}
