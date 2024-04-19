package com.example.teashop.screen.screen.profile_screen.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teashop.data.model.auth.AuthRequest
import com.example.teashop.data.model.auth.AuthResponse
import com.example.teashop.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel : ViewModel() {
    fun authenticate(authRequest: AuthRequest, onSuccess: (AuthResponse) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val response: Response<AuthResponse> = AuthRepository().authenticate(authRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    onSuccess(it)
                }
            } else {
                onError()
            }
        }
    }
}