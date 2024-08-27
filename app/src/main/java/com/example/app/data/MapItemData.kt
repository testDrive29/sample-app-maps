package com.example.app.data

import com.arcgismaps.tasks.offlinemaptask.PreplannedMapArea
import com.google.gson.annotations.Expose

/**
 * Represents a map item data on the map list screen.
 */
sealed interface MapItemData

/**
 * Represents a web map item data.
 *
 * @property title The title of the web map.
 * @property description The description of the web map.
 * @property thumbnailUri The URI of the thumbnail image for the web map.
 */
data class WebMapItemData(
    val title: String,
    val description: String,
    val thumbnailUri: String?
) : MapItemData

/**
 * Represents an offline or preplanned map item data.
 *
 * @property preplannedMapArea The preplanned map area associated with this offline map item.
 * @property itemId The item ID of the offline map. Used to identify the unique download path for the offline map.
 * @property title The title of the offline map.
 * @property description The description of the offline map.
 * @property downloadPath The download path of the offline map on the device, if it exists.
 * @property thumbnailUri The URI of the thumbnail image for the offline map.
 * @property state The download state of the offline map. Can be [MapItemState.Downloading], [MapItemState.Downloaded], or [MapItemState.NotDownloaded].
 */
data class OfflineMapItemData(
    val preplannedMapArea: PreplannedMapArea,
    @Expose val itemId: String,
    @Expose val title: String,
    @Expose val description: String,
    @Expose val downloadPath: String?,
    @Expose val thumbnailUri: String?,
    val state: MapItemState
) : MapItemData

sealed interface MapItemState {
    data class Downloading(val progress: Int = 0) : MapItemState
    data object Downloaded : MapItemState
    data object NotDownloaded : MapItemState
}