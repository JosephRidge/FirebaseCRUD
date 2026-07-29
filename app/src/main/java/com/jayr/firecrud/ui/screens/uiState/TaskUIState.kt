package com.jayr.firecrud.ui.screens.uiState

sealed class TaskUIState {
    object isIdle : TaskUIState()
    object isLoading : TaskUIState()
    object isSuccess : TaskUIState()
    object isError : TaskUIState()
}