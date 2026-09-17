package org.globalmentorship.portal.platform

import kotlinx.datetime.Clock
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class PlatformSecureStorage {
    private val memoryStore = mutableMapOf<String, String>()

    actual fun saveToken(key: String, value: String) {
        memoryStore[key] = value
    }

    actual fun getToken(key: String): String? {
        return memoryStore[key]
    }

    actual fun clearToken(key: String) {
        memoryStore.remove(key)
    }
}

actual class PlatformBiometrics {
    actual suspend fun canAuthenticate(): Boolean = true
    actual suspend fun authenticate(title: String, subtitle: String): Boolean = true
}

actual class PlatformFilePicker {
    actual suspend fun pickDocument(allowedExtensions: List<String>): PickedFileResult? {
        return PickedFileResult(
            fileName = "Resume.pdf",
            mimeType = "application/pdf",
            sizeBytes = 1024 * 1024,
            uriOrPath = "file:///documents/Resume.pdf"
        )
    }
}

actual class PlatformImagePicker {
    actual suspend fun pickImage(): PickedImageResult? {
        return PickedImageResult(
            fileName = "avatar.png",
            mimeType = "image/png",
            sizeBytes = 1024L,
            bytes = ByteArray(1024)
        )
    }
}

actual class PlatformPdfViewer {
    actual fun openPdf(url: String, title: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}

actual class PlatformCalendarExporter {
    actual fun exportMeetingToCalendar(
        title: String,
        startTimeIso: String,
        endTimeIso: String,
        description: String,
        locationOrUrl: String?
    ): Boolean = true
}

actual class PlatformPushHandler {
    actual suspend fun getDevicePushToken(): String? = "ios_apns_mock_token_${Clock.System.now().toEpochMilliseconds()}"
}

actual class PlatformShareSheet {
    actual fun shareText(text: String, title: String?) {}

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }

    actual fun openMail(email: String, subject: String?) {
        val urlString = if (subject != null) "mailto:$email?subject=$subject" else "mailto:$email"
        val nsUrl = NSURL.URLWithString(urlString) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}
