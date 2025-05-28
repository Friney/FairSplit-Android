package com.friney.fairsplit.network.model

import java.io.Serializable
import java.math.BigDecimal

data class Expense(
    val id: Long,
    val amount: BigDecimal,
    val expenseMembers: List<ExpenseMember>,
    val name: String
) : Serializable