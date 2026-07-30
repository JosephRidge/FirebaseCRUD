package com.jayr.firecrud.ui.screens.forms

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.firestore.FirestoreRepository
import com.jayr.firecrud.ui.screens.uiState.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import androidx.core.net.toUri

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


    // read bytes
    private fun readBytesFromPath(path: String, context: Context): ByteArray {
        val uri = path.toUri()
        return if (uri.scheme == "content" || uri.scheme == "file") {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("Could not open input stream for $path")
        } else {
            File(path).readBytes()
        }
    }

    fun createTask(
        task: Task,
        localImagePaths: List<String>,
//        readBytes: (String?) -> ByteArray,
        context: Context
    )
    {
        viewModelScope.launch {
            _uiState.value = UIState.isLoading
            try {
                firestoreRepository.addTask(
                    task,
                    localImagePaths,
                    readBytesFromPath()
                )
                _uiState.value = UIState.isSuccess
                _responseMessage.value = "Task created successfully."
            } catch (e: Exception) {
                _uiState.value = UIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
}

    fun updateTask(task: Task, localImagePaths: List<String>,readBytes: (String?) -> ByteArray) {
        viewModelScope.launch {
            _uiState.value = UIState.isLoading
            try {
                firestoreRepository.updateTask(task, localImagePaths, readBytes )
                _uiState.value = UIState.isSuccess
                _responseMessage.value = "Task updated successfully."
            } catch (e: Exception) {
                _uiState.value = UIState.isError
                _responseMessage.value = e.message.toString()
            }
        }
    }
}