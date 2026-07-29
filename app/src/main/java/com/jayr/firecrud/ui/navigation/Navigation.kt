package com.jayr.firecrud.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jayr.firecrud.ui.screens.home.HomeScreen

@Composable
fun Navigation(
    modifier: Modifier,
    navHostController: NavHostController
){

    NavHost(
        navController =  navHostController,
        startDestination = Home
    ){
        composable<Home>{ HomeScreen() }
        composable<About>{  }
        composable<Login>{ }
    }

}