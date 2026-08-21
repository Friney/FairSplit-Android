package com.friney.fairsplit.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.friney.fairsplit.R
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

        fragmentNavigator.setNavController(findNavController())

        setupViews()
        setupScaleSpinner()
        observeViewModel()
    }

    private fun setupViews() {
        mBinding.logoutButton.setOnClickListener {
            authViewModel.logout()
            Toast.makeText(context, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            fragmentNavigator.navigateToLogin()
        }
    }

    private fun setupScaleSpinner() {
        val spinner: Spinner = mBinding.scaleSpinner
        val scaleOptions = listOf(
            getString(R.string.scale_small),
            getString(R.string.scale_normal),
            getString(R.string.scale_large),
            getString(R.string.scale_xlarge)
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, scaleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Получаем сохранённый масштаб
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedScale = prefs.getFloat("font_scale", 1.0f)
        val currentFontScale = resources.configuration.fontScale
        Log.i("currentFontScale", currentFontScale.toString())
        spinner.setSelection(
            when (savedScale) {
                0.85f -> 0
                1.0f -> 1
                1.15f -> 2
                1.3f -> 3
                else -> 1
            }
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val scale = when (position) {
                    0 -> 0.85f // Small
                    1 -> 1.0f  // Normal
                    2 -> 1.15f // Large
                    3 -> 1.3f  // Extra Large
                    else -> 1.0f
                }
                val currentScale = resources.configuration.fontScale
                if (scale != currentScale) {
                    // Сохраняем выбор
                    prefs.edit().putFloat("font_scale", scale).apply()
                    // Применяем масштаб
                    val config = Configuration(resources.configuration)
                    config.fontScale = scale
                    requireActivity().resources.updateConfiguration(
                        config,
                        requireActivity().resources.displayMetrics
                    )
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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