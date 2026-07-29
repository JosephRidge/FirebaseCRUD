package com.jayr.firecrud.ui.screens.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUIstate {
    object isLoading : AuthUIstate()
    object isIdle : AuthUIstate()
    object isSuccess : AuthUIstate()
    object isFailed : AuthUIstate()
}
/*
class AuthenticationViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    //    states & backing property
    private var _uiState: MutableStateFlow< AuthUIstate> =
        MutableStateFlow(
            AuthUIstate.isIdle
        )
    val uiState = _uiState.asStateFlow()

    private var _responseMessage: MutableStateFlow<String> = MutableStateFlow("")
    val responseMessage = _responseMessage.asStateFlow()


    //     methods
    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value =
                AuthUIstate.isLoading
            try {
                authRepository.register(email = email, password = password)
                _uiState.value =
                    AuthUIstate.isSuccess
                _responseMessage.value = "Welcome to AuthyAuthKE!"
            } catch (e: Exception) {
                _uiState.value =
                    AuthUIstate.isFailed
                _responseMessage.value = "OopS! ${e.message}"
            }

        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value =
                AuthUIstate.isLoading
            try {
                authRepository.login(email = email, password = password)
                _uiState.value =
                    AuthUIstate.isSuccess
                _responseMessage.value = "Welcome back to AuthyAuthKE!"
            } catch (e: Exception) {
                _uiState.value =
                    AuthUIstate.isFailed
                _responseMessage.value = "OopS! ${e.message}"
            }

        }
    }
    fun sendPasswordResetToUser(email: String) {
        viewModelScope.launch {
            _uiState.value =
                AuthUIstate.isLoading
            try {
                authRepository.forgotPassword(email = email)
                _uiState.value =
                    AuthUIstate.isSuccess
                _responseMessage.value = "Kindly check your email! especially SPAM"
            } catch (e: Exception) {
                _uiState.value =
                    AuthUIstate.isFailed
                _responseMessage.value = "OopS! ${e.message}"
            }

        }
    }


}*/