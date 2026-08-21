package com.friney.fairsplit.ui.edit.receipt

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentEditReceiptBinding
import com.friney.fairsplit.network.model.user.User
import com.friney.fairsplit.ui.adapter.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReceiptFragment : Fragment() {

    private var _binding: FragmentEditReceiptBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: EditReceiptViewModel by viewModels()
    private val args: EditReceiptFragmentArgs by navArgs()
    private lateinit var usersAdapter: UsersAdapter
    private var allUsers: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReceiptBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receipt = args.receipt
        viewModel.selectUser(receipt.paidByUser.id)
        mBinding.nameInput.setText(receipt.name)
        mBinding.userInput.setText(receipt.paidByUser.displayName)

        usersAdapter = UsersAdapter()
        mBinding.usersRecyclerView.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getAllUsers()

        mBinding.backButton.setOnClickListener { findNavController().navigateUp() }

        mBinding.userInput.setOnClickListener {
            mBinding.usersOverlay.visibility = View.VISIBLE
            mBinding.createButton.visibility = View.GONE
            mBinding.userSearch.setText("")
            usersAdapter.differ.submitList(allUsers)
        }

        mBinding.closeUsersOverlay.setOnClickListener {
            mBinding.usersOverlay.visibility = View.GONE
            mBinding.createButton.visibility = View.VISIBLE
        }

        mBinding.addUserButton.setOnClickListener {
            mBinding.createUserDialog.visibility = View.VISIBLE
            mBinding.newUserNameInput.setText("")
        }

        mBinding.cancelCreateUser.setOnClickListener {
            mBinding.createUserDialog.visibility = View.GONE
        }

        mBinding.confirmCreateUser.setOnClickListener {
            val userName = mBinding.newUserNameInput.text.toString().trim()
            if (userName.isBlank()) {
                Toast.makeText(context, "Введите имя пользователя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.createUser(userName)
        }

        mBinding.userSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                val filtered = allUsers.filter { it.displayName.lowercase().contains(query) }
                usersAdapter.differ.submitList(filtered)
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        usersAdapter.setOnItemClickListener { user ->
            mBinding.userInput.setText(user.displayName)
            viewModel.selectUser(user.id)
            mBinding.usersOverlay.visibility = View.GONE
            mBinding.createButton.visibility = View.VISIBLE
        }

        mBinding.createButton.setOnClickListener {
            val name = mBinding.nameInput.text.toString().trim()
            viewModel.updateReceipt(receipt.id, args.eventId, name)
        }

        viewModel.allUsersState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    allUsers = state.data ?: emptyList()
                    usersAdapter.differ.submitList(allUsers)
                    val currentUser = allUsers.firstOrNull { it.id == receipt.paidByUser.id }
                    if (currentUser != null) {
                        mBinding.userInput.setText(currentUser.displayName)
                    }
                }

                is DataState.Error -> {
                    Toast.makeText(
                        context,
                        state.message ?: "Не удалось загрузить пользователей",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> Unit
            }
        }

        viewModel.updateReceiptState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    mBinding.createButton.isEnabled = true
                    Toast.makeText(context, "Чек успешно обновлён!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
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
                    mBinding.createButton.isEnabled = false
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.createUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.createUserProgressBar.visibility = View.INVISIBLE
                    mBinding.confirmCreateUser.isEnabled = true
                    mBinding.createUserDialog.visibility = View.GONE
                    mBinding.userInput.setText(state.data?.displayName ?: "")
                    state.data?.id?.let { viewModel.selectUser(it) }
                    viewModel.getAllUsers()
                    Toast.makeText(context, "Пользователь успешно создан!", Toast.LENGTH_SHORT)
                        .show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
