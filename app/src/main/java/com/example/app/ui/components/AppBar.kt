package com.example.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.app.R
import com.example.app.navigation.AppScreen

/**
 * Composable function that displays the top app bar with a title and optional navigation icon.
 *
 * @param currentScreen The current screen being displayed, used to set the title.
 * @param canNavigateBack Boolean indicating if the back navigation icon should be displayed.
 * @param navigateUp Lambda function to be called when the back navigation icon is clicked.
 * @param modifier The modifier to be applied to the TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        modifier = modifier,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}