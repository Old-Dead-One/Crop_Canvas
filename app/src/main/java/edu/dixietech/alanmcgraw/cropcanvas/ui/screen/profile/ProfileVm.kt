package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserRepository
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading: ProfileUiState()
    data class Success(val profile: Profile): ProfileUiState()
    data class Error(val message: String): ProfileUiState()
}

@HiltViewModel
class ProfileVm @Inject constructor(
    val repository: CropCanvasRepository,
    val userRepo: UserRepository,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    fun loadProfile() {
        // Cancel any existing collection to avoid multiple collectors
        viewModelScope.launch {
            repository.getUserProfile().collect { result ->
                _uiState.update { 
                    when (result) {
                        is AsyncResult.Loading -> ProfileUiState.Loading
                        is AsyncResult.Success -> ProfileUiState.Success(result.result)
                        is AsyncResult.Error -> ProfileUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun observeProfile() {
        viewModelScope.launch {
            try {
                repository.observeUserProfile().collect { profile ->
                    _uiState.update { ProfileUiState.Success(profile) }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileVm", "Error in observeProfile", e)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepo.signOut()
        }
    }

    fun sellProducts(productName: String, amount: Int) {
        viewModelScope.launch {
            repository.sellProducts(productName, amount).collect { result ->
                when (result) {
                    is AsyncResult.Loading -> { /* Loading state handled by UI */ }
                    is AsyncResult.Success -> {
                        // Force refresh profile data after selling
                        forceRefreshProfile()
                    }
                    is AsyncResult.Error -> {
                        android.util.Log.e("ProfileVm", "Selling failed: ${result.message}")
                    }
                }
            }
        }
    }

    fun forceRefreshProfile() {
        viewModelScope.launch {
            try {
                // Cancel current collection and restart with fresh data
                observeProfile()
            } catch (e: Exception) {
                android.util.Log.e("ProfileVm", "Error in forceRefreshProfile", e)
            }
        }
    }
}