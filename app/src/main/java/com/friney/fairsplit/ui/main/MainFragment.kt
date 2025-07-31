package com.friney.fairsplit.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentMainBinding
import com.friney.fairsplit.ui.adapter.EventAdapter
import com.friney.fairsplit.ui.auth.AuthViewModel
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentMainBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
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

        val isLogin = authViewModel.isLoggedIn()
        Log.i("isLogin", isLogin.toString())
        if (!isLogin) {
            fragmentNavigator.navigateToLogin()
            return
        }

        initAdapter()

        eventAdapter.setOnItemClickListener {
            val bundle = bundleOf("event" to it)
            fragmentNavigator.navigateMainToDetailsEvent(bundle)
        }

        mBinding.addEventButton.setOnClickListener {
            fragmentNavigator.navigateMainToCreateEvent()
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