package com.friney.fairsplit.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.friney.fairsplit.data.utility.AuthState
import com.friney.fairsplit.databinding.FragmentRegisterBinding
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentNavigator.setNavController(findNavController())

        resetRegisterState()

        setupViews()
        observeViewModel()
    }

    private fun resetRegisterState() {
        viewModel.resetRegisterState()
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }

    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    viewModel.register(name, email, password, confirmPassword)
                }
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            viewModel.resetRegisterState()
            fragmentNavigator.navigateToLogin()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.btnRegister.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is AuthState.Success -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Регистрация успешна! Вы автоматически вошли в систему",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetRegisterState()
                        fragmentNavigator.navigateToMain()
                    }

                    is AuthState.Error -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
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