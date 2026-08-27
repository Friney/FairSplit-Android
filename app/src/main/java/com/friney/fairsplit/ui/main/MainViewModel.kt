package com.friney.fairsplit.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.event.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val eventLiveData = MutableLiveData<DataState<List<Event>>>()

    init {
        getAllEvent()
    }

    fun refreshEvents() {
        getAllEvent()
    }

    private fun getAllEvent() = viewModelScope.launch {
        eventLiveData.postValue(DataState.Loading())
        eventRepository.getAll().let {
            if (it.isSuccessful) {
                eventLiveData.postValue(DataState.Success(it.body() as List<Event>))
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
                eventLiveData.postValue(DataState.Error(errorMessage))
            }
        }
    }
}