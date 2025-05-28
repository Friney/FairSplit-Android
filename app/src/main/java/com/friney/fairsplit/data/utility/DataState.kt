package com.friney.fairsplit.data.utility

sealed class DataState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : DataState<T>(data = data)
    class Error<T>(message: String) : DataState<T>(message = message)
    class Loading<T> : DataState<T>()
}