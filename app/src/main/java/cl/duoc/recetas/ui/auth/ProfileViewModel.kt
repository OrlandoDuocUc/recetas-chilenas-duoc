package cl.duoc.recetas.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.recetas.data.AuthRepository
import cl.duoc.recetas.data.local.AppDatabase
import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.data.local.entities.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null,
    val success: Boolean = false
)

class ProfileViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    fun loadProfile() {
        _state.value = ProfileUiState(isLoading = true)
        viewModelScope.launch {
            val userId = repository.getUserId().firstOrNull()
            if (userId != null) {
                val user = repository.getCurrentUser()
                _state.value = ProfileUiState(user = user)
            } else {
                _state.value = ProfileUiState(error = "No se encontró el usuario")
            }
        }
    }

    fun updateProfile(name: String, newPassword: String?) {
        _state.value = _state.value.copy(isLoading = true, success = false)
        viewModelScope.launch {
            val userId = repository.getUserId().firstOrNull()
            if (userId != null) {
                val result = repository.updateProfile(userId, name, newPassword)
                result.onSuccess {
                    loadProfile()
                    _state.value = _state.value.copy(success = true, isLoading = false)
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al actualizar",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

class ProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val sessionManager = SessionManager(application)
            val repository = AuthRepository(database.userDao(), sessionManager)
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
