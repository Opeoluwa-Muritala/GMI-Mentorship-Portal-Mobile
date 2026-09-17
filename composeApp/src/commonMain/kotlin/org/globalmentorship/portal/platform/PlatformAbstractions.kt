package org.globalmentorship.portal.platform

/**
 * Platform abstraction inventory for GMI Mentorship Portal:
 * 1. PlatformSecureStorage: Secure key-value storage (Android EncryptedSharedPreferences, iOS Keychain).
 * 2. PlatformBiometrics: Native biometric authentication prompt (Android BiometricPrompt, iOS LocalAuthentication).
 * 3. PlatformCaptcha: Web-based CAPTCHA challenge resolver.
 * 4. PlatformFilePicker: Document picker for PDF, DOC, DOCX up to 10MB.
 * 5. PlatformImagePicker: Photo picker supporting JPEG, PNG, GIF, WebP up to 500KB.
 * 6. PlatformPdfViewer: In-app PDF sheet viewer with offline caching.
 * 7. PlatformCalendarExporter: System calendar event addition for confirmed meetings.
 * 8. PlatformPushHandler: APNs / FCM token retrieval and push notification handlers.
 * 9. PlatformShareSheet: Native system share sheet for sharing links/resources.
 * 10. PlatformBrowser: Launches mailto: and external URLs in system browser.
 */

expect class PlatformSecureStorage {
    fun saveToken(key: String, value: String)
    fun getToken(key: String): String?
    fun clearToken(key: String)
}

expect class PlatformBiometrics {
    suspend fun canAuthenticate(): Boolean
    suspend fun authenticate(title: String, subtitle: String): Boolean
}

expect class PlatformFilePicker {
    suspend fun pickDocument(allowedExtensions: List<String>): PickedFileResult?
}

data class PickedFileResult(
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val uriOrPath: String
)

expect class PlatformImagePicker {
    suspend fun pickImage(): PickedImageResult?
}

data class PickedImageResult(
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val bytes: ByteArray
)

expect class PlatformPdfViewer {
    fun openPdf(url: String, title: String)
}

expect class PlatformCalendarExporter {
    fun exportMeetingToCalendar(
        title: String,
        startTimeIso: String,
        endTimeIso: String,
        description: String,
        locationOrUrl: String?
    ): Boolean
}

expect class PlatformPushHandler {
    suspend fun getDevicePushToken(): String?
}

expect class PlatformShareSheet {
    fun shareText(text: String, title: String? = null)
    fun openUrl(url: String)
    fun openMail(email: String, subject: String? = null)
}
