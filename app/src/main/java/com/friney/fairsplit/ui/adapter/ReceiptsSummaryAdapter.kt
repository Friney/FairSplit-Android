package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemReceiptSummaryBinding
import com.friney.fairsplit.network.model.summary.SummaryReceipt

class ReceiptsSummaryAdapter : RecyclerView.Adapter<ReceiptsSummaryAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemReceiptSummaryBinding) :
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
            binding.expandedContent.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandButton.rotation = if (isExpanded) 180f else 0f
        }
    }

    private val callback = object : DiffUtil.ItemCallback<SummaryReceipt>() {
        override fun areItemsTheSame(oldItem: SummaryReceipt, newItem: SummaryReceipt): Boolean {
            return oldItem.total == newItem.total && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SummaryReceipt, newItem: SummaryReceipt): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemReceiptSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FairSplitViewHolder, position: Int) {
        val receipt = differ.currentList[position]
        holder.binding.apply {
            receiptName.text = receipt.name
            receiptTotal.text = receipt.total.toString()

            // Настройка RecyclerView для участников
            participantsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ParticipantsSummaryAdapter().apply {
                    differ.submitList(receipt.payerInfos)
                }
            }

            // Настройка RecyclerView для покупок
            expensesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExpensesSummaryAdapter().apply {
                    differ.submitList(receipt.expenses)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
} 