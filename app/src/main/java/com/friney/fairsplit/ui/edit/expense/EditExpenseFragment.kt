package com.friney.fairsplit.ui.edit.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentEditExpenseBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class EditExpenseFragment : Fragment() {

    private var _binding: FragmentEditExpenseBinding? = null
    private val mBinding get() = _binding!!
    private val args: EditExpenseFragmentArgs by navArgs()
    private val viewModel: EditExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditExpenseBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expense = args.expense
        mBinding.nameInput.setText(expense.name)
        mBinding.amountInput.setText(expense.amount.toPlainString())

        mBinding.backButton.setOnClickListener { findNavController().navigateUp() }

        mBinding.createButton.setOnClickListener {
            val name = mBinding.nameInput.text.toString().trim()
            val amountText = mBinding.amountInput.text.toString().trim()
            val amount = try {
                BigDecimal(amountText)
            } catch (_: NumberFormatException) {
                Toast.makeText(context, "Пожалуйста, введите корректную сумму", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.updateExpense(expense.id, args.receiptId, name, amount)
        }

        viewModel.updateExpenseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    mBinding.createButton.isEnabled = true
                    Toast.makeText(context, "Покупка успешно обновлена!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    mBinding.createButton.isEnabled = true
                    Toast.makeText(context, state.message ?: "Неизвестная ошибка", Toast.LENGTH_LONG).show()
                }
                is DataState.Loading -> {
                    mBinding.createButton.isEnabled = false
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
