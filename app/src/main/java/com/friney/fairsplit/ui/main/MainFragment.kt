package com.friney.fairsplit.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        viewModel.event.observe(viewLifecycleOwner) { response ->
            eventAdapter.differ.submitList(response)
        }

    }

    private fun initAdapter() {
        val sharedPreferences = context?.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        sharedPreferences?.edit {
            putString(
                "jwt_token",
                "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzQ3NDgwNjUyfQ.xMXgO6lUhAx0bk3KYaMQuHxaGw_-p92xTAv_T0yqXAt02UCSy3uo6rutu_qpjrKrkoBHyA0tQcdhtIQywyTk5w"
            )
        }
        eventAdapter = EventAdapter()
        mBinding.eventRecyclerView.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}