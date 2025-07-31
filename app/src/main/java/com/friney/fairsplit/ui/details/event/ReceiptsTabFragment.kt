package com.friney.fairsplit.ui.details.event

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
import com.friney.fairsplit.databinding.FragmentReceiptsTabBinding
import com.friney.fairsplit.ui.adapter.ReceiptsAdapter
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReceiptsTabFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentReceiptsTabBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: DetailsEventViewModel by viewModels(ownerProducer = { requireActivity() })
    lateinit var receiptAdapter: ReceiptsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptsTabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        receiptAdapter.setOnItemClickListener {
            val bundle = bundleOf("receipt" to it)
            fragmentNavigator.navigateMainToDetailsReceipt(bundle)
        }

        viewModel.receiptsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        receiptAdapter.differ.submitList(it)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get all receipt: ", response.message)
                    }
                }

                is DataState.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initAdapter() {
        receiptAdapter = ReceiptsAdapter()
        mBinding.receiptRecyclerView.apply {
            adapter = receiptAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
} 