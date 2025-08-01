package com.friney.fairsplit.network.model.user

data class CreateRegisteredUser(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
) 