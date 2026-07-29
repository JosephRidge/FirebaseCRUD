package com.jayr.firecrud.ui.navigation
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
object About

@Serializable
object Login

@Serializable
data class TaskForm(
    val taskId: String? = null
)

@Serializable
data class TaskDetail(
    val taskId: String
)