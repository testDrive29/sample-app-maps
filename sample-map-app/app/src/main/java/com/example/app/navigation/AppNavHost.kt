package com.example.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app.screens.MapDetailScreen
import com.example.app.screens.MapListScreen
import com.example.app.viewmodel.MapListViewModel

const val MAP_LIST_ROUTE = "map_list_route"
const val DOWNLOAD_PATH_ARG = "downloadPath"
const val MAP_DETAIL_ROUTE_BASE = "map_detail_route"
const val MAP_DETAIL_ROUTE = "$MAP_DETAIL_ROUTE_BASE?$DOWNLOAD_PATH_ARG={$DOWNLOAD_PATH_ARG}"

/**
 * Composable function that sets up the navigation host for the application.
 *
 * @param modifier The modifier to be applied to the NavHost.
 * @param navController The NavHostController that handles navigation within the app.
 * @param startDestination The starting destination route for the NavHost. Defaults to [MAP_LIST_ROUTE].
 * @param viewModel The ViewModel that provides data and handles business logic for the map list and detail screens.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = MAP_LIST_ROUTE,
    viewModel: MapListViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MAP_LIST_ROUTE) {
            MapListScreen(viewModel) { downloadPath ->
                val route = if (downloadPath != null) {
                    // Encode the downloadPath to ensure it is properly formatted for use in the navigation route URL
                    "$MAP_DETAIL_ROUTE_BASE?$DOWNLOAD_PATH_ARG=${Uri.encode(downloadPath)}"
                } else {
                    MAP_DETAIL_ROUTE_BASE
                }
                navController.navigate(route)
            }
        }
        composable(
            route = MAP_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(DOWNLOAD_PATH_ARG) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val downloadPath = backStackEntry.arguments?.getString(DOWNLOAD_PATH_ARG)
            MapDetailScreen(viewModel, modifier, downloadPath)
        }
    }
}