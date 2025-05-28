package com.friney.fairsplit.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.R
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentMainBinding
import com.friney.fairsplit.ui.adapter.EventAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()
    lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        eventAdapter.setOnItemClickListener {
            val bundle = bundleOf("event" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailsEventFragment,
                bundle
            )
        }

        viewModel.eventLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        eventAdapter.differ.submitList(it)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get all event: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun initAdapter() {
        eventAdapter = EventAdapter()
        mBinding.eventRecyclerView.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}