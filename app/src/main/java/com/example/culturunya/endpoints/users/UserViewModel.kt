package com.example.culturunya.endpoints.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserViewModel: ViewModel() {
    private val repository = UserRepository(Api.instance)

    private val _getUserInfoResponse = MutableStateFlow<UserInfo?>(null)
    val getUserInfoResponse: StateFlow<UserInfo?> = _getUserInfoResponse

    private val _getUserInfoError = MutableStateFlow<Int?>(null)
    val getUserInfoError: StateFlow<Int?> = _getUserInfoError

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
                    Log.d("UserViewModel", "Setting user data with: ${user.username}, ${user.email}")
                    if (user.profile_pic != null) CurrentSession.setUserData(user.username, user.email, user.profile_pic)
                    else CurrentSession.setUserData(user.username, user.email, "")
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
