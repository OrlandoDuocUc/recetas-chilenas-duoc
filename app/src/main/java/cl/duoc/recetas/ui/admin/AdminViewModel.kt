package cl.duoc.recetas.ui.admin

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

data class AdminUiState(
    val isLoading: Boolean = false,
    val users: List<UserEntity> = emptyList(),
    val error: String? = null,
    val deleteSuccess: Boolean = false
)

class AdminViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    fun loadUsers() {
        _state.value = AdminUiState(isLoading = true)
        viewModelScope.launch {
            repository.getAllUsers().collect { users ->
                _state.value = AdminUiState(users = users)
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val result = repository.deleteUser(userId)
            result.onSuccess {
                _state.value = _state.value.copy(deleteSuccess = true)
                loadUsers()
            }.onFailure { error ->
                _state.value = _state.value.copy(error = error.message ?: "Error al eliminar usuario")
            }
        }
    }
}

class AdminViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val sessionManager = SessionManager(application)
            val repository = AuthRepository(database.userDao(), sessionManager)
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
