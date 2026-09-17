package org.globalmentorship.portal.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Program : Screen("my-program")
    data class SessionDetail(val sessionNumber: Int) : Screen("my-program/session/$sessionNumber")
    data object Calendar : Screen("my-program/calendar")
    data object Messages : Screen("messages")
    data class MessageThread(val threadId: String) : Screen("messages/thread/$threadId")
    data object Notifications : Screen("notifications")
    data object Settings : Screen("account")
    data object Login : Screen("login")
}

enum class BottomBarTab(val route: String) {
    DASHBOARD("dashboard"),
    PROGRAM("my-program"),
    CALENDAR("my-program/calendar"),
    MESSAGES("messages"),
    NOTIFICATIONS("notifications")
}
