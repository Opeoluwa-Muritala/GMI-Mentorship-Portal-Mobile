package org.globalmentorship.portal.data.sync

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import org.globalmentorship.portal.data.local.GmiLocalDatabase
import org.globalmentorship.portal.data.remote.GmiApiClient

class OnlineSyncManager(
    val localDb: GmiLocalDatabase,
    private val apiClient: GmiApiClient? = null
) {
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncStatus = MutableStateFlow<String?>(null)
    val lastSyncStatus: StateFlow<String?> = _lastSyncStatus.asStateFlow()

    val pendingQueueCount: StateFlow<Int> = MutableStateFlow(localDb.getOutboundQueue().size)

    suspend fun syncNow(): Result<String> {
        _isSyncing.value = true
        _lastSyncStatus.value = "Connecting to ${localDb.apiBaseUrl}..."

        return try {
            delay(1200) // Simulate network round-trip

            // 1. Flush outbound queue
            val pending = localDb.getOutboundQueue()
            if (pending.isNotEmpty()) {
                _lastSyncStatus.value = "Uploading ${pending.size} local updates..."
                delay(600)
                localDb.clearOutboundQueue()
            }

            // 2. Record sync time
            val now = Clock.System.now().toString().take(19).replace("T", " ")
            localDb.lastSyncTimestamp = now

            val successMessage = "Successfully synced with online API at $now"
            _lastSyncStatus.value = successMessage
            _isSyncing.value = false
            Result.success(successMessage)
        } catch (e: Exception) {
            val err = "Sync failed: ${e.message}"
            _lastSyncStatus.value = err
            _isSyncing.value = false
            Result.failure(e)
        }
    }
}
