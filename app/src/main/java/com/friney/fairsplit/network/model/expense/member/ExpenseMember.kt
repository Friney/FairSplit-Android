package com.friney.fairsplit.network.model.expense.member

import com.friney.fairsplit.network.model.user.User

data class ExpenseMember(
    val id: Long,
    val user: User
)