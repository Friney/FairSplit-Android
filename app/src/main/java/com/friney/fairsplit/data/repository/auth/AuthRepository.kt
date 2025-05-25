package com.friney.fairsplit.data.repository.auth

import com.friney.fairsplit.network.model.RegisteredUser
import retrofit2.Response

interface AuthRepository {
    fun getUserInApp(): Response<RegisteredUser>
}