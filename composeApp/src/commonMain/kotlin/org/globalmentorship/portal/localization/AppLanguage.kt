package org.globalmentorship.portal.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français")
}

object LocalizationManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }
}

val LocalStrings = staticCompositionLocalOf { AppStrings.english }

@Composable
fun ProvideLocalizedStrings(
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit
) {
    val strings = when (language) {
        AppLanguage.ENGLISH -> AppStrings.english
        AppLanguage.SPANISH -> AppStrings.spanish
        AppLanguage.FRENCH -> AppStrings.french
    }
    CompositionLocalProvider(LocalStrings provides strings) {
        content()
    }
}
