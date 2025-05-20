package com.example.culturunya.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.CurrentSession
import com.example.culturunya.dataclasses.ranking.RankingPosition
import com.example.culturunya.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GetLeaderboardQuizViewModel : ViewModel() {
    private val _getLeaderboardQuizResponse = MutableStateFlow<List<RankingPosition>?>(null)
    val getLeaderboardQuizResponse: StateFlow<List<RankingPosition>?> = _getLeaderboardQuizResponse

    private val _getLeaderboardQuizError = MutableStateFlow<Int?>(null)
    val getLeaderboardQuizError: StateFlow<Int?> = _getLeaderboardQuizError

    private val api = Api.instance
    private val repository = LeaderboardRepository(api)

    fun getLeaderboardQuiz() {
        Log.d("GetLeaderboardQuiz", "Function called")

        viewModelScope.launch {
            val token = CurrentSession.token
            Log.d("GetLeaderboardQuiz", "Using token: $token")

            val result = repository.getLeaderboardQuiz("Token $token")

            result.onSuccess { body ->
                Log.d("GetLeaderboardQuiz", "Success: Received ${body.size} positions")
                _getLeaderboardQuizResponse.value = body
                _getLeaderboardQuizError.value = null
            }.onFailure { error ->
                Log.e("GetLeaderboardQuiz", "Error fetching chat: ${error.message}")
                _getLeaderboardQuizResponse.value = null
                _getLeaderboardQuizError.value = when (error) {
                    is HttpException -> {
                        Log.e("GetLeaderboardQuiz", "HTTP Error code: ${error.code()}")
                        error.code()
                    }
                    else -> -1
                }
            }
        }
    }
}