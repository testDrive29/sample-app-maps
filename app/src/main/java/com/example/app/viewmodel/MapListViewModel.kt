package com.example.app.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.PortalItem
import com.arcgismaps.portal.Portal
import com.arcgismaps.tasks.offlinemaptask.OfflineMapTask
import com.arcgismaps.tasks.offlinemaptask.PreplannedMapArea
import com.example.app.data.MapDataStore
import com.example.app.data.MapItemData
import com.example.app.data.MapItemState
import com.example.app.data.WebMapItemData
import com.example.app.data.OfflineMapItemData
import com.example.app.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "MapListViewModel"

/**
 * ViewModel for managing the list of maps, including web map and preplanned map areas.
 */
class MapListViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val portal = Portal(
        url = "https://www.arcgis.com",
        connection = Portal.Connection.Anonymous
    )

    private val portalItem = PortalItem(
        portal = portal,
        itemId = "3bc3179f17da44a0ac0bfdac4ad15664"
    )

    private val _preplannedMapAreasList = mutableStateListOf<OfflineMapItemData>()

    /* list of preplanned map areas observed by the UI. The backed mutable list is updated when:
    1. the list is loaded from the portal,
    2. the list is loaded from the disk,
    3. a map is downloaded,
    4. a map is deleted
    */
    val preplannedMapAreasList: List<OfflineMapItemData> = _preplannedMapAreasList

    // web map item data observed by the Map List screen
    var webMapItemData = mutableStateOf<WebMapItemData?>(null)

    // observed by Map Detail Screen. The value is set to either web map or a downloaded map when a map is selected from the list.
    var arcGISMapForDetailScreen = mutableStateOf<ArcGISMap?>(null)

    val context = application.applicationContext

    private val networkManager = NetworkManager(context)
    private val dataStore = MapDataStore(context)

    init {
        createWebMapItemData()
        getPreplannedMapAreasList()
    }

    fun createDetailWebMap() {
        arcGISMapForDetailScreen.value = ArcGISMap(portalItem)
    }

    private suspend fun getDownloadedArcGISMap(downloadPath: String): ArcGISMap? {
        val mapPackage = MobileMapPackage(downloadPath)
        mapPackage.load().getOrElse {
            Log.d(TAG, "Error loading mobile map package: $it")
        }
        return mapPackage.maps.firstOrNull()
    }

    suspend fun createDetailMapFromDownloadPath(downloadPath: String) {
        arcGISMapForDetailScreen.value = getDownloadedArcGISMap(downloadPath)
    }

    private fun createWebMapItemData() {
        viewModelScope.launch {
            portalItem.load()
            webMapItemData.value = WebMapItemData(
                title = portalItem.title,
                description = portalItem.snippet,
                thumbnailUri = portalItem.thumbnail?.uri
            )
        }
    }

    private fun getPreplannedMapAreasList() {
        if (networkManager.isNetworkAvailable()) {
            getPreplannedMapAreasListFromPortal()
        } else {
            getPreplannedMapAreasListFromDisk()
        }
    }

    private fun getPreplannedMapAreasListFromPortal() {
        val offlineMapTask = OfflineMapTask(portalItem)
        viewModelScope.launch {
            offlineMapTask.getPreplannedMapAreas().onSuccess { preplannedMapAreas ->
                _preplannedMapAreasList.clear()
                _preplannedMapAreasList.addAll(preplannedMapAreas.map { mapArea ->
                    OfflineMapItemData(
                        preplannedMapArea = mapArea,
                        itemId = mapArea.portalItem.itemId,
                        title = mapArea.portalItem.title,
                        description = mapArea.portalItem.snippet,
                        downloadPath = if (downloadedMapExists(mapArea.portalItem.itemId)) getDownloadPathForPreplannedMapArea(
                            mapArea.portalItem.itemId
                        ) else null,
                        thumbnailUri = mapArea.portalItem.thumbnail?.uri,
                        state = getDownloadState(mapArea.portalItem.itemId)
                    )
                })
                persistPreplannedMapAreasList()
            }.onFailure {
                Toast.makeText(
                    context,
                    "Error loading offline maps from portal, showing downloaded maps instead",
                    Toast.LENGTH_SHORT
                ).show()
                getPreplannedMapAreasListFromDisk()
            }
        }
    }

    private fun getPreplannedMapAreasListFromDisk() {
        viewModelScope.launch {
            dataStore.offlineMapsList.collect { offlineMapsList ->
                _preplannedMapAreasList.clear()
                _preplannedMapAreasList.addAll(offlineMapsList)
            }
        }
    }

    private fun persistPreplannedMapAreasList() {
        viewModelScope.launch {
            val downloadedMapsList =
                _preplannedMapAreasList.filter { it.state == MapItemState.Downloaded }
            dataStore.saveOfflineMapsList(downloadedMapsList)
        }

    }

    private suspend fun getDownloadState(itemId: String): MapItemState {
        return if (downloadedMapExists(itemId)) {
            MapItemState.Downloaded
        } else {
            MapItemState.NotDownloaded
        }
    }

    private fun getDownloadPathForPreplannedMapArea(itemId: String): String {
        return context.getExternalFilesDir(null)?.path + "_offlinePreplannedMap_" + itemId
    }

    private suspend fun downloadedMapExists(itemId: String): Boolean {
        val downloadPath = getDownloadPathForPreplannedMapArea(itemId)
        val file = File(downloadPath)
        return file.exists() && getDownloadedArcGISMap(downloadPath) != null
    }

    fun downloadOfflineMapArea(
        mapArea: PreplannedMapArea,
        onDownloadSuccess: () -> Unit = {},
        onDownloadFailure: () -> Unit = {}
    ) {
        viewModelScope.launch {
            downloadOfflineMapAreaAsync(mapArea, onDownloadSuccess, onDownloadFailure)
        }
    }

    private suspend fun downloadOfflineMapAreaAsync(
        mapArea: PreplannedMapArea,
        onDownloadSuccess: () -> Unit = {},
        onDownloadFailure: () -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val offlineMapTask = OfflineMapTask(portalItem)
        val params = offlineMapTask.createDefaultDownloadPreplannedOfflineMapParameters(mapArea)
            .getOrElse { error ->
                Log.d(TAG, "Failed to create download parameters: $error")
                return@withContext
            }

        // use item id of the portal item to create a unique download path for the map area.
        val downloadPath = getDownloadPathForPreplannedMapArea(mapArea.portalItem.itemId)

        // delete the downloaded map if it exists to handle use case where the map was partially downloaded
        if (File(downloadPath).exists()) {
            deleteDownloadedMap(downloadPath)
        }

        val downloadPreplannedOfflineMapJob = offlineMapTask.createDownloadPreplannedOfflineMapJob(
            parameters = params,
            downloadDirectoryPath = downloadPath
        )
        launch {
            downloadPreplannedOfflineMapJob.progress.collect { progress ->
                Log.i(
                    TAG,
                    "Downloading preplanned offline map ${mapArea.portalItem.title}. Job progress: $progress%"
                )
                _preplannedMapAreasList.find { it.preplannedMapArea == mapArea }?.let { mapItem ->
                    // update the download progress in the list for the item being downloaded
                    val updatedMapItem = mapItem.copy(state = MapItemState.Downloading(progress))
                    val index = _preplannedMapAreasList.indexOf(mapItem)
                    if (index != -1) {
                        _preplannedMapAreasList[index] = updatedMapItem
                    }
                }
            }
        }

        downloadPreplannedOfflineMapJob.start()

        downloadPreplannedOfflineMapJob.result().onSuccess {
            _preplannedMapAreasList.find { it.preplannedMapArea == mapArea }?.let { mapItem ->
                Log.d(TAG, "Map downloaded successfully: ${mapArea.portalItem.title}")
                onDownloadSuccess()
                // update the downloaded map item's download path and download state in the list
                val updatedMapItem =
                    mapItem.copy(downloadPath = downloadPath, state = MapItemState.Downloaded)
                val index = _preplannedMapAreasList.indexOf(mapItem)
                if (index != -1) {
                    _preplannedMapAreasList[index] = updatedMapItem
                    addDownloadedMapToPersistedMapList(updatedMapItem)
                }
            }
        }.onFailure {
            Log.d(TAG, "Error downloading offline map: $it")
            onDownloadFailure()
        }
    }

    private fun addDownloadedMapToPersistedMapList(updatedMapItem: OfflineMapItemData) {
        viewModelScope.launch {
            val currentList = dataStore.offlineMapsList.firstOrNull() ?: emptyList()
            val updatedList = currentList.toMutableList().apply { add(updatedMapItem) }
            dataStore.saveOfflineMapsList(updatedList)
        }
    }

    fun deleteDownloadedMap(downloadPath: String) {
        val deleted = File(downloadPath).deleteRecursively()
        if (deleted) {
            _preplannedMapAreasList.find { it.downloadPath == downloadPath }?.let { mapItem ->
                val deletedMapItem =
                    mapItem.copy(downloadPath = null, state = MapItemState.NotDownloaded)
                val index = _preplannedMapAreasList.indexOf(mapItem)
                if (index != -1) {
                    _preplannedMapAreasList[index] = deletedMapItem
                    removeDeletedMapFromPersistedMapList(mapItem)
                }
            }
        } else {
            Log.d(TAG, "Error deleting map at path: $downloadPath")
        }
    }

    private fun removeDeletedMapFromPersistedMapList(deletedMapItem: OfflineMapItemData) {
        viewModelScope.launch {
            val currentList = dataStore.offlineMapsList.firstOrNull() ?: emptyList()
            val updatedList = currentList.filter { it.itemId != deletedMapItem.itemId }
            dataStore.saveOfflineMapsList(updatedList)
        }
    }

    fun getDownloadPath(mapItemData: MapItemData): String? {
        return if (mapItemData is OfflineMapItemData) {
            _preplannedMapAreasList.find { it.itemId == mapItemData.itemId }?.downloadPath!!
        } else {
            null
        }
    }
}