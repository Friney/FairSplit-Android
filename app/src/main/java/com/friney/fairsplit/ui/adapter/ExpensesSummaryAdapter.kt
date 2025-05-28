package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemExpenseSummaryBinding
import com.friney.fairsplit.network.model.summary.SummaryExpense

class ExpensesSummaryAdapter : RecyclerView.Adapter<ExpensesSummaryAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemExpenseSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isExpanded = false

        init {
            binding.root.setOnClickListener {
                toggleExpanded()
            }
            binding.expandButton.setOnClickListener {
                toggleExpanded()
            }
        }

        private fun toggleExpanded() {
            isExpanded = !isExpanded
            binding.participantsRecyclerView.visibility =
                if (isExpanded) View.VISIBLE else View.GONE
            binding.expandButton.rotation = if (isExpanded) 180f else 0f
        }
    }

    private val callback = object : DiffUtil.ItemCallback<SummaryExpense>() {
        override fun areItemsTheSame(oldItem: SummaryExpense, newItem: SummaryExpense): Boolean {
            return oldItem.name == newItem.name && oldItem.total == newItem.total
        }

        override fun areContentsTheSame(oldItem: SummaryExpense, newItem: SummaryExpense): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemExpenseSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FairSplitViewHolder, position: Int) {
        val expense = differ.currentList[position]
        holder.binding.apply {
            expenseName.text = expense.name
            expenseTotal.text = expense.total.toString()

            // Настройка RecyclerView для участников
            participantsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ParticipantsSummaryAdapter().apply {
                    differ.submitList(expense.payerInfos)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
} 