package com.friney.fairsplit.ui.create.expense

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentCreateExpenseBinding
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class CreateExpenseFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentCreateExpenseBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: CreateExpenseViewModel by viewModels()
    private val bundleArgs: CreateExpenseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateExpenseBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backButton.setOnClickListener {
            fragmentNavigator.navigateBack()
        }

        mBinding.createButton.setOnClickListener {
            Log.e("Create expense", "Create expense")
            val name = mBinding.nameInput.text.toString()
            val amountStr = mBinding.amountInput.text.toString()
            
            try {
                val amount = BigDecimal(amountStr)
                val receiptId = bundleArgs.receiptId
                viewModel.createExpense(name, amount, receiptId)
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    context,
                    "Пожалуйста, введите корректную сумму",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Наблюдатель за созданием покупки
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createExpenseState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is DataState.Success -> {
                        mBinding.progressBar.visibility = View.INVISIBLE
                        mBinding.createButton.isEnabled = true
                        Toast.makeText(
                            context,
                            "Покупка успешно создана!",
                            Toast.LENGTH_SHORT
                        ).show()
                        fragmentNavigator.navigateBack()
                    }

                    is DataState.Error -> {
                        mBinding.progressBar.visibility = View.INVISIBLE
                        mBinding.createButton.isEnabled = true
                        Toast.makeText(
                            context,
                            state.message ?: "Неизвестная ошибка",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is DataState.Loading -> {
                        Log.e("Create expenseLoading", "Create expenseLoading")
                        mBinding.createButton.isEnabled = false
                        mBinding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}