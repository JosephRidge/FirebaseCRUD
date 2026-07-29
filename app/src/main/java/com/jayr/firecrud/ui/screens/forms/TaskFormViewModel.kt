package com.jayr.firecrud.ui.screens.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.firestore.FirestoreRepository
import com.jayr.firecrud.ui.screens.uiState.TaskUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val firestoreRepository: FirestoreRepository
): ViewModel() {

//     states
    private var _uiState: MutableStateFlow<TaskUIState> = MutableStateFlow(TaskUIState.isIdle)
    val uiState = _uiState.asStateFlow()
    private var _responseMessage: MutableStateFlow<String> = MutableStateFlow("")
    val responseMessage = _responseMessage.asStateFlow()


//    methods (just functions in classes)
fun createTask(task: Task, localImagePaths: List<String>) {
    viewModelScope.launch {
        _uiState.value = TaskUIState.isLoading
        try {
            firestoreRepository.addTask(task, localImagePaths)
            _uiState.value = TaskUIState.isSuccess
            _responseMessage.value = "Task created successfully."
        } catch (e: Exception) {
            _uiState.value = TaskUIState.isError
            _responseMessage.value = e.message.toString()
        }
    }
}

    fun updateTask(task: Task, localImagePaths: List<String>) {
        viewModelScope.launch {
            _uiState.value = TaskUIState.isLoading
            try {
                firestoreRepository.updateTask(task, localImagePaths)
                _uiState.value = TaskUIState.isSuccess
                _responseMessage.value = "Task updated successfully."
            } catch (e: Exception) {
                _uiState.value = TaskUIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
    }
}