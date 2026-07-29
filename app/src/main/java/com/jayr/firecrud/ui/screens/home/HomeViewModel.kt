package com.jayr.firecrud.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.cloudinary.CloudinaryImageUpload
import com.jayr.firecrud.data.repository.firestore.FirestoreRepository
import com.jayr.firecrud.ui.screens.uiState.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val firebaseRepository: FirestoreRepository  = FirestoreRepository(
        CloudinaryImageUpload()
    )
): ViewModel() {
    private var _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.isIdle)
    val uiState = _uiState.asStateFlow()
    private var _tasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasks = _tasks.asStateFlow()
    private var _responseMessage: MutableStateFlow<String> = MutableStateFlow("")
    val responseMessage = _responseMessage.asStateFlow()

//     load tasks
    fun loadTasks(userId: String) {
        viewModelScope.launch {
            _uiState.value = UIState.isLoading
            try {
                firebaseRepository.observeTasks(userId).collect { taskList ->
                    _tasks.value = taskList
                    _uiState.value = UIState.isSuccess
                }
            } catch (e: Exception) {
                _uiState.value = UIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
    }
}
