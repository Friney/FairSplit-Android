package com.friney.fairsplit.ui.details.receipt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.expense.ExpenseRepository
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.expense.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val expensesLiveData = MutableLiveData<DataState<List<Expense>>>()
    val deletesReceiptLiveData = MutableLiveData<DataState<Boolean>>()
    private var _receiptId: Long? = null
    private var _eventId: Long? = null

    fun init(receiptId: Long, eventId: Long) {
        _receiptId = receiptId
        _eventId = eventId
        getAllExpenses()
    }

    private fun getAllExpenses() = viewModelScope.launch {
        _receiptId?.let { id ->
            expensesLiveData.postValue(DataState.Loading())
            expenseRepository.getAllByReceiptId(id).let {
                if (it.isSuccessful) {
                    expensesLiveData.postValue(DataState.Success(it.body() as List<Expense>))
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
                    expensesLiveData.postValue(DataState.Error(errorMessage))
                }
            }
        }
    }

    fun deleteReceipt() = viewModelScope.launch {
        _eventId?.let { eventId ->
            _receiptId?.let { receiptId ->
                deletesReceiptLiveData.postValue(DataState.Loading())
                try {
                    receiptRepository.delete(receiptId, eventId)
                    deletesReceiptLiveData.postValue(DataState.Success(true))
                } catch (e: Exception) {
                    deletesReceiptLiveData.postValue(DataState.Error("Ошибка при удалении чека: ${e.message}"))
                }
            }
        }
    }
} 