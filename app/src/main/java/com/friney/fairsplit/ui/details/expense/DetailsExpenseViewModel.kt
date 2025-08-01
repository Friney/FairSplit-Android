package com.friney.fairsplit.ui.details.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val expenseMemberRepository: ExpenseMemberRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val expenseMembersLiveData = MutableLiveData<DataState<List<ExpenseMember>>>()
    val allUsersState = MutableLiveData<DataState<List<User>>>()
    val createUserState = MutableLiveData<DataState<User>>()
    val createExpenseMemberState = MutableLiveData<DataState<ExpenseMember>>()
    val deleteExpenseMemberState = MutableLiveData<DataState<Unit>>()
    val updateExpenseMemberState = MutableLiveData<DataState<ExpenseMember>>()
    private var _expenseId: Long? = null
    private var selectedUserId: Long? = null

    fun init(expenseId: Long) {
        _expenseId = expenseId
        getAllExpenseMembers()
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

    fun createExpenseMember() {
        if (selectedUserId == null) {
            createExpenseMemberState.value = DataState.Error("Пожалуйста, выберите пользователя")
            return
        }

        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                createExpenseMemberState.value = DataState.Loading()
                try {
                    val expenseMemberCreate = ExpenseMemberCreate(selectedUserId!!)
                    val response = expenseMemberRepository.create(expenseMemberCreate, expenseId)

                    if (response.isSuccessful) {
                        createExpenseMemberState.value = DataState.Success(response.body()!!)
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
                        createExpenseMemberState.value = DataState.Error(errorMessage)
                    }
                } catch (e: Exception) {
                    createExpenseMemberState.value = DataState.Error("Ошибка сети: ${e.message}")
                }
            }
        }
    }

    fun deleteExpenseMember(expenseMemberId: Long) {
        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                deleteExpenseMemberState.value = DataState.Loading()
                try {
                    expenseMemberRepository.delete(expenseMemberId, expenseId)
                    deleteExpenseMemberState.value = DataState.Success(Unit)
                    // Обновляем список участников после удаления
                    getAllExpenseMembers()
                } catch (e: Exception) {
                    deleteExpenseMemberState.value =
                        DataState.Error("Ошибка при удалении участника: ${e.message}")
                }
            }
        }
    }

    fun updateExpenseMember(expenseMemberId: Long, userId: Long) {
        _expenseId?.let { expenseId ->
            viewModelScope.launch {
                updateExpenseMemberState.value = DataState.Loading()
                try {
                    val expenseMemberUpdate = ExpenseMemberUpdate(userId)
                    val response = expenseMemberRepository.update(
                        expenseMemberId,
                        expenseId,
                        expenseMemberUpdate
                    )

                    if (response.isSuccessful) {
                        updateExpenseMemberState.value = DataState.Success(response.body()!!)
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
                        updateExpenseMemberState.value = DataState.Error(errorMessage)
                    }
                } catch (e: Exception) {
                    updateExpenseMemberState.value = DataState.Error("Ошибка сети: ${e.message}")
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
}