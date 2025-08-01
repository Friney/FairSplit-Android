package com.friney.fairsplit.ui.details.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.friney.fairsplit.data.utility.DataState
import com.friney.fairsplit.databinding.FragmentSummaryTabBinding
import com.friney.fairsplit.ui.adapter.DebtsAdapter
import com.friney.fairsplit.ui.adapter.ReceiptsSummaryAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SummaryTabFragment : Fragment() {

    private var _binding: FragmentSummaryTabBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: DetailsEventViewModel by viewModels()
    lateinit var debtsAdapter: DebtsAdapter
    lateinit var receiptsAdapter: ReceiptsSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryTabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeSummary()
    }

    private fun initAdapter() {
        debtsAdapter = DebtsAdapter()
        receiptsAdapter = ReceiptsSummaryAdapter()

        mBinding.debtsRecyclerView.apply {
            adapter = debtsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        mBinding.receiptsRecyclerView.apply {
            adapter = receiptsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun observeSummary() {
        viewModel.summaryLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DataState.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let { summary ->
                        mBinding.totalAmount.text =
                            String.format(Locale.getDefault(), "%.2f", summary.total)
                        debtsAdapter.differ.submitList(summary.debts)
                        receiptsAdapter.differ.submitList(summary.receipts)
                    }
                }

                is DataState.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.message?.let {
                        Log.e("Error get summary: ", response.message)
                    }
                }

                is DataState.Loading -> {
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