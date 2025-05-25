package com.friney.fairsplit.ui.details.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friney.fairsplit.databinding.FragmentSummaryTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryTabFragment : Fragment() {

    private var _binding: FragmentSummaryTabBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryTabBinding.inflate(inflater, container, false)
        return mBinding.root
    }
} 