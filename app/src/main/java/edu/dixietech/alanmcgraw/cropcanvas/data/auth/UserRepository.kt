package edu.dixietech.alanmcgraw.cropcanvas.data.auth

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val authToken: Flow<String?>

    suspend fun signUp(username: String)
    suspend fun signIn(token: String)
    suspend fun signOut()
}