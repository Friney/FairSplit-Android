package com.friney.fairsplit.ui.edit.receipt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptUpdate
import com.friney.fairsplit.network.model.user.CreateNotRegisteredUser
import com.friney.fairsplit.network.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val updateReceiptState = MutableLiveData<DataState<Receipt>>()
    val allUsersState = MutableLiveData<DataState<List<User>>>()
    val createUserState = MutableLiveData<DataState<User>>()
    private var selectedUserId: Long? = null

    fun selectUser(userId: Long) {
        selectedUserId = userId
    }

    fun createUser(name: String) {
        if (name.isBlank()) {
            createUserState.value = DataState.Error("Пожалуйста, введите имя пользователя")
            return
        }

        viewModelScope.launch {
            createUserState.value = DataState.Loading()
            try {
                val response = userRepository.create(CreateNotRegisteredUser(name))
                if (response.isSuccessful) {
                    createUserState.value = DataState.Success(response.body()!!)
                    getAllUsers()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Parse error: ${e.message}"
                        }
                    } else {
                        "Empty error body (HTTP ${response.code()})"
                    }
                    createUserState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                createUserState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun getAllUsers() = viewModelScope.launch {
        allUsersState.postValue(DataState.Loading())
        try {
            val response = userRepository.getAllUser()
            if (response.isSuccessful) {
                allUsersState.postValue(DataState.Success(response.body() as List<User>))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        JSONObject(errorBody).getString("message")
                    } catch (e: Exception) {
                        "Parse error: ${e.message}"
                    }
                } else {
                    "Empty error body (HTTP ${response.code()})"
                }
                allUsersState.postValue(DataState.Error(errorMessage))
            }
        } catch (e: Exception) {
            allUsersState.postValue(DataState.Error("Ошибка сети: ${e.message}"))
        }
    }

    fun updateReceipt(receiptId: Long, eventId: Long, name: String) {
        if (name.isBlank()) {
            updateReceiptState.value = DataState.Error("Пожалуйста, заполните название чека")
            return
        }

        if (selectedUserId == null) {
            updateReceiptState.value = DataState.Error("Пожалуйста, выберите пользователя")
            return
        }

        viewModelScope.launch {
            updateReceiptState.value = DataState.Loading()
            try {
                val response = receiptRepository.update(ReceiptUpdate(name, selectedUserId!!), receiptId, eventId)
                if (response.isSuccessful) {
                    updateReceiptState.value = DataState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Parse error: ${e.message}"
                        }
                    } else {
                        "Empty error body (HTTP ${response.code()})"
                    }
                    updateReceiptState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                updateReceiptState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
}
