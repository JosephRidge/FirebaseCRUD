package com.jayr.firecrud.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jayr.firecrud.data.models.Task
import com.jayr.firecrud.ui.screens.uiState.TaskUIState

@Composable
fun HomeScreen(
    userId: String,
    homeViewModel: HomeViewModel = viewModel(),
    onTaskClick: (Task) -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val tasks by homeViewModel.tasks.collectAsState()
    val responseMessage by homeViewModel.responseMessage.collectAsState()

    LaunchedEffect(userId) {
        homeViewModel.loadTasks(userId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Tasks", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        when (uiState) {
            is TaskUIState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TaskUIState.isError -> {
                Text(
                    text = responseMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                if (tasks.isEmpty()) {
                    Text("No tasks yet. Tap + to add one.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks, key = { it.id }) { task ->
                            TaskListItem(task = task, onClick = { onTaskClick(task) })
                        }
                    }
                }
            }
        }
    }
}

//component for each task
@Composable
fun TaskListItem(task: Task, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(task.title, style = MaterialTheme.typography.titleMedium)
            if (task.description.isNotBlank()) {
                Text(
                    task.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}