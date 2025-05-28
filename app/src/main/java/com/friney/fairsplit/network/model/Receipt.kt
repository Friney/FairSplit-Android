package com.friney.fairsplit.network.model

import java.io.Serializable

data class Receipt(
    val id: Long,
    val expenses: List<Expense>,
    val name: String,
    val paidByUser: User
) : Serializable