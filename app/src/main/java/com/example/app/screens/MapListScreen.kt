package com.example.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arcgismaps.tasks.offlinemaptask.PreplannedMapArea
import com.example.app.R
import com.example.app.data.MapItemData
import com.example.app.data.WebMapItemData
import com.example.app.data.OfflineMapItemData
import com.example.app.ui.components.LabelText
import com.example.app.ui.components.MapItem
import com.example.app.ui.components.MapItemWithButton
import com.example.app.viewmodel.MapListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Composable function that displays a list of maps, including web map and preplanned offline maps.
 *
 * @param viewModel The ViewModel that provides data and handles business logic for the map list.
 * @param onWebMapClick Callback function to be invoked when a web map item is clicked. The download path of the offline map, if it exists, is passed as a parameter.
 */
@Composable
fun MapListScreen(viewModel: MapListViewModel, onWebMapClick: (downloadPath: String?) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    MapList(
        viewModel.webMapItemData.value,
        viewModel.preplannedMapAreasList,
        onWebMapClick = { mapItemData ->
            onWebMapClick(viewModel.getDownloadPath(mapItemData))
        },
        onDeleteButtonClick = { downloadPath ->
            viewModel.deleteDownloadedMap(downloadPath)
        },
    ) { mapArea ->
        viewModel.downloadOfflineMapArea(
            mapArea = mapArea,
            onDownloadSuccess = {
                coroutineScope.launch {
                    showToast(context, "Map downloaded successfully")
                }
            },
            onDownloadFailure = {
                coroutineScope.launch {
                    showToast(context, "Error downloading map")
                }
            }
        )
    }
}

/**
 * Composable function that displays a list of map items, including web map and preplanned offline maps.
 *
 * @param webMap The web map item data to be displayed.
 * @param preplannedMapList The list of preplanned offline map items to be displayed.
 * @param onWebMapClick Callback function to be invoked when a web map item is clicked.
 * @param onDeleteButtonClick Callback function to be invoked when the delete button is clicked for a downloaded preplanned map item.
 * @param onDownloadButtonClick Callback function to be invoked when the download button is clicked for a preplanned map area.
 */
@Composable
fun MapList(
    webMap: WebMapItemData?,
    preplannedMapList: List<OfflineMapItemData>,
    onWebMapClick: (MapItemData) -> Unit,
    onDeleteButtonClick: (String) -> Unit,
    onDownloadButtonClick: (PreplannedMapArea) -> Unit
) {
    Column(modifier = Modifier.padding(4.dp)) {
        if (webMap != null && webMap.title.isNotEmpty()) {
            LabelText(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                text = stringResource(id = R.string.web_map_header)
            )
            MapItem(
                title = webMap.title,
                description = webMap.description,
                thumbnail = webMap.thumbnailUri,
                onClick = { onWebMapClick(webMap) }
            )
            HorizontalDivider()
        }

        LabelText(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
            text = stringResource(id = R.string.preplanned_map_header)
        )
        LazyColumn {
            items(preplannedMapList) { mapData ->
                MapItemWithButton(
                    mapItemData = mapData,
                    onPreplannedMapAreaClick = { mapItemClicked -> onWebMapClick(mapItemClicked) },
                    onDeleteButtonClick = { downloadPath ->
                        onDeleteButtonClick(downloadPath)
                    },
                    onDownloadButtonClick = {
                        onDownloadButtonClick(mapData.preplannedMapArea)
                    }
                )
            }
        }
    }
}

suspend fun showToast(context: Context, text: String) = withContext(Dispatchers.Main) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

