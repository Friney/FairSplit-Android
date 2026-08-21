package com.friney.fairsplit.network.model.user

data class RegisteredUser(
    var id: Long,
    val name: String,
    val email: String,
    val displayName: String
)