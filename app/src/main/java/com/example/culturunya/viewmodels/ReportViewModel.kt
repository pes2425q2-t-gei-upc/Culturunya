package com.example.culturunya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api // Assuming you have your Api instance here
import com.example.culturunya.dataclasses.ratings.ReportRequest // Your ReportRequest class
import com.example.culturunya.repositories.ReportsRepository // Your repository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing reports.
 *
 * @property reportsRepository The repository for fetching and creating reports.
 */
class ReportViewModel : ViewModel() {
    private val reportsRepository = ReportsRepository(Api.instance) // Example instantiation

    //private val _successMessage = MutableStateFlow<String?>(null)
    //val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    //private val _errorMessage = MutableStateFlow<String?>(null)
    //val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // To show a loading indicator in the button if you want
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastEventChannel = Channel<String>()
    val toastEvent = _toastEventChannel.receiveAsFlow()

    /**
     * Report a rating.
     *
     * @param reportRequest The request object containing the report details.
     */
    fun reportRating(reportRequest: ReportRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            //_successMessage.value = null // Clear previous messages
            //_errorMessage.value = null   // Clear previous messages
            reportsRepository.reportRating(reportRequest).onSuccess {
                //_successMessage.value = "Report submitted successfully!" // Or a more specific message from response
                _toastEventChannel.send("Success")
                _isLoading.value = false
            }.onFailure { exception ->
                //_errorMessage.value = "Error: ${exception.message ?: "Failed to submit report."}"
                _toastEventChannel.send("Error: ${exception.message}")
                _isLoading.value = false
            }
        }
    }
    /*fun clearSuccessMessage() {
        _successMessage.value = null
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }*/

    override fun onCleared() {
        super.onCleared()
        _toastEventChannel.close() // Cierra el canal cuando el ViewModel se destruye
    }
}