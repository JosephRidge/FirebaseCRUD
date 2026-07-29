package com.jayr.firecrud.ui.screens.uiState

sealed class UIState {
    object isIdle : UIState()
    object isLoading : UIState()
    object isSuccess : UIState()
    object isError : UIState()
}