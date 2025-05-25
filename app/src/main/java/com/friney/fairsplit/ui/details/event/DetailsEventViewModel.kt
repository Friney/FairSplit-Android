package com.friney.fairsplit.ui.details.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.Receipt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsEventViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    val receiptsLiveData = MutableLiveData<DataState<List<Receipt>>>()
    private var _eventId: Long? = null

    fun init(eventId: Long) {
        _eventId = eventId
        getAllReceipt()
    }

    private fun getAllReceipt() = viewModelScope.launch {
        _eventId?.let { id ->
            receiptsLiveData.postValue(DataState.Loading())
            receiptRepository.getAllByEventId(id).let {
                if (it.isSuccessful) {
                    receiptsLiveData.postValue(DataState.Success(it.body() as List<Receipt>))
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
                    receiptsLiveData.postValue(DataState.Error(errorMessage))
                }
            }
        }
    }
}