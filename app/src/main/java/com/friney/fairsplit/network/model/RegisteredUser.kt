package com.friney.fairsplit.network.model

data class RegisteredUser(
    var id: Long,
    val name: String,
    val email: String,
    val displayName: String
)