package com.example.culturunya.viewmodels

import SessionManager
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.dataclasses.users.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel for managing user-related operations.
 *
 * @property application The application context.
 */
class UserViewModel(application: Application): AndroidViewModel(application) {
    private val repository = UserRepository(Api.instance)
    private val sessionManager = SessionManager(application.applicationContext)

    private val _getUserInfoResponse = MutableStateFlow<UserInfo?>(null)
    val getUserInfoResponse: StateFlow<UserInfo?> = _getUserInfoResponse

    private val _getUserInfoError = MutableStateFlow<Int?>(null)
    val getUserInfoError: StateFlow<Int?> = _getUserInfoError

    /**
     * Fetches the user's profile information.
     */
    fun fetchProfileInfo() {
        Log.d("UserViewModel", "fetchProfileInfo called")
        viewModelScope.launch {
            try {
                val token = CurrentSession.token
                Log.d("UserViewModel", "Using token: $token")
                _getUserInfoResponse.value = repository.getProfileInfo("Token $token")
                Log.d("UserViewModel", "Profile info fetched: ${getUserInfoResponse.value}")
                CurrentSession.getInstance()
                val user = _getUserInfoResponse.value
                if (user != null) {
                    Log.d("UserViewModel", "Setting user data with: ${user.username}, ${user.email}, ${user.language}")
                    if (user.profile_pic != null) CurrentSession.setUserData(user.username, user.email, user.profile_pic, user.language)
                    else CurrentSession.setUserData(user.username, user.email, "", user.language)
                    CurrentSession.saveToDataStore(sessionManager)
                }
            }
            catch (e: Exception){
                val code = when (e) {
                    is HttpException -> e.code()
                    else -> -1
                }
                Log.d("UserViewModel", "Error fetching profile info: ${e.message}, code: $code")
                _getUserInfoError.value = code
            }
        }
    }
}
