package com.friney.fairsplit.ui.edit.event

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
import com.friney.fairsplit.databinding.FragmentEditEventBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditEventFragment : Fragment() {

    private var _binding: FragmentEditEventBinding? = null
    private val mBinding get() = _binding!!
    private val args: EditEventFragmentArgs by navArgs()
    private val viewModel: EditEventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEventBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = args.event
        mBinding.nameInput.setText(event.name)
        mBinding.descriptionInput.setText(event.description)

        mBinding.backButton.setOnClickListener { findNavController().navigateUp() }

        mBinding.createButton.setOnClickListener {
            val name = mBinding.nameInput.text.toString().trim()
            val description = mBinding.descriptionInput.text.toString().trim()
            viewModel.updateEvent(event.id, name, description)
        }

        viewModel.updateEventState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    mBinding.createButton.isEnabled = true
                    Toast.makeText(context, "Событие успешно обновлено!", Toast.LENGTH_SHORT).show()
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
