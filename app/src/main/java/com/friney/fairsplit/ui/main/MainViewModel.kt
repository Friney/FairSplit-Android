package com.friney.fairsplit.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.network.model.Event
import com.friney.fairsplit.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _user = MutableLiveData<List<User>>()
    val user: LiveData<List<User>>
        get() = _user

    private val _event = MutableLiveData<List<Event>>()
    val event: LiveData<List<Event>>
        get() = _event

    init {
        getAllUser()
        getAllEvent()
    }

    private fun getAllEvent() = viewModelScope.launch {
        eventRepository.getAllEvent().let {
            if (it.isSuccessful) {
                _event.postValue(it.body())
            } else {
                Log.d("Error", it.errorBody().toString())
            }
        }
    }

    fun getAllUser() = viewModelScope.launch {
        userRepository.getAllUser().let {
            if (it.isSuccessful) {
                _user.postValue(it.body())
            } else {
                Log.d("Error", it.errorBody().toString())
            }
        }
    }
}