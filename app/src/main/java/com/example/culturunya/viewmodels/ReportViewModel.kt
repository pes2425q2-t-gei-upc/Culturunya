package com.example.culturunya.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api // Assuming you have your Api instance here
import com.example.culturunya.dataclasses.reports.Report
import com.example.culturunya.dataclasses.reports.ReportRequest // Your ReportRequest class
import com.example.culturunya.dataclasses.reports.ResolveAction
import com.example.culturunya.repositories.ReportsRepository // Your repository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Collections

class ReportViewModel : ViewModel() {
    private val reportsRepository = ReportsRepository(Api.instance) // Example instantiation

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _errorCode = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastEventChannel = Channel<String>()
    val toastEvent = _toastEventChannel.receiveAsFlow()

    private val _reports = MutableStateFlow<List<Report>>(Collections.emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _reportResolved = MutableStateFlow(false)
    val reportResolved: StateFlow<Boolean> = _reportResolved

    fun reportRating(reportRequest: ReportRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _successMessage.value = null
            _errorMessage.value = null
            _errorCode.value = null
            _reportResolved.value = false
            reportsRepository.reportRating(reportRequest).onSuccess {
                _successMessage.value = "Reports obtained successfully!"
                _toastEventChannel.send("Success")
                _isLoading.value = false
            }.onFailure { exception ->
                _errorMessage.value = "Error: ${exception.message ?: "Failed to obtain reports."}"
                _toastEventChannel.send("Error: ${exception.message}")
                _isLoading.value = false
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        _toastEventChannel.close() // Cierra el canal cuando el ViewModel se destruye
    }

    fun getReports() {
        viewModelScope.launch {
            reportsRepository.getReports().onSuccess {
                Log.d("GetReports", "Success: Received ${it.size} messages")
                _reports.value = it
                _successMessage.value = "Success"
                _errorMessage.value = null
                _errorCode.value = null
                _reportResolved.value = false
            }.onFailure { error ->
                _errorMessage.value = when (error) {
                    is HttpException -> {
                        Log.e("GetReports", "HTTP Error code: ${error.code()}")
                        "HTTP Error code: ${error.code()}. Error message: ${error.message()}"
                    }
                    else -> "Error: ${error.message ?: "Failed to obtain reports."}"
                }
                _errorCode.value = when(error){
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }

    fun resolveReport(reportId: String, action: String, message: String) {
        viewModelScope.launch {
            reportsRepository.resolveReport(reportId, action, message).onSuccess {
                _successMessage.value = "Success"
                _errorMessage.value = null
                _errorCode.value = null
                _reportResolved.value = true
            }.onFailure { error ->
                _errorMessage.value = when (error) {
                    is HttpException -> {
                        Log.e("GetReports", "HTTP Error code: ${error.code()}")
                        "HTTP Error code: ${error.code()}. Error message: ${error.message()}"
                    }
                    else -> "Error: ${error.message ?: "Failed to obtain reports."}"
                }
                _errorCode.value = when(error){
                    is HttpException -> error.code()
                    else -> -1
                }
            }

        }
    }

    fun reset() {
        _reports.value = emptyList()
        _successMessage.value = null
        _errorMessage.value = null
        _errorCode.value = null
        _reportResolved.value = false
    }
}