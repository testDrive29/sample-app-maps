package com.example.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.data.MapItemData
import com.example.app.data.MapItemState
import com.example.app.data.OfflineMapItemData
import com.example.app.ui.theme.AppTypography

/**
 * Composable function that displays a map item with a title, description, and optional thumbnail.
 *
 * @param title The title of the map item.
 * @param description The description of the map item.
 * @param onClick Callback function to be invoked when the map item is clicked.
 * @param modifier The modifier to be applied to the map item.
 * @param thumbnail The URL of the thumbnail image to be displayed. Defaults to null.
 */
@Composable
fun MapItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thumbnail: String? = null
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {

        //Async image loading using Coil
        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = thumbnail,
            contentDescription = "Thumbnail"
        )

        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            LabelText(text = title)
            Spacer(modifier = Modifier.height(4.dp))
            BodyText(text = description)
        }
    }
}

/**
 * Composable function that displays a map item with a button for additional actions.
 *
 * @param mapItemData The data of the map item to be displayed.
 * @param onPreplannedMapAreaClick Callback function to be invoked when the map item is clicked.
 * @param onDownloadButtonClick Callback function to be invoked when the download button is clicked.
 * @param onDeleteButtonClick Callback function to be invoked when the delete button is clicked.
 */
@Composable
fun MapItemWithButton(
    mapItemData: OfflineMapItemData,
    onPreplannedMapAreaClick: (MapItemData) -> Unit,
    onDownloadButtonClick: () -> Unit,
    onDeleteButtonClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MapItem(
            modifier = Modifier.weight(1f),
            title = mapItemData.title,
            description = mapItemData.description,
            onClick = {
                if (mapItemData.downloadPath != null) {
                    onPreplannedMapAreaClick(mapItemData)
                }
            },
            thumbnail = mapItemData.thumbnailUri
        )
        when (mapItemData.state) {
            is MapItemState.Downloading -> {
                CircularProgressIndicator(
                    progress = { mapItemData.state.progress.toFloat() / 100 },
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterVertically),
                )
            }

            is MapItemState.Downloaded -> {
                MoreIconButtonDropdownMenu(
                    menuItems = listOf(
                        MenuItem(
                            label = "Delete Map",
                            onClick = { onDeleteButtonClick(mapItemData.downloadPath!!) }
                        ),
                        MenuItem(
                            label = "View Map",
                            onClick = { onPreplannedMapAreaClick(mapItemData) }
                        )
                    )
                )
            }

            is MapItemState.NotDownloaded -> {
                IconButton(onClick = onDownloadButtonClick) {
                    Icon(
                        painterResource(id = R.drawable.ic_download),
                        contentDescription = "Download"
                    )
                }
            }
        }
    }
}

/**
 * Data class representing a menu item with a label and click action.
 */
data class MenuItem(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Composable function that displays a dropdown menu with a list of menu items.
 */
@Composable
fun MoreIconButtonDropdownMenu(
    modifier: Modifier = Modifier,
    menuItems: List<MenuItem>,
    iconDrawableId: Int = R.drawable.ic_more,
) {
    val expanded = remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        IconButton(
            onClick = {
                expanded.value = true
            },
            modifier = modifier
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconDrawableId),
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            menuItems.forEach { menuItem ->
                DropdownMenuItem(
                    text = { Text(menuItem.label) },
                    onClick = {
                        expanded.value = false
                        menuItem.onClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LabelText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = AppTypography.labelMedium
    )
}

@Composable
fun BodyText(text: String) {
    Text(
        text = text,
        style = AppTypography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 3
    )
}

