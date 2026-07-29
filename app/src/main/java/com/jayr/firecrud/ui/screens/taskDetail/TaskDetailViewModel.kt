package com.jayr.firecrud.ui.screens.taskDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.firestore.FirestoreRepository
import com.jayr.firecrud.ui.screens.uiState.TaskUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
        private val firestoreRepository: FirestoreRepository
    ) : ViewModel() {

        // UI State
        private val _uiState = MutableStateFlow<TaskUIState>(TaskUIState.isIdle)
        val uiState = _uiState.asStateFlow()

        // Selected Task
        private val _task = MutableStateFlow<Task?>(null)
        val task = _task.asStateFlow()

        // Response Message
        private val _responseMessage = MutableStateFlow("")
        val responseMessage = _responseMessage.asStateFlow()

        /**
         * Loads a single task from Firestore.
         */
        fun loadTask(taskId: String) {
            viewModelScope.launch {
                _uiState.value = TaskUIState.isLoading

                try {
                    val result = firestoreRepository.getTask(taskId)

                    if (result != null) {
                        _task.value = result
                        _uiState.value = TaskUIState.isSuccess
                    } else {
                        _uiState.value = TaskUIState.isError
                        _responseMessage.value = "Task not found."
                    }

                } catch (e: Exception) {
                    _uiState.value = TaskUIState.isError
                    _responseMessage.value = e.message ?: "Something went wrong."
                }
            }
        }
    }