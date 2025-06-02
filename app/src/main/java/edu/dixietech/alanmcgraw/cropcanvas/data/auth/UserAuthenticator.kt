package edu.dixietech.alanmcgraw.cropcanvas.data.auth

import kotlinx.coroutines.flow.Flow

interface UserAuthenticator {
    val authToken: Flow<String?>

    suspend fun saveToken(token: String)
    suspend fun deleteToken()
}