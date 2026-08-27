package com.friney.fairsplit.network.refresher

interface TokenRefresher {
    suspend fun refreshToken(): Boolean
}