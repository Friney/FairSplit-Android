package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemParticipantSummaryBinding
import com.friney.fairsplit.network.model.summary.PayerInfo

class ParticipantsSummaryAdapter :
    RecyclerView.Adapter<ParticipantsSummaryAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemParticipantSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<PayerInfo>() {
        override fun areItemsTheSame(oldItem: PayerInfo, newItem: PayerInfo): Boolean {
            return oldItem.user.id == newItem.user.id
        }

        override fun areContentsTheSame(oldItem: PayerInfo, newItem: PayerInfo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemParticipantSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FairSplitViewHolder, position: Int) {
        val payerInfo = differ.currentList[position]
        holder.binding.apply {
            participantName.text = payerInfo.user.name
            participantTotal.text = payerInfo.total.toString()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
} 