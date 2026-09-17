package org.globalmentorship.portal.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract

actual class PlatformSecureStorage(private val context: Context) {
    private val prefs = context.getSharedPreferences("gmi_secure_store", Context.MODE_PRIVATE)

    actual fun saveToken(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getToken(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun clearToken(key: String) {
        prefs.edit().remove(key).apply()
    }
}

actual class PlatformBiometrics(private val context: Context) {
    actual suspend fun canAuthenticate(): Boolean = true
    actual suspend fun authenticate(title: String, subtitle: String): Boolean = true
}

actual class PlatformFilePicker(private val context: Context) {
    actual suspend fun pickDocument(allowedExtensions: List<String>): PickedFileResult? {
        return PickedFileResult(
            fileName = "Document.pdf",
            mimeType = "application/pdf",
            sizeBytes = 1024 * 1024,
            uriOrPath = "content://media/document.pdf"
        )
    }
}

actual class PlatformImagePicker(private val context: Context) {
    actual suspend fun pickImage(): PickedImageResult? {
        val dummyBytes = ByteArray(1024)
        return PickedImageResult(
            fileName = "profile.png",
            mimeType = "image/png",
            sizeBytes = 1024L,
            bytes = dummyBytes
        )
    }
}

actual class PlatformPdfViewer(private val context: Context) {
    actual fun openPdf(url: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}

actual class PlatformCalendarExporter(private val context: Context) {
    actual fun exportMeetingToCalendar(
        title: String,
        startTimeIso: String,
        endTimeIso: String,
        description: String,
        locationOrUrl: String?
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, locationOrUrl ?: "")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}

actual class PlatformPushHandler(private val context: Context) {
    actual suspend fun getDevicePushToken(): String? = "android_mock_fcm_token_${System.currentTimeMillis()}"
}

actual class PlatformShareSheet(private val context: Context) {
    actual fun shareText(text: String, title: String?) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, title ?: "Share via")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    actual fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    actual fun openMail(email: String, subject: String?) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                if (subject != null) putExtra(Intent.EXTRA_SUBJECT, subject)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
