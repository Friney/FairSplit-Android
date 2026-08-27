package com.friney.fairsplit.ui.create.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.event.Event
import com.friney.fairsplit.network.model.event.EventCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val createEventState = MutableLiveData<DataState<Event>>()

    fun createEvent(name: String, description: String) {
        if (name.isBlank() || description.isBlank()) {
            createEventState.value = DataState.Error("Пожалуйста, заполните все поля")
            return
        }

        viewModelScope.launch {
            createEventState.value = DataState.Loading()
            val response = eventRepository.create(EventCreate(name, description))
            if (response.isSuccessful) {
                createEventState.value = DataState.Success(response.body()!!)
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
                createEventState.value = DataState.Error(errorMessage)
            }
        }
    }
}
