package com.friney.fairsplit.network.model

data class Receipt(
    val id: Long,
    val expenses: List<Expense>,
    val name: String,
    val paidByUser: User
)