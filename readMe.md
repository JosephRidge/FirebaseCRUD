## MVVM Architecture
![img.png](img.png)

Use this [repository](https://github.com/JosephRidge/MVVMSafari) to help.

## Application Architecture 
![img_1.png](img_1.png)

##  User Flow Diagram
![img_2.png](img_2.png)

# Data Section 
## Data Section file structure
![img_4.png](img_4.png)

## File Structure for Data:
![img_3.png](img_3.png)

## Task Model:
``` kotlin
data class Task(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
)
```
## Interface to be added inside the repository directory
- The interface is like a contract that states, you must implement the following methods, in this case it is to perform CRUD operations
``` Kotlin
interface TaskService {
    fun observeTasks(userId: String): Flow<List<Task>>   // Read (continuous)
    suspend fun getTask(taskId: String ): Task?            // Read (one-shot)
    suspend fun addTask(task: Task, localImagePaths: List<String>): Task                // Create
    suspend fun updateTask(task: Task, localImagePaths: List<String>) :Task            // Update
    suspend fun deleteTask(taskId: String)   // Delete
}
```

## FirestoreRepository
```Kotlin
class FirestoreRepository(
    private val firestore: FirebaseFirestore,
    private val cloudinaryImageUpload: CloudinaryImageUpload

) : TaskService {
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
                val tasks =
                    snapshot?.documents?.mapNotNull { it.toObject(Task::class.java) } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    /*
    * Uploads any local image files first, then attaches the resulting
    * Cloudinary URLs to imageUrls before writing the task to Firestore.
    * Uploading before the Firestore write means we never save a task
    * that references images that failed to upload.
    */
    override suspend fun addTask(task: Task, localImagePaths: List<String>): Task {
        val docRef = tasksRef.document()
        val uploadedUrls = if (localImagePaths.isNotEmpty()) {
            cloudinaryImageUpload.uploadImages(localImagePaths)
        } else {
            emptyList()
        }
        val withId = task.copy(
            id = docRef.id,
            imageUrls = task.imageUrls + uploadedUrls
        )
        docRef.set(withId).await()
        return withId
    }

    /*
    * Uploads any new local images and appends their URLs to the task's
    * existing imageUrls, then overwrites the document. Also refreshes
    * updatedAt since this represents a modification.
    *
    * Note: this only adds images — it doesn't handle removing individual
    * images from imageUrls. That's a UI-level concern: if your edit screen
    * lets users remove an image, just pass in a Task whose imageUrls list
    * already has that entry stripped out, and this function will save it
    * as-is via .set().
    */
    override suspend fun updateTask(task: Task, localImagePaths: List<String>): Task {
        val uploadedUrls = if (localImagePaths.isNotEmpty()) {
            cloudinaryImageUpload.uploadImages(localImagePaths)
        } else {
            emptyList()
        }
        val updatedTask = task.copy(
            imageUrls = task.imageUrls + uploadedUrls,
            updatedAt = System.currentTimeMillis()
        )
        tasksRef.document(updatedTask.id).set(updatedTask).await()
        return updatedTask
    }

    override suspend fun getTask(taskId: String): Task? =
        tasksRef.document(taskId).get().await().toObject(Task::class.java)

    /*
    * Deletes the Firestore document only. The images referenced in
    * imageUrls are left behind on Cloudinary — deleting them requires
    * your api_secret via a signed backend/Admin API call, which can't
    * safely run on-device. A production app would send task.imageUrls
    * to a backend endpoint here to trigger cleanup.
    */
    override suspend fun deleteTask(taskId: String) {
        tasksRef.document(taskId).delete().await()
    }
}
```

