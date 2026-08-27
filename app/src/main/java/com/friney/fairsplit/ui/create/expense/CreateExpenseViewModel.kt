package com.friney.fairsplit.ui.create.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.expense.ExpenseRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val createExpenseState = MutableLiveData<DataState<Expense>>()

    fun createExpense(name: String, amount: BigDecimal, receiptId: Long) {
        if (name.isBlank()) {
            createExpenseState.value = DataState.Error("Пожалуйста, заполните название покупки")
            return
        }

        if (amount <= BigDecimal.ZERO) {
            createExpenseState.value = DataState.Error("Сумма должна быть больше нуля")
            return
        }

        viewModelScope.launch {
            createExpenseState.value = DataState.Loading()
            try {
                val expenseCreate = ExpenseCreate(amount, name)
                val response = expenseRepository.create(expenseCreate, receiptId)
                
                if (response.isSuccessful) {
                    createExpenseState.value = DataState.Success(response.body()!!)
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
                    createExpenseState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                createExpenseState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
} 