package com.example.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.app.screens.MapApp
import com.example.app.ui.theme.DisplayAWebMapTheme
import com.example.app.viewmodel.MapListViewModel

/**
 * Main activity for the application, responsible for setting up the content view and initializing the ViewModel.
 */
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MapListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DisplayAWebMapTheme {
                MapApp(viewModel)
            }
        }
    }
}

