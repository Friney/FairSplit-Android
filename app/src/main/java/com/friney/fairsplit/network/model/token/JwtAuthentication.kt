package com.friney.fairsplit.network.model.token

data class JwtAuthentication(
    val token: String,
    val refreshToken: String
) 