package cl.duoc.recetas.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    
    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }
    
    val userId: Flow<Int?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL_KEY] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE_KEY] }
    
    suspend fun saveSession(userId: Int, email: String, name: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[USER_ROLE_KEY] = role
        }
    }
    
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
    
    suspend fun isLoggedIn(): Boolean {
        var loggedIn = false
        context.dataStore.data.map { it[USER_ID_KEY] }.collect { 
            loggedIn = it != null
        }
        return loggedIn
    }
}
