package edu.dixietech.alanmcgraw.cropcanvas.data.auth

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

class PreferencesUserAuthenticator(
    private val dataStore: DataStore<Preferences>
): UserAuthenticator {
    override val authToken: Flow<String?> = dataStore.data
        .catch { exception ->
            Log.e(LOG_TAG, "Error Reading Preferences", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            try {
                preferences[USER_TOKEN]
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error accessing USER_TOKEN", e)
                null
            }
        }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN)
        }
    }

    private companion object {
        const val LOG_TAG = "PreferencesUserAuthenticator"
        val USER_TOKEN = stringPreferencesKey("user_token")
    }
}