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

class GetLeaderboardEventsViewModel : ViewModel() {
    private val _getLeaderboardEventsResponse = MutableStateFlow<List<RankingPosition>?>(null)
    val getLeaderboardEventsResponse: StateFlow<List<RankingPosition>?> = _getLeaderboardEventsResponse

    private val _getLeaderboardEventsError = MutableStateFlow<Int?>(null)
    val getLeaderboardEventsError: StateFlow<Int?> = _getLeaderboardEventsError

    private val api = Api.instance
    private val repository = LeaderboardRepository(api)

    fun getLeaderboardEvents() {
        Log.d("GetLeaderboardEvents", "Function called")

        viewModelScope.launch {
            val token = CurrentSession.token
            Log.d("GetLeaderboardEvents", "Using token: $token")

            val result = repository.getLeaderboardQuiz("Token $token")

            result.onSuccess { body ->
                Log.d("GetLeaderboardEvents", "Success: Received ${body.size} positions")
                _getLeaderboardEventsResponse.value = body
                _getLeaderboardEventsError.value = null
            }.onFailure { error ->
                Log.e("GetLeaderboardEvents", "Error fetching chat: ${error.message}")
                _getLeaderboardEventsResponse.value = null
                _getLeaderboardEventsError.value = when (error) {
                    is HttpException -> {
                        Log.e("GetLeaderboardEvents", "HTTP Error code: ${error.code()}")
                        error.code()
                    }
                    else -> -1
                }
            }
        }
    }
}