package com.example.teashop.screen.screen.profile_screen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teashop.data.model.user.User
import com.example.teashop.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    fun getLoggedUserInfo(token: String, onError: () -> Unit) {
        viewModelScope.launch {
            val response = UserRepository().getLoggedUserInfo("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    _user.value = it
                }
            } else {
                onError()
            }
        }
    }
}