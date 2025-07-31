package com.friney.fairsplit.network.model
 
data class JwtAuthentication(
    val token: String,
    val refreshToken: String
) 