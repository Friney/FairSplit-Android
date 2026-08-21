package com.friney.fairsplit.ui.details.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.expense.ExpenseRepository
import com.friney.fairsplit.data.repository.expense.member.ExpenseMemberRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberCreate
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberUpdate
import com.friney.fairsplit.network.model.user.CreateNotRegisteredUser
import com.friney.fairsplit.network.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseMemberRepository: ExpenseMemberRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val expenseMembersLiveData = MutableLiveData<DataState<List<ExpenseMember>>>()
    val allUsersLiveData = MutableLiveData<DataState<List<User>>>()
    val createUserLiveData = MutableLiveData<DataState<User>>()
    val createExpenseMemberLiveData = MutableLiveData<DataState<ExpenseMember>>()
    val deleteExpenseMemberLiveData = MutableLiveData<DataState<Boolean>>()
    val deletesExpenseLiveData = MutableLiveData<DataState<Boolean>>()
    val updateExpenseMemberLiveData = MutableLiveData<DataState<ExpenseMember>>()
    private var _expenseId: Long? = null
    private var _receiptId: Long? = null
    private var selectedUserId: Long? = null

    fun init(expenseId: Long, receiptId: Long) {
        _receiptId = receiptId
        _expenseId = expenseId
        getAllExpenseMembers()
        getAllUsers()
    }

    fun selectUser(userId: Long) {
        selectedUserId = userId
    }

    fun createUser(name: String) {
        if (name.isBlank()) {
            createUserLiveData.value = DataState.Error("Пожалуйста, введите имя пользователя")
            return
        }

        viewModelScope.launch {
            createUserLiveData.value = DataState.Loading()
            val createUser = CreateNotRegisteredUser(name)
            val response = userRepository.create(createUser)

            if (response.isSuccessful) {
                createUserLiveData.value = DataState.Success(response.body()!!)
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
                createUserLiveData.value = DataState.Error(errorMessage)
            }
        }
    }

    fun createExpenseMember() {
        if (selectedUserId == null) {
            createExpenseMemberLiveData.value = DataState.Error("Пожалуйста, выберите пользователя")
            return
        }

        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                createExpenseMemberLiveData.value = DataState.Loading()
                try {
                    val expenseMemberCreate = ExpenseMemberCreate(selectedUserId!!)
                    val response = expenseMemberRepository.create(expenseMemberCreate, expenseId)

                    if (response.isSuccessful) {
                        createExpenseMemberLiveData.value = DataState.Success(response.body()!!)
                        // Обновляем список участников после создания
                        getAllExpenseMembers()
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
                        createExpenseMemberLiveData.value = DataState.Error(errorMessage)
                    }
                } catch (e: Exception) {
                    createExpenseMemberLiveData.value = DataState.Error("Ошибка сети: ${e.message}")
                }
            }
        }
    }

    fun deleteExpenseMember(expenseMemberId: Long) {
        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                deleteExpenseMemberLiveData.value = DataState.Loading()
                try {
                    expenseMemberRepository.delete(expenseMemberId, expenseId)
                    deleteExpenseMemberLiveData.value = DataState.Success(true)
                    // Обновляем список участников после удаления
                    getAllExpenseMembers()
                } catch (e: Exception) {
                    deleteExpenseMemberLiveData.value =
                        DataState.Error("Ошибка при удалении участника: ${e.message}")
                }
            }
        }
    }

    fun updateExpenseMember(expenseMemberId: Long, userId: Long) {
        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                updateExpenseMemberLiveData.value = DataState.Loading()
                try {
                    val expenseMemberUpdate = ExpenseMemberUpdate(userId)
                    val response = expenseMemberRepository.update(
                        expenseMemberUpdate,
                        expenseMemberId,
                        expenseId
                    )

                    if (response.isSuccessful) {
                        updateExpenseMemberLiveData.value = DataState.Success(response.body()!!)
                        // Обновляем список участников после обновления
                        getAllExpenseMembers()
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
                        updateExpenseMemberLiveData.value = DataState.Error(errorMessage)
                    }
                } catch (e: Exception) {
                    updateExpenseMemberLiveData.value = DataState.Error("Ошибка сети: ${e.message}")
                }
            }
        }
    }

    private fun getAllExpenseMembers() = viewModelScope.launch {
        _expenseId?.let { id ->
            expenseMembersLiveData.postValue(DataState.Loading())
            expenseMemberRepository.getAllByExpenseId(id).let {
                if (it.isSuccessful) {
                    expenseMembersLiveData.postValue(DataState.Success(it.body() as List<ExpenseMember>))
                } else {
                    val errorBody = it.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Parse error: ${e.message}"
                        }
                    } else {
                        "Empty error body (HTTP ${it.code()})"
                    }
                    expenseMembersLiveData.postValue(DataState.Error(errorMessage))
                }
            }
        }
    }

    private fun getAllUsers() = viewModelScope.launch {
        allUsersLiveData.postValue(DataState.Loading())
        try {
            val response = userRepository.getAllUser()
            if (response.isSuccessful) {
                allUsersLiveData.postValue(DataState.Success(response.body() as List<User>))
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
                allUsersLiveData.postValue(DataState.Error(errorMessage))
            }
        } catch (e: Exception) {
            allUsersLiveData.postValue(DataState.Error("Ошибка сети: ${e.message}"))
        }
    }

    fun deleteExpense() = viewModelScope.launch {
        _receiptId?.let { receiptId ->
            _expenseId?.let { expenseId ->
                deletesExpenseLiveData.postValue(DataState.Loading())
                try {
                    expenseRepository.delete(expenseId, receiptId)
                    deletesExpenseLiveData.postValue(DataState.Success(true))
                } catch (e: Exception) {
                    deletesExpenseLiveData.postValue(DataState.Error("Ошибка при удалении покупки: ${e.message}"))
                }
            }
        }
    }
}