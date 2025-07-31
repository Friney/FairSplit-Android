package com.friney.fairsplit.ui.create.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentCreateEventBinding
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentCreateEventBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: CreateEventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backButton.setOnClickListener {
            fragmentNavigator.navigateBack()
        }

        mBinding.createButton.setOnClickListener {
            Log.e("Create event", "Create event")
            val name = mBinding.nameInput.text.toString()
            val description = mBinding.descriptionInput.text.toString()
            viewModel.createEvent(name, description)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createEventState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is DataState.Success -> {
                        mBinding.progressBar.visibility = View.INVISIBLE
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
                        Log.e("Create eventLoading", "Create eventLoading")
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