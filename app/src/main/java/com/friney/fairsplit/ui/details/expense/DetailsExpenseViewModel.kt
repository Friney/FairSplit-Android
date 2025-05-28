package com.friney.fairsplit.ui.details.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.expense.member.ExpenseMemberRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.ExpenseMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsExpenseViewModel @Inject constructor(
    private val expenseMemberRepository: ExpenseMemberRepository
) : ViewModel() {
    val expenseMembersLiveData = MutableLiveData<DataState<List<ExpenseMember>>>()
    private var _expenseId: Long? = null

    fun init(expenseId: Long) {
        _expenseId = expenseId
        getAllExpenseMembers()
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
}