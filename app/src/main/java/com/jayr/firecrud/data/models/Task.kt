package com.jayr.firecrud.data.models

/*
* Note userId:Every task must be tied to the authenticated user,
*  both for querying ("show me only my tasks") and for
*  Firestore security rules
* */
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