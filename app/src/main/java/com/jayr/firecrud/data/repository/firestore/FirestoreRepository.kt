package com.jayr.firecrud.data.repository.firestore

import android.system.Os
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.data.repository.cloudinary.CloudinaryImageUpload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val firestore: FirebaseFirestore,
    private val cloudinaryImageUpload: CloudinaryImageUpload

): TaskService {
    private val tasksRef get() = firestore.collection("tasks")
    /*
    * Note:
    * Flow<List<Task>>: think of a Flow as a stream of data over time, rather than a single value returned once. Every time the underlying data changes, a new list gets pushed out through the stream.
    callbackFlow { ... }: Firestore's real-time listener (addSnapshotListener) works with old-style callbacks, not Kotlin coroutines. callbackFlow is a bridge that lets us wrap that callback-based API and turn it into a proper Flow.
    whereEqualTo / orderBy: this builds our query: only fetch tasks belonging to userId, sorted newest-first by createdAt.
    addSnapshotListener: this is Firestore's way of saying "watch this query, and call me back every time something changes" (a task added, edited, deleted, etc.), not just once.
    trySend(tasks): every time the listener fires with new data, we push the updated task list into the Flow so anyone collecting it (like your ViewModel or UI) gets the fresh list automatically.
    close(error): if Firestore reports an error, we shut down the flow and pass the error along, instead of silently failing.
    awaitClose { listener.remove() }: this is the cleanup step. It only runs when whoever is collecting the flow stops listening (e.g., the screen is closed). At that point we detach the Firestore listener so it's not running forever in the background, wasting reads and battery.
    *
    * */
    override fun observeTasks(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = tasksRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull { it.toObject(Task::class.java) } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    /*
    * suspend fun: this is a one-time operation. Unlike observeTasks, it doesn't keep listening; it fetches the data once and hands it back.
    * tasksRef.document(taskId): points to one specific document in Firestore, identified by its ID.
    * .get(): asks Firestore for that document's current data, just once.
    * .await(): Firestore's .get() normally returns a Task (the Google/Firebase kind, a Task object representing "work in progress" — confusingly the same name as your app's Task model, just from a different library). .await() is a Kotlin coroutines extension that pauses here until that work finishes, then gives you the real result.
    * .toObject(Task::class.java): converts the raw Firestore document into your app's Task data class.
    * Return type Task?: nullable, because the document might not exist (wrong ID, already deleted), in which case this returns null.
    * */
    override suspend fun getTask(taskId: String): Task? {
        return tasksRef.document(taskId).get().await().toObject(Task::class.java)
    }

    /*
    * tasksRef.document(): with no ID passed in, Firestore auto-generates a new, unique document reference (and unique ID) for you.
    * task.copy(id = docRef.id): takes the incoming task and makes a copy with that generated ID attached, so the ID stored inside the document matches the ID Firestore uses to locate the document. This is important — it means later on you can just look at a Task object and know its own ID, without needing to separately track "which document did this come from?"
    * docRef.set(withId): writes the full task data to that new document.
    * .await(): waits for the write to finish before moving on.
    * Returns withId: gives the caller back the complete task, now including its assigned ID, so the UI can use it immediately (e.g., to navigate to a detail screen).
    *  */
    override suspend fun addTask(task: Task): Task {
        val docRef = tasksRef.document()
        val withId = task.copy(id = docRef.id)
        docRef.set(withId).await()
        return withId    }

    /*
    *.set(task): overwrites that document with the new data.
    * .set() replaces the entire document. If task is missing some field, that field gets wiped out.
    * If you only want to change one or two fields (like just isCompleted), the safer option is:
    * tasksRef.document(task.id).update(mapOf("isCompleted" to task.isCompleted)).await()
    * */
    override suspend fun updateTask(task: Task) {
          tasksRef.document(task.id).set(task).await()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksRef.document(taskId).delete().await()    }
}