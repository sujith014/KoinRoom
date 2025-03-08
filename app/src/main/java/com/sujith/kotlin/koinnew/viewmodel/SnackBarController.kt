package com.sujith.kotlin.koinnew.viewmodel

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackBarEvent(
    val message: String,
    val action: SnackBarAction? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val withDismissAction : Boolean = false,
    val backgroundColor: Color = Color.White, // Snackbar BG Color
    val actionButtonColor: Color = Color.Yellow, // Action Button BG Color
    val dismissButtonColor: Color = Color.Red // Dismiss Button BG Color
)


data class SnackBarAction(
    val label: String?,
    val action: suspend () -> Unit = { },
    val dismissAction: suspend () -> Unit = { },
)

object SnackBarController {
    private val _events = MutableSharedFlow<SnackBarEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun sendEvent(event: SnackBarEvent) {
        _events.emit(event)
    }
}