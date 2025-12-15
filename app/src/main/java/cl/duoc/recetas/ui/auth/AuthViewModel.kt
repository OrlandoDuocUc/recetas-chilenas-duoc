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
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { user ->
                _authState.value = AuthUiState(user = user)
            }.onFailure { error ->
                _authState.value = AuthUiState(error = error.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _authState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val result = repository.register(email, password, name)
            result.onSuccess { user ->
                _authState.value = AuthUiState(user = user)
            }.onFailure { error ->
                _authState.value = AuthUiState(error = error.message ?: "Error al registrarse")
            }
        }
    }
}

class AuthViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val sessionManager = SessionManager(application)
            val repository = AuthRepository(database.userDao(), sessionManager)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
