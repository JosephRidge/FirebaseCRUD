package com.jayr.firecrud.ui.screens.forms

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jayr.firecrud.data.models.Task

@Composable
fun TaskForm(
    taskId: String? = null,
    taskFormViewModel: TaskFormViewModel = viewModel()
) {
//   states
    val titleState = rememberTextFieldState("")
    val descriptionState = rememberTextFieldState("")
    /*
    * Kindly note:
        For 'by' in:
           var localImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }    val pickImagesLauncher = rememberLauncherForActivityResult(
             contract = ActivityResultContracts.PickMultipleVisualMedia()
            ) { uris ->
                localImagePaths = uris.map { it.toString() }
            }
     you need to import:
        import androidx.compose.runtime.getValue
        import androidx.compose.runtime.setValue
     */
    var localImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        localImagePaths = uris.map { it.toString() }
    }
    val existingTask by taskFormViewModel.existingTask.collectAsState()

    LaunchedEffect(taskId) {
        if (taskId != null) {
            taskFormViewModel.loadTask(taskId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            state = titleState,
            label = { Text("Title") }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            state = descriptionState,
            label = { Text("Description") }
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            pickImagesLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) { Text("Add Images") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val task = (existingTask ?: Task()).copy(
                title = titleState.text.toString(),
                description = descriptionState.text.toString()
            )
            taskFormViewModel.createTask(task, localImagePaths)
        }) { Text("Save Task") }
    }
}