package com.friney.fairsplit.ui.details.expense

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.R
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentDetailsExpenseBinding
import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import com.friney.fairsplit.network.model.user.User
import com.friney.fairsplit.ui.adapter.ExpenseMemberAdapter
import com.friney.fairsplit.ui.adapter.UsersAdapter
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class DetailsExpenseFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentDetailsExpenseBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<DetailsExpenseViewModel>()
    private val bundleArgs: DetailsExpenseFragmentArgs by navArgs()
    lateinit var expenseMemberAdapter: ExpenseMemberAdapter
    private lateinit var usersAdapter: UsersAdapter
    private var allUsers: List<User> = emptyList()
    private var currentEditingExpenseMemberId: Long? = null
    private var currentEditingExpenseMemberUserId: Long? = null
    private lateinit var currentExpense: Expense
    private var currentExpenseAmount: BigDecimal = BigDecimal.ZERO

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
        initUsersAdapter()

        fragmentNavigator.setNavController(findNavController())

        val expenseArg = bundleArgs.expense
        val receiptId = bundleArgs.receiptId
        currentExpense = expenseArg
        currentExpenseAmount = currentExpense.amount
        mBinding.expenseName.text = currentExpense.name
        mBinding.expensePrice.text = DecimalFormat("#,##0.00").format(currentExpenseAmount)
        viewModel.init(currentExpense.id, receiptId)

        mBinding.backButton.setOnClickListener {
            fragmentNavigator.navigateBack()
        }

        mBinding.menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

        mBinding.addMemberButton.setOnClickListener {
            mBinding.usersOverlay.visibility = View.VISIBLE
            mBinding.addMemberButton.visibility = View.GONE
            mBinding.userSearch.setText("")
            usersAdapter.differ.submitList(allUsers)
        }

        mBinding.closeUsersOverlay.setOnClickListener {
            mBinding.usersOverlay.visibility = View.GONE
            mBinding.addMemberButton.visibility = View.VISIBLE
        }

        mBinding.addUserButton.setOnClickListener {
            mBinding.createUserDialog.visibility = View.VISIBLE
            mBinding.newUserNameInput.setText("")
        }

        mBinding.userSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                val filtered = allUsers.filter { it.displayName.lowercase().contains(query) }
                usersAdapter.differ.submitList(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        usersAdapter.setOnItemClickListener { user ->
            viewModel.selectUser(user.id)

            if (currentEditingExpenseMemberId != null) {
                if (currentEditingExpenseMemberUserId == user.id) {
                    Toast.makeText(
                        context,
                        "Выбран тот же пользователь",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.updateExpenseMember(currentEditingExpenseMemberId!!, user.id)
                }
                currentEditingExpenseMemberId = null
                currentEditingExpenseMemberUserId = null
            } else {
                viewModel.createExpenseMember()
            }

            mBinding.usersOverlay.visibility = View.GONE
            mBinding.addMemberButton.visibility = View.VISIBLE
        }

        mBinding.cancelCreateUser.setOnClickListener {
            mBinding.createUserDialog.visibility = View.GONE
        }

        mBinding.confirmCreateUser.setOnClickListener {
            val userName = mBinding.newUserNameInput.text.toString()
            viewModel.createUser(userName)
        }

        viewModel.deletesExpenseLiveData.observe(viewLifecycleOwner) { state ->
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

        viewModel.expenseMembersLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let { expenseMembers ->
                        val size = if (expenseMembers.isEmpty()) 1 else expenseMembers.size
                        val amountByOnePerson =
                            currentExpenseAmount.divide(BigDecimal(size), 2, RoundingMode.HALF_UP)
                        expenseMemberAdapter.setAmountByOnePerson(amountByOnePerson)
                        expenseMemberAdapter.differ.submitList(expenseMembers)
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

        viewModel.allUsersLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    allUsers = state.data ?: emptyList()
                    usersAdapter.differ.submitList(allUsers)
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Ошибка загрузки пользователей: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.createUserLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.createUserProgressBar.visibility = View.INVISIBLE
                    mBinding.confirmCreateUser.isEnabled = true
                    mBinding.createUserDialog.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Пользователь успешно создан!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DataState.Error -> {
                    mBinding.createUserProgressBar.visibility = View.INVISIBLE
                    mBinding.confirmCreateUser.isEnabled = true
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.createUserProgressBar.visibility = View.VISIBLE
                    mBinding.confirmCreateUser.isEnabled = false
                }
            }
        }

        viewModel.createExpenseMemberLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Участник успешно добавлен!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.deleteExpenseMemberLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Участник успешно удален!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.updateExpenseMemberLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Участник успешно обновлен!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.updateExpenseLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    state.data?.let { updatedExpense ->
                        currentExpense = updatedExpense
                        currentExpenseAmount = updatedExpense.amount
                        mBinding.expenseName.text = updatedExpense.name
                        mBinding.expensePrice.text =
                            DecimalFormat("#,##0.00").format(updatedExpense.amount)

                        val membersCount = expenseMemberAdapter.differ.currentList.size
                        val size = if (membersCount == 0) 1 else membersCount
                        val amountByOnePerson =
                            currentExpenseAmount.divide(BigDecimal(size), 2, RoundingMode.HALF_UP)
                        expenseMemberAdapter.setAmountByOnePerson(amountByOnePerson)
                    }
                    Toast.makeText(
                        context,
                        "Покупка успешно обновлена!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        state.message ?: "Неизвестная ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initAdapter() {
        expenseMemberAdapter = ExpenseMemberAdapter()
        mBinding.membersRecyclerView.apply {
            adapter = expenseMemberAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        expenseMemberAdapter.setOnItemLongClickListener { expenseMember ->
            showExpenseMemberContextMenu(expenseMember)
        }
    }

    private fun initUsersAdapter() {
        usersAdapter = UsersAdapter()
        mBinding.usersRecyclerView.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
                    val action =
                        DetailsExpenseFragmentDirections.actionDetailsExpenseFragmentToEditExpenseFragment(
                            currentExpense,
                            bundleArgs.receiptId
                        )
                    findNavController().navigate(action)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showExpenseMemberContextMenu(expenseMember: ExpenseMember) {
        val position = expenseMemberAdapter.differ.currentList.indexOf(expenseMember)
        if (position == -1) return

        val viewHolder = mBinding.membersRecyclerView.findViewHolderForAdapterPosition(position)

        if (viewHolder != null) {
            val itemView = viewHolder.itemView
            val popup = PopupMenu(requireContext(), itemView, Gravity.BOTTOM)
            popup.menuInflater.inflate(R.menu.details_event_menu, popup.menu)

            popup.setForceShowIcon(true)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        mBinding.usersOverlay.visibility = View.VISIBLE
                        mBinding.addMemberButton.visibility = View.GONE
                        mBinding.userSearch.setText("")
                        usersAdapter.differ.submitList(allUsers)
                        currentEditingExpenseMemberId = expenseMember.id
                        currentEditingExpenseMemberUserId = expenseMember.user.id
                        true
                    }

                    R.id.menu_delete -> {
                        viewModel.deleteExpenseMember(expenseMember.id)
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление покупки")
            .setMessage("Вы точно уверены, что хотите удалить эту покупку? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteExpense()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}