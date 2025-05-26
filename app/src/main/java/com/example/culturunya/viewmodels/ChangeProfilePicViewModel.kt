package com.example.culturunya.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

data class ChangeProfilePicState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class ChangeProfilePicViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChangeProfilePicState())
    val state: StateFlow<ChangeProfilePicState> = _state

    private val repository = UserRepository(Api.instance)
    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
    }

    fun uploadProfilePic(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                // Redimensionar la imatge a un màxim de 800x800
                val maxDimension = 800
                val width = bitmap.width
                val height = bitmap.height
                val scale = if (width > height) {
                    maxDimension.toFloat() / width
                } else {
                    maxDimension.toFloat() / height
                }
                
                val scaledBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                )

                // Comprimir la imatge
                val file = File.createTempFile("profile_pic", ".jpeg", context.cacheDir)
                file.outputStream().use { out ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val profilePic = MultipartBody.Part.createFormData("profile_pic", file.name, requestFile)

                val currentToken = CurrentSession.token
                val result = repository.uploadProfilePic("Token $currentToken", profilePic)

                result.onSuccess {
                    // After successful upload, fetch updated profile info
                    try {
                        val userInfo = repository.getProfileInfo("Token $currentToken")
                        if (userInfo.profile_pic != null) {
                            CurrentSession.setUserData(userInfo.username, userInfo.email, userInfo.profile_pic, userInfo.language, userInfo.is_admin)
                        }
                        _state.value = _state.value.copy(isLoading = false, success = true)
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Error actualitzant la informació del perfil: ${e.message}"
                        )
                    }
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = when (error) {
                            is HttpException -> when (error.code()) {
                                400 -> "Dato inválido"
                                401 -> "No autenticado"
                                else -> "Error desconocido: ${error.code()}"
                            }
                            else -> "Error desconocido: ${error.message}"
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error desconocido: ${e.message}"
                )
            }
        }
    }
} 