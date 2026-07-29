package com.jayr.firecrud.ui.screens.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.firestore.FirestoreRepository
import com.jayr.firecrud.ui.screens.uiState.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val firestoreRepository: FirestoreRepository
): ViewModel() {

//     states
    private var _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.isIdle)
    val uiState = _uiState.asStateFlow()
    private var _responseMessage: MutableStateFlow<String> = MutableStateFlow("")
    val responseMessage = _responseMessage.asStateFlow()
    private var _existingTask: MutableStateFlow<Task?> = MutableStateFlow(null)
    val existingTask = _existingTask.asStateFlow()

//    methods (just functions in classes)
    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.value = UIState.isLoading
            try {
                _existingTask.value = firestoreRepository.getTask(taskId)
                _uiState.value = UIState.isSuccess
            } catch (e: Exception) {
                _uiState.value = UIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
    }

    fun createTask(task: Task, localImagePaths: List<String>) {
    viewModelScope.launch {
        _uiState.value = UIState.isLoading
        try {
            firestoreRepository.addTask(
                task, localImagePaths,
            )
            _uiState.value = UIState.isSuccess
            _responseMessage.value = "Task created successfully."
        } catch (e: Exception) {
            _uiState.value = UIState.isError
            _responseMessage.value = e.message.toString()
        }
    }
}

    fun updateTask(task: Task, localImagePaths: List<String>) {
        viewModelScope.launch {
            _uiState.value = UIState.isLoading
            try {
                firestoreRepository.updateTask(task, localImagePaths)
                _uiState.value = UIState.isSuccess
                _responseMessage.value = "Task updated successfully."
            } catch (e: Exception) {
                _uiState.value = UIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
    }
}