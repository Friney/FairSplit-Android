package com.friney.fairsplit.ui.create.receipt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.auth.AuthRepository
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptCreate
import com.friney.fairsplit.network.model.user.CreateNotRegisteredUser
import com.friney.fairsplit.network.model.user.RegisteredUser
import com.friney.fairsplit.network.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CreateReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val createReceiptState = MutableLiveData<DataState<Receipt>>()
    val currentUserState = MutableLiveData<DataState<RegisteredUser>>()
    val allUsersState = MutableLiveData<DataState<List<User>>>()
    val createUserState = MutableLiveData<DataState<User>>()
    private var selectedUserId: Long? = null

    init {
        getCurrentUser()
        getAllUsers()
    }

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
            val createUser = CreateNotRegisteredUser(name)
            val response = userRepository.createUser(createUser)

            if (response.isSuccessful) {
                createUserState.value = DataState.Success(response.body()!!)
                // Обновляем список пользователей после создания
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
        }
    }

    fun createReceipt(name: String, eventId: Long) {
        if (name.isBlank()) {
            createReceiptState.value = DataState.Error("Пожалуйста, заполните название чека")
            return
        }

        if (selectedUserId == null) {
            createReceiptState.value = DataState.Error("Пожалуйста, выберите пользователя")
            return
        }

        viewModelScope.launch {
            createReceiptState.value = DataState.Loading()
            try {
                val receiptCreate = ReceiptCreate(name, selectedUserId!!)
                val response = receiptRepository.create(receiptCreate, eventId)

                if (response.isSuccessful) {
                    createReceiptState.value = DataState.Success(response.body()!!)
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
                    createReceiptState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                createReceiptState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    private fun getAllUsers() = viewModelScope.launch {
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

    private fun getCurrentUser() = viewModelScope.launch {
        currentUserState.postValue(DataState.Loading())
        try {
            val response = authRepository.getCurrentUser()
            if (response.isSuccessful) {
                currentUserState.postValue(DataState.Success(response.body() as RegisteredUser))
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
                currentUserState.postValue(DataState.Error(errorMessage))
            }
        } catch (e: Exception) {
            currentUserState.postValue(DataState.Error("Ошибка сети: ${e.message}"))
        }
    }
} 