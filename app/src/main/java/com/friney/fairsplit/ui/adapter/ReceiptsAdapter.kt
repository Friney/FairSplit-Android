package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemReceiptBinding
import com.friney.fairsplit.network.model.receipt.Receipt
import java.math.BigDecimal
import java.text.DecimalFormat

class ReceiptsAdapter : RecyclerView.Adapter<ReceiptsAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Receipt>() {
        override fun areItemsTheSame(
            oldItem: Receipt,
            newItem: Receipt
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Receipt,
            newItem: Receipt
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
            ItemReceiptBinding.inflate(
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
        val receipt = differ.currentList[position]
        val amount = receipt.expenses
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, current -> acc.add(current) }

        holder.binding.apply {
            receiptName.text = receipt.name
            receiptPayer.text = receipt.paidByUser.name
            receiptTotal.text = DecimalFormat("#,##0.00").format(amount)
            root.setOnClickListener {
                onItemClickListener?.let { it(receipt) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Receipt) -> Unit)? = null

    fun setOnItemClickListener(listener: (Receipt) -> Unit) {
        onItemClickListener = listener
    }
}
