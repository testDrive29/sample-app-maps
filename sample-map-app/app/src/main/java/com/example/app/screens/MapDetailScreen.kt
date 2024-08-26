package com.example.app.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.app.viewmodel.MapListViewModel

/**
 * Composable function that displays the map detail screen.
 *
 * @param viewModel The ViewModel that provides data and handles business logic for this screen.
 * @param modifier The modifier to apply to this layout.
 * @param downloadPath The path to the downloaded map on the device. If null, web map is shown.
 */
@Composable
fun MapDetailScreen(
    viewModel: MapListViewModel,
    modifier: Modifier,
    downloadPath: String?
) {
    LaunchedEffect(Unit) {
        if (downloadPath != null) {
            viewModel.createDetailMapFromDownloadPath(downloadPath)
        } else {
            viewModel.createDetailWebMap()
        }
    }
    MapView(
        modifier = modifier,
        arcGISMap = viewModel.arcGISMapForDetailScreen.value ?: return
    )
}
