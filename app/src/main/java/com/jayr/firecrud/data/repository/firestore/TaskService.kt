package com.jayr.firecrud.data.repository.firestore

import com.jayr.firecrud.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskService {
    fun observeTasks(userId: String): Flow<List<Task>>   // Read (continuous)
    suspend fun getTask(taskId: String): Task?            // Read (one-shot)
    suspend fun addTask(task: Task): Task                // Create
    suspend fun updateTask(task: Task)             // Update
    suspend fun deleteTask(taskId: String)   // Delete
}