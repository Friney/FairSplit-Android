package com.friney.fairsplit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friney.fairsplit.databinding.ItemExpenseMemberBinding
import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import java.math.BigDecimal
import java.text.DecimalFormat

class ExpenseMemberAdapter : RecyclerView.Adapter<ExpenseMemberAdapter.FairSplitViewHolder>() {

    inner class FairSplitViewHolder(val binding: ItemExpenseMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<ExpenseMember>() {
        override fun areItemsTheSame(
            oldItem: ExpenseMember,
            newItem: ExpenseMember
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ExpenseMember,
            newItem: ExpenseMember
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var _amountByOnePerson: BigDecimal = BigDecimal.ZERO

    fun setAmountByOnePerson(amountByOnePerson: BigDecimal) {
        _amountByOnePerson = amountByOnePerson
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FairSplitViewHolder {
        return FairSplitViewHolder(
            ItemExpenseMemberBinding.inflate(
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
            memberName.text = expense.user.name
            memberAmount.text = DecimalFormat("#,##0.00").format(_amountByOnePerson)
            
            root.setOnClickListener {
                onItemClickListener?.let { it(expense) }
            }
            
            root.setOnLongClickListener {
                onItemLongClickListener?.let { it(expense) }
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((ExpenseMember) -> Unit)? = null
    private var onItemLongClickListener: ((ExpenseMember) -> Unit)? = null

    fun setOnItemClickListener(listener: (ExpenseMember) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (ExpenseMember) -> Unit) {
        onItemLongClickListener = listener
    }
} 