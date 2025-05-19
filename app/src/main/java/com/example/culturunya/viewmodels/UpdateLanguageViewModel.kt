package com.example.culturunya.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UpdateLanguageViewModel : ViewModel() {
    private val _updateLanguageStatus = MutableStateFlow<Int?>(null)
    val updateLanguageStatus: StateFlow<Int?> = _updateLanguageStatus

    private val api = Api.instance
    private val repository = UserRepository(api)

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            val token = CurrentSession.token
            Log.d("UpdateLanguageVM", "Trying to update language to: $language with token: Token $token")

            val result = repository.updateLangugage("Token $token", language)

            result.onSuccess {
                Log.d("UpdateLanguageVM", "Language update successful.")
                _updateLanguageStatus.value = 200
            }.onFailure { error ->
                val code = when (error) {
                    is HttpException -> {
                        Log.d("UpdateLanguageVM", "HttpException occurred: code ${error.code()}")
                        error.code()
                    }
                    else -> {
                        Log.d("UpdateLanguageVM", "Unexpected error: ${error.localizedMessage}")
                        -1
                    }
                }
                _updateLanguageStatus.value = code
            }
        }
    }
}
