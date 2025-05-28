package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemDebtBinding
import com.friney.fairsplit.network.model.summary.Debt

class DebtsAdapter : RecyclerView.Adapter<DebtsAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemDebtBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Debt>() {
        override fun areItemsTheSame(oldItem: Debt, newItem: Debt): Boolean {
            return oldItem.from.id == newItem.from.id &&
                    oldItem.to.id == newItem.to.id
        }

        override fun areContentsTheSame(oldItem: Debt, newItem: Debt): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemDebtBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FairSplitViewHolder, position: Int) {
        val debt = differ.currentList[position]
        holder.binding.apply {
            fromName.text = debt.from.name
            toName.text = debt.to.name
            amount.text = debt.amount.toString()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
} 