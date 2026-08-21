package com.friney.fairsplit.ui.edit.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.expense.ExpenseRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class EditExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val updateExpenseState = MutableLiveData<DataState<Expense>>()

    fun updateExpense(expenseId: Long, receiptId: Long, name: String, amount: BigDecimal) {
        if (name.isBlank()) {
            updateExpenseState.value = DataState.Error("Пожалуйста, заполните название покупки")
            return
        }

        if (amount <= BigDecimal.ZERO) {
            updateExpenseState.value = DataState.Error("Сумма должна быть больше нуля")
            return
        }

        viewModelScope.launch {
            updateExpenseState.value = DataState.Loading()
            try {
                val response = expenseRepository.update(ExpenseUpdate(amount, name), expenseId, receiptId)
                if (response.isSuccessful) {
                    updateExpenseState.value = DataState.Success(response.body()!!)
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
                    updateExpenseState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                updateExpenseState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
}
