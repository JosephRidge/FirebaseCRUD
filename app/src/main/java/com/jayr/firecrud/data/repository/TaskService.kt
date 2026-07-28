package com.jayr.firecrud.data.repository

import com.jayr.firecrud.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskService {
    fun observeTasks(userId: String): Flow<List<Task>>   // Read (continuous)
    suspend fun getTask(taskId: String): Task?            // Read (one-shot)
    suspend fun addTask(task:Task): Result<Task>                // Create
    suspend fun updateTask(task:Task): Result<Task>              // Update
    suspend fun deleteTask(taskId: String): Result<Unit>  // Delete
}