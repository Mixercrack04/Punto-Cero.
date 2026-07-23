package com.example.data.web

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WebSyncStatus(
    val isSyncing: Boolean = false,
    val lastSyncedTimestamp: Long = System.currentTimeMillis() - 120000,
    val syncedReportsCount: Int = 14,
    val syncedSurveysCount: Int = 28,
    val schoolProjectWebUrl: String = "https://mosquialerta.colegio.edu/proyecto-zancudos",
    val syncCode: String = "COLEGIO-2026-MOSQUI-998",
    val statusMessage: String = "Sincronizado con el Portal Web Escolar"
)

object WebSyncSimulator {
    private val _syncState = MutableStateFlow(WebSyncStatus())
    val syncState: StateFlow<WebSyncStatus> = _syncState

    suspend fun performSyncNow() {
        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            statusMessage = "Conectando con la página web del colegio..."
        )
        delay(1200)
        _syncState.value = _syncState.value.copy(
            statusMessage = "Enviando reportes comunitarios y encuestas de concientización..."
        )
        delay(1000)
        _syncState.value = _syncState.value.copy(
            isSyncing = false,
            lastSyncedTimestamp = System.currentTimeMillis(),
            syncedReportsCount = _syncState.value.syncedReportsCount + 1,
            syncedSurveysCount = _syncState.value.syncedSurveysCount + 1,
            statusMessage = "¡Sincronización exitosa! Todos los datos están actualizados en la Web."
        )
    }
}
