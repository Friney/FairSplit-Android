package com.friney.fairsplit.ui.details.expense

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.R
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentDetailsExpenseBinding
import com.friney.fairsplit.ui.adapter.ExpenseMemberAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.DecimalFormat

@AndroidEntryPoint
class DetailsExpenseFragment : Fragment() {

    private var _binding: FragmentDetailsExpenseBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<DetailsExpenseViewModel>()
    private val bundleArgs: DetailsExpenseFragmentArgs by navArgs()
    lateinit var expenseMemberAdapter: ExpenseMemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsExpenseBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        val expenseArg = bundleArgs.expense
        expenseArg.let { expense ->
            mBinding.expenseName.text = expense.name
            mBinding.expensePrice.text = DecimalFormat("#,##0.00").format(expense.amount)
            viewModel.init(expense.id)
        }

        mBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
        var size = bundleArgs.expense.expenseMembers.size
        if (size == 0) {
            size = 1
        }
        val amountByOnePerson = bundleArgs.expense.amount.divide(BigDecimal(size), 2)
        viewModel.expenseMembersLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        expenseMemberAdapter.setAmountByOnePerson(amountByOnePerson)
                        expenseMemberAdapter.differ.submitList(it)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get all expense member: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initAdapter() {
        expenseMemberAdapter = ExpenseMemberAdapter()
        mBinding.membersRecyclerView.apply {
            adapter = expenseMemberAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view, Gravity.END)
        popup.menuInflater.inflate(R.menu.details_event_menu, popup.menu)

        popup.setForceShowIcon(true)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit -> {
                    // Действие при редактировании
                    true
                }

                R.id.menu_delete -> {
                    // Действие при удалении
                    true
                }

                else -> false
            }
        }

        popup.show()
    }
}