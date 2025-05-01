package com.example.culturunya.endpoints.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val repository = UserRepository(Api.instance)
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _profilePic = MutableStateFlow("")
    val profilePic: StateFlow<String> = _profilePic

    fun fetchProfileInfo(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getProfileInfo(token)
                _username.value = response.username
                _email.value = response.email
                _profilePic.value = response.profilePic ?: ""
            }
            catch (e: Exception){
                // Manejar errores
            }
        }
    }
}