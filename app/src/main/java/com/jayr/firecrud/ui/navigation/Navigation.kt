package com.jayr.firecrud.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jayr.firecrud.ui.screens.forms.TaskFormScreen 
import com.jayr.firecrud.ui.screens.home.HomeScreen
import com.jayr.firecrud.ui.screens.taskDetail.TaskDetailScreen
import kotlin.toString

@Composable
fun Navigation(
    modifier: Modifier,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                navController = navHostController
            )
        }
        composable<TaskDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<TaskDetail>()
            TaskDetailScreen(
                taskId = args.taskId,
                navController = navHostController,
            )
        }

        composable<TaskForm> { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskFormScreen(
                taskId = taskId
            )
        }
    }
}