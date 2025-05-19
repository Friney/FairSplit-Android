package com.friney.fairsplit.network.model

data class Expense(
    val id: Long,
    val amount: Double,
    val expenseMembers: List<ExpenseMember>,
    val name: String
)