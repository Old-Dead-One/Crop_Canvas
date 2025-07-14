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

    fun observeProfile() {
        viewModelScope.launch {
            try {
                repository.observeUserProfile().collect { profile ->
                    _uiState.update { ProfileUiState.Success(profile) }
                }
            } catch (e: Exception) {
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
                    is AsyncResult.Loading -> { }
                    is AsyncResult.Success -> {
                        forceRefreshProfile()
                    }
                    is AsyncResult.Error -> { }
                }
            }
        }
    }

    fun forceRefreshProfile() {
        viewModelScope.launch {
            try {
                observeProfile()
            } catch (e: Exception) {
            }
        }
    }
}