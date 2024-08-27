package com.example.app.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.navigation.AppNavHost
import com.example.app.navigation.AppScreen
import com.example.app.navigation.MAP_DETAIL_ROUTE
import com.example.app.ui.components.AppBar
import com.example.app.viewmodel.MapListViewModel

/**
 * Composable function that sets up the main application screen with navigation and app bar.
 *
 * @param viewModel The ViewModel that provides data and handles business logic for the map list and detail screens.
 * @param navController The NavHostController that handles navigation within the app. Defaults to a new instance created by [rememberNavController].
 */

@Composable
fun MapApp(
    viewModel: MapListViewModel,
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = when (backStackEntry?.destination?.route) {
        MAP_DETAIL_ROUTE -> AppScreen.MapDetail
        else -> AppScreen.MapList
    }

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) {
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            viewModel = viewModel
        )
    }
}


