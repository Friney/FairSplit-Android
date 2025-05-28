package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemExpenseBinding
import com.friney.fairsplit.network.model.Expense
import java.text.DecimalFormat

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(
            oldItem: Expense,
            newItem: Expense
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Expense,
            newItem: Expense
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FairSplitViewHolder,
        position: Int
    ) {
        val expense = differ.currentList[position]
        holder.binding.apply {
            expenseName.text = expense.name
            expensePrice.text = DecimalFormat("#,##0.00").format(expense.amount)
            root.setOnClickListener {
                onItemClickListener?.let { it(expense) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Expense) -> Unit)? = null

    fun setOnItemClickListener(listener: (Expense) -> Unit) {
        onItemClickListener = listener
    }
} 