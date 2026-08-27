package com.friney.fairsplit.ui.details.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.repository.summary.SummaryRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.event.Event
import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.summary.Summary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsEventViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val summaryRepository: SummaryRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val receiptsLiveData = MutableLiveData<DataState<List<Receipt>>>()
    val summaryLiveData = MutableLiveData<DataState<Summary>>()
    val deleteEventLiveData = MutableLiveData<DataState<Boolean>>()
    val updateEventLiveData = MutableLiveData<DataState<Event>>()
    private var _eventId: Long? = null

    fun getEventId(): Long? = _eventId

    fun init(eventId: Long) {
        _eventId = eventId
        getAllReceipt()
        getSummary()
    }

    fun deleteEvent() = viewModelScope.launch {
        _eventId?.let { id ->
            deleteEventLiveData.postValue(DataState.Loading())
            try {
                eventRepository.delete(id)
                deleteEventLiveData.postValue(DataState.Success(true))
            } catch (e: Exception) {
                deleteEventLiveData.postValue(DataState.Error("Ошибка при удалении события: ${e.message}"))
            }
        }
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
                    summaryLiveData.postValue(DataState.Error(errorMessage))
                }
            }
        }
    }

    private fun getSummary() = viewModelScope.launch {
        _eventId?.let { id ->
            summaryLiveData.postValue(DataState.Loading())
            summaryRepository.getSummaryByEventId(id).let {
                if (it.isSuccessful) {
                    summaryLiveData.postValue(DataState.Success(it.body() as Summary))
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
