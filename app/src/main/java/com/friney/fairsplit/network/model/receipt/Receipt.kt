package com.friney.fairsplit.network.model.receipt

import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.user.User
import java.io.Serializable

data class Receipt(
    val id: Long,
    val expenses: List<Expense>,
    val name: String,
    val paidByUser: User
) : Serializable