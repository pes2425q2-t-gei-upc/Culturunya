import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.culturunya.dataclasses.login.SessionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

/**
 * @class SessionManager
 * @brief Clase que gestiona la sesión del usuario utilizando DataStore.
 *
 * Almacena y recupera datos de sesión como token, nombre de usuario, correo electrónico,
 * foto de perfil y estado de administrador.
 */
class SessionManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val PROFILE_PIC = stringPreferencesKey("profile_pic")
        private val RANK_QUIZ = stringPreferencesKey("rank_quiz")
        private val RANK_EVENTS = stringPreferencesKey("rank_events")
        private val TOTAL_QUIZ_POINTS = intPreferencesKey("total_quiz_points")
        private val TOTAL_EVENTS_POINTS = intPreferencesKey("total_events_points")
        private val IS_ADMIN = booleanPreferencesKey("is_admin")
        private const val TAG = "SessionManager"
    }

    /**
     * @brief Guarda la sesión del usuario.
     *
     * @param token Token de autenticación del usuario.
     * @param username Nombre de usuario.
     * @param email Correo electrónico del usuario.
     * @param profilePic URL de la foto de perfil del usuario.
     * @param isAdmin Estado de administrador del usuario.
     */
    suspend fun saveSession(
        token: String,
        username: String,
        email: String,
        profilePic: String,
        isAdmin: Boolean,
        rankQuiz: String? = null,
        rankEvents: String? = null,
        totalQuizPoints: Int? = null,
        totalEventsPoints: Int? = null,
        is_admin: Boolean
    ) {
        Log.d(TAG, "Saving session: token=$token, username=$username, email=$email, profilePic=$profilePic, isAdmin=$isAdmin")

        dataStore.edit { preferences ->
            if (token.isNotEmpty()) preferences[TOKEN] = token
            if (username.isNotEmpty()) preferences[USERNAME] = username
            if (email.isNotEmpty()) preferences[EMAIL] = email
            if (profilePic.isNotEmpty()) preferences[PROFILE_PIC] = profilePic
            preferences[IS_ADMIN] = isAdmin

            rankQuiz?.let { preferences[RANK_QUIZ] = it }
            rankEvents?.let { preferences[RANK_EVENTS] = it }
            totalQuizPoints?.let { preferences[TOTAL_QUIZ_POINTS] = it }
            totalEventsPoints?.let { preferences[TOTAL_EVENTS_POINTS] = it }
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
                isAdmin = preferences[IS_ADMIN] ?: false,
                rankQuiz = preferences[RANK_QUIZ] ?: "",
                rankEvents = preferences[RANK_EVENTS] ?: "",
                totalQuizPoints = preferences[TOTAL_QUIZ_POINTS] ?: 0,
                totalEventsPoints = preferences[TOTAL_EVENTS_POINTS] ?: 0,
                is_admin = preferences[IS_ADMIN] ?: false
            )
            Log.d(TAG, "Session data retrieved: $data")
            data
        }

    /**
     * @brief Limpia los datos de la sesion
     */
    suspend fun clearSession() {
        Log.d(TAG, "Clearing session...")
        dataStore.edit { preferences ->
            preferences.clear()
        }
        Log.d(TAG, "Session cleared.")
    }
}

