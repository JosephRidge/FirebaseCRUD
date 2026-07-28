## MVVM Architecture
![img.png](img.png)

Use this [repository](https://github.com/JosephRidge/MVVMSafari) to help.

## Application Architecture 
![img_1.png](img_1.png)

##  User Flow Diagram
![img_2.png](img_2.png)


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
### Interface to be added inside the repository directory
- The interface is like a contract that state that you must implement the following methods, in this case it is to perform CRUD operations
``` Kotlin
interface TaskService {
    fun observeTasks(userId: String): Flow<List<Task>>
    suspend fun getTask(taskId: String): Task?
    suspend fun addTask(task: Task, imageBytesList: List<ByteArray>): Result<Task>
    suspend fun updateTask(task: Task, newImageBytesList: List<ByteArray>): Result<Task>
    suspend fun deleteTask(taskId: String): Result<Unit>
}
```
