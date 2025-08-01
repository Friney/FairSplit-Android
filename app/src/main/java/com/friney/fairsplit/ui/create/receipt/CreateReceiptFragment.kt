package com.friney.fairsplit.ui.create.receipt

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentCreateReceiptBinding
import com.friney.fairsplit.network.model.user.User
import com.friney.fairsplit.ui.adapter.UsersAdapter
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateReceiptFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentCreateReceiptBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: CreateReceiptViewModel by viewModels()
    private val bundleArgs: CreateReceiptFragmentArgs by navArgs()
    private lateinit var usersAdapter: UsersAdapter
    private var allUsers: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReceiptBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backButton.setOnClickListener {
            fragmentNavigator.navigateBack()
        }

        mBinding.createButton.setOnClickListener {
            Log.e("Create receipt", "Create receipt")
            val name = mBinding.nameInput.text.toString()
            val eventId = bundleArgs.eventId
            viewModel.createReceipt(name, eventId)
        }

        // --- USERS OVERLAY LOGIC ---
        usersAdapter = UsersAdapter()
        mBinding.usersRecyclerView.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

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
            mBinding.userInput.setText(user.displayName)
            viewModel.selectUser(user.id)
            mBinding.usersOverlay.visibility = View.GONE
            mBinding.createButton.visibility = View.VISIBLE
        }

        // --- CREATE USER DIALOG LOGIC ---
        mBinding.cancelCreateUser.setOnClickListener {
            mBinding.createUserDialog.visibility = View.GONE
        }

        mBinding.confirmCreateUser.setOnClickListener {
            val userName = mBinding.newUserNameInput.text.toString()
            viewModel.createUser(userName)
        }
        // --- END CREATE USER DIALOG LOGIC ---
        // --- END USERS OVERLAY LOGIC ---

        // Наблюдатель за текущим пользователем
        viewModel.currentUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.userInput.setText(state.data?.displayName)
                    viewModel.selectUser(state.data?.id ?: 0)
                }

                is DataState.Error -> {
                    Toast.makeText(
                        context,
                        "Ошибка загрузки пользователя: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                    // Можно показать прогресс
                }
            }
        }

        // Наблюдатель за списком пользователей
        viewModel.allUsersState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    allUsers = state.data ?: emptyList()
                    usersAdapter.differ.submitList(allUsers)
                }

                is DataState.Error -> {
                    Toast.makeText(
                        context,
                        "Ошибка загрузки пользователей: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is DataState.Loading -> {
                }
            }
        }

        // Наблюдатель за созданием пользователя
        viewModel.createUserState.observe(viewLifecycleOwner) { state ->
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

        // Наблюдатель за созданием чека
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createReceiptState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is DataState.Success -> {
                        mBinding.progressBar.visibility = View.INVISIBLE
                        mBinding.createButton.isEnabled = true
                        Toast.makeText(
                            context,
                            "Чек успешно создан!",
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
                        Log.e("Create receiptLoading", "Create receiptLoading")
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