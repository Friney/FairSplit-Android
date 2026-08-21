package com.friney.fairsplit.ui.details.receipt

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.R
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentDetailsReceiptBinding
import com.friney.fairsplit.ui.adapter.ExpenseAdapter
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class DetailsReceiptFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentDetailsReceiptBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<DetailsReceiptViewModel>()
    private val bundleArgs: DetailsReceiptFragmentArgs by navArgs()
    lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsReceiptBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        fragmentNavigator.setNavController(findNavController())

        val receiptArg = bundleArgs.receipt
        val eventId = bundleArgs.eventId
        receiptArg.let { receipt ->
            val amount = receipt.expenses
                .map { it.amount }
                .fold(BigDecimal.ZERO) { acc, current -> acc.add(current) }
            mBinding.receiptName.text = receipt.name
            mBinding.receiptPayer.text = receipt.paidByUser.name
            mBinding.receiptTotal.text = DecimalFormat("#,##0.00").format(amount)
            viewModel.init(receipt.id, eventId)
        }

        mBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

        mBinding.addExpenseButton.setOnClickListener {
            val receiptId = bundleArgs.receipt.id
            val bundle = bundleOf("receiptId" to receiptId)
            findNavController().navigate(
                R.id.action_detailsReceiptFragment_to_createExpenseFragment,
                bundle
            )
        }

        expenseAdapter.setOnItemClickListener {
            val receiptId = bundleArgs.receipt.id
            val bundle = bundleOf("expense" to it, "receiptId" to receiptId)
            view.findNavController().navigate(
                R.id.action_detailsReceiptFragment_to_detailsExpenseFragment,
                bundle
            )
        }

        viewModel.expensesLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        expenseAdapter.differ.submitList(it)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get all expense: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.deletesReceiptLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }

                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.GONE
                    fragmentNavigator.navigateBack()
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initAdapter() {
        expenseAdapter = ExpenseAdapter()
        mBinding.expenseRecyclerView.apply {
            adapter = expenseAdapter
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
                    showDeleteConfirmationDialog()
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление чека")
            .setMessage("Вы точно уверены, что хотите удалить этот чек? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteReceipt()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}