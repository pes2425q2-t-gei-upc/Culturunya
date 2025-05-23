import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val PROFILE_PIC = stringPreferencesKey("profile_pic")
        private val IS_ADMIN = booleanPreferencesKey("is_admin")

        private const val TAG = "SessionManager"
    }

    suspend fun saveSession(
        token: String,
        username: String,
        email: String,
        profilePic: String,
        isAdmin: Boolean
    ) {
        Log.d(TAG, "Saving session: token=$token, username=$username, email=$email, profilePic=$profilePic, isAdmin=$isAdmin")

        dataStore.edit { preferences ->
            if (token.isNotEmpty()) preferences[TOKEN] = token
            if (username.isNotEmpty()) preferences[USERNAME] = username
            if (email.isNotEmpty()) preferences[EMAIL] = email
            if (profilePic.isNotEmpty()) preferences[PROFILE_PIC] = profilePic
            preferences[IS_ADMIN] = isAdmin
        }
    }

    val token: Flow<String?> = dataStore.data
        .map { preferences ->
            val value = preferences[TOKEN]
            Log.d(TAG, "Token retrieved: $value")
            value
        }

    val sessionData: Flow<SessionData> = dataStore.data
        .map { preferences ->
            val data = SessionData(
                token = preferences[TOKEN] ?: "",
                username = preferences[USERNAME] ?: "",
                email = preferences[EMAIL] ?: "",
                profilePic = preferences[PROFILE_PIC] ?: "",
                isAdmin = preferences[IS_ADMIN] ?: false
            )
            Log.d(TAG, "Session data retrieved: $data")
            data
        }

    suspend fun clearSession() {
        Log.d(TAG, "Clearing session...")
        dataStore.edit { preferences ->
            preferences.clear()
        }
        Log.d(TAG, "Session cleared.")
    }
}

data class SessionData(
    val token: String,
    val username: String,
    val email: String,
    val profilePic: String,
    val isAdmin: Boolean
)
