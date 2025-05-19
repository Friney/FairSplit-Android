package com.friney.fairsplit.data.repository.user

import com.friney.fairsplit.network.model.User
import retrofit2.Response

interface UserRepository {

    suspend fun getAllUser(): Response<List<User>>
}