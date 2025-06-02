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
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            repository.getUserProfile()
                .collect { result ->
                    _uiState.value = when (result) {
                        is AsyncResult.Loading -> ProfileUiState.Loading
                        is AsyncResult.Success -> ProfileUiState.Success(result.result)
                        is AsyncResult.Error -> ProfileUiState.Error(result.message)
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepo.signOut()
        }
    }
}