package com.friney.fairsplit.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.auth.AuthRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.RegisteredUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    val userInAppLiveData = MutableLiveData<DataState<RegisteredUser>>()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        authRepository.getCurrentUser().let {
            userInAppLiveData.postValue(DataState.Loading())
            if (it.isSuccessful) {
                userInAppLiveData.postValue(DataState.Success(it.body() as RegisteredUser))
            } else {
                val errorBody = it.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        JSONObject(errorBody).getString("message")
                    } catch (e: Exception) {
                        "Ошибка парсинга: ${e.message}"
                    }
                } else {
                    "Ошибка HTTP ${it.code()}"
                }
                userInAppLiveData.postValue(DataState.Error(errorMessage))
            }
        }
    }
}