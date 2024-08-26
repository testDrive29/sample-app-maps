package com.example.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/* DataStore class to store offline maps list to show the downloaded maps list when there is no internet connection */

class MapDataStore(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "map_data_store")

    companion object {
        val OFFLINE_MAPS_LIST_KEY = stringPreferencesKey("offline_maps_list")
    }

    private val gson = Gson().newBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    val offlineMapsList: Flow<List<OfflineMapItemData>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[OFFLINE_MAPS_LIST_KEY] ?: return@map emptyList()
            val type = object : TypeToken<List<OfflineMapItemData>>() {}.type
            gson.fromJson(json, type)
        }

    suspend fun saveOfflineMapsList(offlineMapsList: List<OfflineMapItemData>) {
        val json = gson.toJson(offlineMapsList)
        context.dataStore.edit { preferences ->
            preferences[OFFLINE_MAPS_LIST_KEY] = json
        }
    }
}