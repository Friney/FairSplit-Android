package com.friney.fairsplit.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentSettingsBinding
import com.friney.fairsplit.ui.auth.AuthViewModel
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentSettingsBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        mBinding.logoutButton.setOnClickListener {
            authViewModel.logout()
            Toast.makeText(context, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            fragmentNavigator.navigateToLogin()
        }
    }

    private fun observeViewModel() {
        viewModel.userInAppLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        _binding?.userName?.text = it.name
                        _binding?.userEmail?.text = it.email
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get current user: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
}