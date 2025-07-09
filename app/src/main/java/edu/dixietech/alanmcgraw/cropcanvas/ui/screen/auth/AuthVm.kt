package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

sealed class AuthUiState {
    data object Loading: AuthUiState()
    data object Unauthenticated: AuthUiState()
    data class Authenticated(val token: String): AuthUiState()
    data class Error(val message: String): AuthUiState()
}

@HiltViewModel
class AuthVm @Inject constructor(
    val userRepo: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                userRepo.authToken.collect { token ->
                    // Handle Token Changes
                    android.util.Log.d("AuthVm", "Token received: ${token?.take(10)}...")
                    if (token == null) {
                        _uiState.value = AuthUiState.Unauthenticated
                    } else {
                        _uiState.value = AuthUiState.Authenticated(token)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthVm", "Error in auth token flow", e)
                _uiState.value = AuthUiState.Error("Authentication error: ${e.message}")
            }
        }
    }

    fun signUp(username: String) {
        if (username.isEmpty()) {
            _uiState.value = AuthUiState.Error("You must provide a username.")
            return
        }

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                userRepo.signUp(username)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unexpected Error")
            }
        }
    }

    fun signIn(token: String) {
        if (token.isEmpty()) {
            _uiState.value = AuthUiState.Error("You must provide a token.")
            return
        }

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                userRepo.signIn(token)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unexpected Error")
            }
        }
    }
}