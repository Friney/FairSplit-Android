package com.friney.fairsplit.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.auth.AuthRepository
import com.friney.fairsplit.data.utility.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful) {
                    _loginState.value = AuthState.Success
                } else {
                    _loginState.value = AuthState.Error("Ошибка входа: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val response = authRepository.register(name, email, password, confirmPassword)
                if (response.isSuccessful) {
                    val loginResponse = authRepository.login(email, password)
                    if (loginResponse.isSuccessful) {
                        _registerState.value = AuthState.Success
                    } else {
                        _registerState.value =
                            AuthState.Error("Регистрация успешна, но не удалось войти автоматически")
                    }
                } else {
                    _registerState.value =
                        AuthState.Error("Ошибка регистрации: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun logout() {
        authRepository.logout()
    }
}