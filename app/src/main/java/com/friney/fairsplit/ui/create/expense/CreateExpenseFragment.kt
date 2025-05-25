package com.friney.fairsplit.ui.create.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friney.fairsplit.databinding.FragmentCreateExpenseBinding

class CreateExpenseFragment : Fragment() {

    private var _binding: FragmentCreateExpenseBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateExpenseBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }
}