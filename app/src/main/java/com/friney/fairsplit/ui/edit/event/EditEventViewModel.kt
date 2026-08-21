package com.friney.fairsplit.ui.edit.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.network.model.event.Event
import com.friney.fairsplit.network.model.event.EventUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val updateEventState = MutableLiveData<DataState<Event>>()

    fun updateEvent(eventId: Long, name: String, description: String) {
        if (name.isBlank() || description.isBlank()) {
            updateEventState.value = DataState.Error("Пожалуйста, заполните все поля")
            return
        }

        viewModelScope.launch {
            updateEventState.value = DataState.Loading()
            try {
                val response = eventRepository.update(EventUpdate(name, description), eventId)
                if (response.isSuccessful) {
                    updateEventState.value = DataState.Success(response.body()!!)
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
                    updateEventState.value = DataState.Error(errorMessage)
                }
            } catch (e: Exception) {
                updateEventState.value = DataState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
}
