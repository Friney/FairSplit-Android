package com.friney.fairsplit.ui.details.event

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.friney.fairsplit.R
import com.friney.fairsplit.databinding.FragmentDetailsEventBinding
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsEventFragment : Fragment() {

    @Inject
    lateinit var fragmentNavigator: FragmentNavigator

    private var _binding: FragmentDetailsEventBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: DetailsEventFragmentArgs by navArgs()
    private val viewModel: DetailsEventViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsEventBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

        val eventArg = bundleArgs.event
        eventArg.let { event ->
            mBinding.eventName.text = event.name
            mBinding.eventDescription.text = event.description
            viewModel.init(event.id)
        }

        mBinding.backButton.setOnClickListener {
            fragmentNavigator.navigateBack()
        }

        mBinding.menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

    }

    private fun setupViewPager() {
        val pagerAdapter = DetailsEventPagerAdapter(requireActivity())
        mBinding.viewPager.adapter = pagerAdapter
        val tabTitles = arrayOf("Чеки", "Итог")

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = tabTitles.getOrNull(position)
                ?: throw IllegalArgumentException("Invalid position $position")
        }.attach()

        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.addReceiptButton.visibility = when (position) {
                    1 -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view, Gravity.END)
        popup.menuInflater.inflate(R.menu.details_event_menu, popup.menu)

        popup.setForceShowIcon(true)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit -> {
                    // Действие при редактировании
                    true
                }

                R.id.menu_delete -> {
                    // Действие при удалении
                    true
                }

                else -> false
            }
        }

        popup.show()
    }
}