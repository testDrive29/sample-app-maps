package com.example.app.navigation

import androidx.annotation.StringRes
import com.example.app.R

/**
 * Enum class defining the screens in the app.
 */
enum class AppScreen(@StringRes val title: Int) {
    MapList(title = R.string.app_name),
    MapDetail(title = R.string.map_detail_app_bar_title),
}