package com.jayr.firecrud.data.repository

import android.system.Os.close
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jayr.firecrud.data.models.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class FirestoreRepository(
    private val firestore: FirebaseFirestore

): TaskService {
    private val tasksRef get() = firestore.collection("tasks")

    override fun observeTasks(userId: String): Flow<List<Task>> {
        val listener = tasksRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val tasks = snapshot?.documents?.mapNotNull { it.toObject(Task::class.java) } ?: emptyList()
//                trySend(tasks)
            }
        awaitClose { listener.remove() }    }

    override suspend fun getTask(taskId: String): Task? {
        return tasksRef.document(taskId).get().await().toObject(Task::class.java)
    }

    override suspend fun addTask(task: Task): Task {
        val docRef = tasksRef.document()
        val withId = task.copy(id = docRef.id)
        docRef.set(withId).await()
        return withId    }

    override suspend fun updateTask(task: Task) {
          tasksRef.document(task.id).set(task).await()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksRef.document(taskId).delete().await()    }
}