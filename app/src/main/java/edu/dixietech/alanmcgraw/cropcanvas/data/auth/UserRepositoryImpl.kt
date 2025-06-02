package edu.dixietech.alanmcgraw.cropcanvas.data.auth

import edu.dixietech.alanmcgraw.cropcanvas.data.network.CropCanvasApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: UserAuthenticator,
    private val network: CropCanvasApi
): UserRepository {
    override val authToken: Flow<String?> = auth.authToken

    override suspend fun signUp(username: String) {
        val response = network.createAccount(username)
        auth.saveToken(response.token)
    }

    override suspend fun signIn(token: String) {
        auth.saveToken(token)
    }

    override suspend fun signOut() {
        auth.deleteToken()
    }
}